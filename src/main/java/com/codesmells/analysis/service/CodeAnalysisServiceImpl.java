package com.codesmells.analysis.service;

import com.codesmells.analysis.dto.AnalysisHistoryDto;
import com.codesmells.analysis.dto.AnalysisRequestDto;
import com.codesmells.analysis.dto.AnalysisResponseDto;
import com.codesmells.analysis.dto.FunctionMetricDto;
import com.codesmells.analysis.dto.LocMetricsDto;
import com.codesmells.analysis.dto.SmellDto;
import com.codesmells.analysis.model.AnalysisReport;
import com.codesmells.analysis.repository.AnalysisReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CodeAnalysisServiceImpl implements CodeAnalysisService {

    private static final Pattern JAVA_METHOD_PATTERN = Pattern.compile(
            "^\\s*(?:(?:public|private|protected|static|final|synchronized|abstract|native|strictfp)\\s+)*[\\w<>\\[\\], ?]+\\s+(?<name>[A-Za-z_][A-Za-z0-9_]*)\\s*\\((?<params>[^)]*)\\)\\s*(?:\\{|$)");
    private static final Pattern PYTHON_METHOD_PATTERN = Pattern.compile(
            "^\\s*def\\s+(?<name>[A-Za-z_][A-Za-z0-9_]*)\\s*\\((?<params>[^)]*)\\)\\s*:");
    private static final Pattern PARAMETER_PATTERN = Pattern.compile(",");
    private static final Pattern DECISION_PATTERN = Pattern.compile("\\b(if|for|while|switch|case|catch|elif|except)\\b|\\?|&&|\\|\\|");
    private static final int LONG_METHOD_THRESHOLD = 30;
    private static final int LARGE_CLASS_THRESHOLD = 300;
    private static final int MANY_PARAMETERS_THRESHOLD = 5;

    private final AnalysisReportRepository analysisReportRepository;

    public CodeAnalysisServiceImpl(AnalysisReportRepository analysisReportRepository) {
        this.analysisReportRepository = analysisReportRepository;
    }

    @Override
    public AnalysisResponseDto analyze(String code, MultipartFile file, String language) throws IOException {
        return analyzeInternal(resolveSource(code, file), resolveLanguage(language, file));
    }

    @Override
    public AnalysisResponseDto analyze(AnalysisRequestDto request) throws IOException {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required.");
        }
        return analyzeInternal(resolveSource(request.getCode(), null), resolveLanguage(request.getLanguage(), null));
    }

    @Override
    public List<AnalysisHistoryDto> getHistory() {
        return analysisReportRepository.findAllByOrderByDateDesc().stream()
                .map(report -> new AnalysisHistoryDto(
                        report.getId(),
                        preview(report.getCodeSnippet()),
                        report.getScore(),
                        report.getDate()))
                .toList();
    }

    private String resolveSource(String code, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        }
        if (StringUtils.hasText(code)) {
            return code;
        }
        throw new IllegalArgumentException("Provide either code text or a source file.");
    }

    private AnalysisResponseDto analyzeInternal(String source, String resolvedLanguage) {
        List<String> lines = Arrays.asList(source.split("\\R", -1));

        List<FunctionMetricDto> functions = extractFunctions(lines, resolvedLanguage);
        int decisionPoints = countDecisionPoints(lines);
        int complexity = decisionPoints + 1;

        LocMetricsDto loc = calculateLoc(lines, resolvedLanguage);
        List<SmellDto> smells = detectSmells(lines, functions, loc.getTotal());
        double maintainabilityScore = calculateMaintainability(complexity, loc, smells.size(), functions.size());
        List<String> suggestions = buildSuggestions(smells, maintainabilityScore);

        AnalysisResponseDto response = new AnalysisResponseDto();
        response.setComplexity(complexity);
        response.setLoc(loc);
        response.setMaintainabilityScore(maintainabilityScore);
        response.setSmells(smells);
        response.setSuggestions(suggestions);
        response.setFunctions(functions);

        AnalysisReport report = new AnalysisReport();
        report.setCodeSnippet(source);
        report.setScore(maintainabilityScore);
        report.setDate(LocalDateTime.now());
        analysisReportRepository.save(report);

        return response;
    }

    private String resolveLanguage(String language, MultipartFile file) {
        if (StringUtils.hasText(language)) {
            return language.trim().toLowerCase(Locale.ROOT);
        }
        if (file != null && StringUtils.hasText(file.getOriginalFilename())) {
            String filename = file.getOriginalFilename().toLowerCase(Locale.ROOT);
            if (filename.endsWith(".py")) {
                return "python";
            }
            if (filename.endsWith(".cpp") || filename.endsWith(".cc") || filename.endsWith(".cxx") || filename.endsWith(".c")) {
                return "cpp";
            }
        }
        return "java";
    }

    private LocMetricsDto calculateLoc(List<String> lines, String language) {
        int blankLines = 0;
        int commentLines = 0;
        boolean blockComment = false;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                blankLines++;
                continue;
            }

            CommentState commentState = classifyCommentLine(trimmed, language, blockComment);
            blockComment = commentState.blockComment();
            if (commentState.commentLine()) {
                commentLines++;
            }
        }

        LocMetricsDto loc = new LocMetricsDto();
        loc.setTotal(lines.size());
        loc.setComments(commentLines);
        loc.setBlanks(blankLines);
        loc.setCommentRatio(lines.isEmpty() ? 0.0 : ((double) commentLines / (double) lines.size()) * 100.0);
        return loc;
    }

    private CommentState classifyCommentLine(String trimmed, String language, boolean blockComment) {
        if (blockComment) {
            if (trimmed.contains("*/") || trimmed.contains("'''") || trimmed.contains("\"\"\"")) {
                return new CommentState(true, false);
            }
            return new CommentState(true, true);
        }

        if (trimmed.startsWith("//") || trimmed.startsWith("#")) {
            return new CommentState(true, false);
        }

        if (trimmed.startsWith("/*") || trimmed.startsWith("'''") || trimmed.startsWith("\"\"\"")) {
            boolean closesSameLine = trimmed.contains("*/") || countOccurrences(trimmed, "'''") > 1 || countOccurrences(trimmed, "\"\"\"") > 1;
            return new CommentState(true, !closesSameLine);
        }

        if ("python".equals(language) && trimmed.startsWith("\"\"\"") && trimmed.endsWith("\"\"\"") && trimmed.length() > 6) {
            return new CommentState(true, false);
        }

        return new CommentState(false, false);
    }

    private int countDecisionPoints(List<String> lines) {
        int count = 0;
        for (String line : lines) {
            String sanitized = sanitize(line);
            if (sanitized.isEmpty()) {
                continue;
            }
            Matcher matcher = DECISION_PATTERN.matcher(sanitized);
            while (matcher.find()) {
                count++;
            }
        }
        return count;
    }

    private List<FunctionMetricDto> extractFunctions(List<String> lines, String language) {
        if ("python".equals(language)) {
            return extractPythonFunctions(lines);
        }
        return extractBracketFunctions(lines);
    }

    private List<FunctionMetricDto> extractBracketFunctions(List<String> lines) {
        List<FunctionMetricDto> functions = new ArrayList<>();

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            Matcher matcher = JAVA_METHOD_PATTERN.matcher(line);
            if (!matcher.find() || isControlFlowHeader(line)) {
                continue;
            }

            int parameterCount = countParameters(matcher.group("params"));
            int startLine = index + 1;
            int bodyStartIndex = findBodyStartIndex(lines, index);
            if (bodyStartIndex < 0) {
                continue;
            }

            int endIndex = findBracketBodyEnd(lines, bodyStartIndex);
            int endLine = endIndex + 1;
            int loc = Math.max(1, endLine - startLine + 1);
            int complexity = countDecisionPoints(lines.subList(index, Math.min(endIndex + 1, lines.size()))) + 1;

            functions.add(new FunctionMetricDto(
                    matcher.group("name"),
                    startLine,
                    endLine,
                    loc,
                    complexity,
                    parameterCount));
        }

        return functions;
    }

    private List<FunctionMetricDto> extractPythonFunctions(List<String> lines) {
        List<FunctionMetricDto> functions = new ArrayList<>();

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            Matcher matcher = PYTHON_METHOD_PATTERN.matcher(line);
            if (!matcher.find()) {
                continue;
            }

            int parameterCount = countParameters(matcher.group("params"));
            int startLine = index + 1;
            int indent = indentationLevel(line);
            int endIndex = findPythonBodyEnd(lines, index + 1, indent);
            int endLine = Math.max(startLine, endIndex);
            int loc = Math.max(1, endLine - startLine + 1);
            int complexity = countDecisionPoints(lines.subList(index, Math.min(endLine, lines.size()))) + 1;

            functions.add(new FunctionMetricDto(
                    matcher.group("name"),
                    startLine,
                    endLine,
                    loc,
                    complexity,
                    parameterCount));
        }

        return functions;
    }

    private boolean isControlFlowHeader(String line) {
        String trimmed = line.trim();
        return trimmed.startsWith("if ") || trimmed.startsWith("if(") || trimmed.startsWith("for ") || trimmed.startsWith("for(")
                || trimmed.startsWith("while ") || trimmed.startsWith("while(") || trimmed.startsWith("switch ") || trimmed.startsWith("catch ");
    }

    private int countParameters(String params) {
        if (!StringUtils.hasText(params)) {
            return 0;
        }
        String trimmed = params.trim();
        if (trimmed.isEmpty()) {
            return 0;
        }
        return (int) PARAMETER_PATTERN.splitAsStream(trimmed).count();
    }

    private int findBodyStartIndex(List<String> lines, int signatureIndex) {
        String signatureLine = lines.get(signatureIndex);
        if (signatureLine.contains("{")) {
            return signatureIndex;
        }
        for (int i = signatureIndex + 1; i < lines.size(); i++) {
            if (lines.get(i).contains("{")) {
                return i;
            }
            if (!lines.get(i).trim().isEmpty()) {
                break;
            }
        }
        return -1;
    }

    private int findBracketBodyEnd(List<String> lines, int bodyStartIndex) {
        int depth = 0;
        boolean started = false;
        for (int i = bodyStartIndex; i < lines.size(); i++) {
            String line = lines.get(i);
            depth += countOccurrences(line, "{");
            depth -= countOccurrences(line, "}");
            if (line.contains("{")) {
                started = true;
            }
            if (started && depth <= 0) {
                return i;
            }
        }
        return lines.size() - 1;
    }

    private int findPythonBodyEnd(List<String> lines, int startIndex, int indent) {
        int endIndex = startIndex - 1;
        for (int i = startIndex; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                endIndex = i;
                continue;
            }
            int currentIndent = indentationLevel(line);
            if (currentIndent <= indent && line.trim().length() > 0) {
                break;
            }
            endIndex = i;
        }
        return endIndex + 1;
    }

    private List<SmellDto> detectSmells(List<String> lines, List<FunctionMetricDto> functions, int totalLines) {
        List<SmellDto> smells = new ArrayList<>();

        for (FunctionMetricDto function : functions) {
            if (function.getLinesOfCode() > LONG_METHOD_THRESHOLD) {
                smells.add(new SmellDto(
                        "Long Method",
                        "Function '" + function.getName() + "' spans " + function.getLinesOfCode() + " lines.",
                        function.getLineNumber(),
                        function.getEndLineNumber(),
                        "High"));
            }

            if (function.getParameters() > MANY_PARAMETERS_THRESHOLD) {
                smells.add(new SmellDto(
                        "Too Many Parameters",
                        "Function '" + function.getName() + "' has " + function.getParameters() + " parameters.",
                        function.getLineNumber(),
                        function.getEndLineNumber(),
                        "Medium"));
            }
        }

        if (totalLines > LARGE_CLASS_THRESHOLD) {
            smells.add(new SmellDto(
                    "Large Class",
                    "This file contains " + totalLines + " lines which exceeds the large class threshold.",
                    1,
                    totalLines,
                    "Medium"));
        }

        smells.addAll(findDuplicatePatterns(lines));
        return smells;
    }

    private List<SmellDto> findDuplicatePatterns(List<String> lines) {
        Map<String, Integer> firstOccurrence = new LinkedHashMap<>();
        List<SmellDto> smells = new ArrayList<>();
        List<NormalizedLine> significantLines = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String normalized = normalizeForDuplication(lines.get(i));
            if (!normalized.isEmpty()) {
                significantLines.add(new NormalizedLine(i + 1, normalized));
            }
        }

        int windowSize = 4;
        for (int i = 0; i <= significantLines.size() - windowSize; i++) {
            String signature = buildWindowSignature(significantLines, i, windowSize);
            Integer firstLine = firstOccurrence.putIfAbsent(signature, significantLines.get(i).lineNumber());
            if (firstLine != null) {
                smells.add(new SmellDto(
                        "Duplicate Code",
                        "Repeated code pattern detected starting at lines " + firstLine + " and " + significantLines.get(i).lineNumber() + ".",
                        significantLines.get(i).lineNumber(),
                        significantLines.get(i + windowSize - 1).lineNumber(),
                        "Medium"));
                break;
            }
        }

        return smells;
    }

    private String buildWindowSignature(List<NormalizedLine> lines, int start, int windowSize) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < start + windowSize; i++) {
            builder.append(lines.get(i).normalized()).append('|');
        }
        return builder.toString();
    }

    private String normalizeForDuplication(String line) {
        String normalized = sanitize(line).trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
        return normalized.replaceAll("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b", "var");
    }

    private double calculateMaintainability(int complexity, LocMetricsDto loc, int smellCount, int functionCount) {
        double score = 100.0;
        score -= complexity * 2.5;
        score -= loc.getTotal() / 12.0;
        score -= smellCount * 7.5;
        score += Math.min(12.0, loc.getCommentRatio() / 8.0);
        score += Math.min(5.0, functionCount * 0.5);
        return Math.max(0.0, Math.min(100.0, Math.round(score * 10.0) / 10.0));
    }

    private List<String> buildSuggestions(List<SmellDto> smells, double maintainabilityScore) {
        List<String> suggestions = new ArrayList<>();

        if (smells.isEmpty()) {
            suggestions.add("No major smell patterns were detected. Keep the current structure and add tests around the critical paths.");
        }

        boolean hasLongMethod = smells.stream().anyMatch(smell -> "Long Method".equals(smell.getType()));
        boolean hasLargeClass = smells.stream().anyMatch(smell -> "Large Class".equals(smell.getType()));
        boolean hasManyParameters = smells.stream().anyMatch(smell -> "Too Many Parameters".equals(smell.getType()));
        boolean hasDuplicateCode = smells.stream().anyMatch(smell -> "Duplicate Code".equals(smell.getType()));

        if (hasLongMethod) {
            suggestions.add("Split long methods into smaller helpers with a single responsibility.");
        }
        if (hasLargeClass) {
            suggestions.add("Extract cohesive responsibilities into separate classes or services.");
        }
        if (hasManyParameters) {
            suggestions.add("Bundle related arguments into a request object or DTO.");
        }
        if (hasDuplicateCode) {
            suggestions.add("Extract repeated logic into a shared function or utility method.");
        }
        if (maintainabilityScore < 60) {
            suggestions.add("Refactor the highest-complexity functions first, then add tests before making further changes.");
        }
        if (suggestions.isEmpty()) {
            suggestions.add("The code looks reasonably healthy, but a test suite would help validate future edits.");
        }

        return suggestions;
    }

    private String sanitize(String line) {
        String withoutStrings = line.replaceAll("\"([^\"\\\\]|\\\\.)*\"", "\"\"")
                .replaceAll("'([^'\\\\]|\\\\.)*'", "''");
        return withoutStrings.replaceAll("//.*$", "")
                .replaceAll("#.*$", "")
                .replaceAll("/\\*.*?\\*/", "")
                .trim();
    }

    private int indentationLevel(String line) {
        int count = 0;
        for (char ch : line.toCharArray()) {
            if (ch == ' ' || ch == '\t') {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private int countOccurrences(String text, String token) {
        int index = 0;
        int count = 0;
        while ((index = text.indexOf(token, index)) >= 0) {
            count++;
            index += token.length();
        }
        return count;
    }

    private String preview(String codeSnippet) {
        if (!StringUtils.hasText(codeSnippet)) {
            return "";
        }
        String compact = codeSnippet.replaceAll("\\s+", " ").trim();
        return compact.length() <= 180 ? compact : compact.substring(0, 180) + "...";
    }

    private record CommentState(boolean commentLine, boolean blockComment) {
    }

    private record NormalizedLine(int lineNumber, String normalized) {
    }
}