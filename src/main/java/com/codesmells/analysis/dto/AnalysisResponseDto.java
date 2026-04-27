package com.codesmells.analysis.dto;

import java.util.ArrayList;
import java.util.List;

public class AnalysisResponseDto {

    private int complexity;
    private LocMetricsDto loc;
    private double maintainabilityScore;
    private List<SmellDto> smells = new ArrayList<>();
    private List<String> suggestions = new ArrayList<>();
    private List<FunctionMetricDto> functions = new ArrayList<>();

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public LocMetricsDto getLoc() {
        return loc;
    }

    public void setLoc(LocMetricsDto loc) {
        this.loc = loc;
    }

    public double getMaintainabilityScore() {
        return maintainabilityScore;
    }

    public void setMaintainabilityScore(double maintainabilityScore) {
        this.maintainabilityScore = maintainabilityScore;
    }

    public List<SmellDto> getSmells() {
        return smells;
    }

    public void setSmells(List<SmellDto> smells) {
        this.smells = smells;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public List<FunctionMetricDto> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionMetricDto> functions) {
        this.functions = functions;
    }
}