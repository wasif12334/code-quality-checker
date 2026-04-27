package com.codesmells.analysis.service;

import com.codesmells.analysis.dto.AnalysisHistoryDto;
import com.codesmells.analysis.dto.AnalysisRequestDto;
import com.codesmells.analysis.dto.AnalysisResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CodeAnalysisService {

    AnalysisResponseDto analyze(String code, MultipartFile file, String language) throws IOException;

    AnalysisResponseDto analyze(AnalysisRequestDto request) throws IOException;

    List<AnalysisHistoryDto> getHistory();
}