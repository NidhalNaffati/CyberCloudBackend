package com.example.gestion_forum.services;

import com.example.gestion_forum.models.Report;
import com.example.gestion_forum.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    public Report reportPost(Report report) {
        return reportRepository.save(report);
    }

    public List<Report> getReportsForPost(Long postId) {
        return reportRepository.findByPostId(postId);
    }
}
