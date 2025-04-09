package com.buzzsocial.repository;

import com.buzzsocial.model.BuzzReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuzzReportRepository extends JpaRepository<BuzzReport, String> {
    boolean existsByUserIdAndBuzzId(String userId, String buzzId);
    
    List<BuzzReport> findByStatus(BuzzReport.ReportStatus status);
    
    long countByStatus(BuzzReport.ReportStatus status);
    
    List<BuzzReport> findByReasonContainingIgnoreCase(String query);
}
