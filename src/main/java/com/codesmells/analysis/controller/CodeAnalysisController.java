package com.codesmells.analysis.controller;

import com.codesmells.analysis.dto.AnalysisHistoryDto;
import com.codesmells.analysis.dto.AnalysisRequestDto;
import com.codesmells.analysis.dto.AnalysisResponseDto;
import com.codesmells.analysis.service.CodeAnalysisService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CodeAnalysisController {

    private final CodeAnalysisService codeAnalysisService;

    public CodeAnalysisController(CodeAnalysisService codeAnalysisService) {
        this.codeAnalysisService = codeAnalysisService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnalysisResponseDto> analyzeCode(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String language) throws IOException {

        AnalysisResponseDto response = codeAnalysisService.analyze(code, file, language);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/analyze", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnalysisResponseDto> analyzeCode(@RequestBody AnalysisRequestDto request) throws IOException {
        AnalysisResponseDto response = codeAnalysisService.analyze(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<AnalysisHistoryDto>> getHistory() {
        return ResponseEntity.ok(codeAnalysisService.getHistory());
    }
}