package com.example.gestion_forum.controllers;

import com.example.gestion_forum.models.Report;
import com.example.gestion_forum.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Report> reportPost(@RequestBody Report report) {
        Report savedReport = reportService.reportPost(report);
        return ResponseEntity.ok(savedReport);
    }

    @GetMapping("/{postId}")
    public List<Report> getReportsForPost(@PathVariable Long postId) {
        return reportService.getReportsForPost(postId);
    }
}
