package com.codesmells.analysis.repository;

import com.codesmells.analysis.model.AnalysisReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, Long> {
    List<AnalysisReport> findAllByOrderByDateDesc();
}