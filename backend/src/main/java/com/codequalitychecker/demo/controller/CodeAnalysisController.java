package com.codequalitychecker.demo.controller;

import com.codequalitychecker.demo.model.CodeRequest;
import com.codequalitychecker.demo.service.AIAnalysisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class CodeAnalysisController {

    private final AIAnalysisService service;

    public CodeAnalysisController(AIAnalysisService service) {
        this.service = service;
    }

    @PostMapping("/analyze")
    public String analyze(@RequestBody CodeRequest request) {
        return service.analyzeCode(request.getCode());
    }
}