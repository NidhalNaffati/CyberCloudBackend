package tn.esprit.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.service.IStatisticsService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("statistics")
public class StatisticsController {

    @Autowired
    private IStatisticsService statisticsService;

    /**
     * Récupère le nombre de publications par mois pour une période donnée
     * @param startDate Date de début (optionnelle)
     * @param endDate Date de fin (optionnelle)
     * @return Map avec les mois comme clés et le nombre de publications comme valeurs
     */
    @GetMapping("posts/monthly")
    public Map<String, Long> getPostsCountByMonth(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return statisticsService.getPostsCountByMonth(startDate, endDate);
    }

    /**
     * Récupère le nombre de publications par jour pour une période donnée
     * @param startDate Date de début (optionnelle)
     * @param endDate Date de fin (optionnelle)
     * @return Map avec les jours comme clés et le nombre de publications comme valeurs
     */
    @GetMapping("posts/daily")
    public Map<String, Long> getPostsCountByDay(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return statisticsService.getPostsCountByDay(startDate, endDate);
    }

    /**
     * Récupère le nombre de publications par année pour une période donnée
     * @param startDate Date de début (optionnelle)
     * @param endDate Date de fin (optionnelle)
     * @return Map avec les années comme clés et le nombre de publications comme valeurs
     */
    @GetMapping("posts/yearly")
    public Map<String, Long> getPostsCountByYear(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return statisticsService.getPostsCountByYear(startDate, endDate);
    }

    /**
     * Récupère la distribution des réactions sur les publications
     * @return Map avec les types de réactions comme clés et leur nombre comme valeurs
     */
    @GetMapping("reactions/distribution")
    public Map<String, Long> getReactionsDistribution() {
        return statisticsService.getReactionsDistribution();
    }

    /**
     * Récupère le nombre de commentaires par mois pour une période donnée
     * @param startDate Date de début (optionnelle)
     * @param endDate Date de fin (optionnelle)
     * @return Map avec les mois comme clés et le nombre de commentaires comme valeurs
     */
    @GetMapping("comments/monthly")
    public Map<String, Long> getCommentsCountByMonth(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return statisticsService.getCommentsCountByMonth(startDate, endDate);
    }

    /**
     * Récupère le nombre de commentaires par jour pour une période donnée
     * @param startDate Date de début (optionnelle)
     * @param endDate Date de fin (optionnelle)
     * @return Map avec les jours comme clés et le nombre de commentaires comme valeurs
     */
    @GetMapping("comments/daily")
    public Map<String, Long> getCommentsCountByDay(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return statisticsService.getCommentsCountByDay(startDate, endDate);
    }

    /**
     * Récupère le nombre de commentaires par année pour une période donnée
     * @param startDate Date de début (optionnelle)
     * @param endDate Date de fin (optionnelle)
     * @return Map avec les années comme clés et le nombre de commentaires comme valeurs
     */
    @GetMapping("comments/yearly")
    public Map<String, Long> getCommentsCountByYear(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return statisticsService.getCommentsCountByYear(startDate, endDate);
    }

    /**
     * Récupère les publications les plus commentées
     * @param limit Nombre de publications à récupérer (par défaut 5)
     * @return Map avec les IDs des publications comme clés et le nombre de commentaires comme valeurs
     */
    @GetMapping("posts/most-commented")
    public Map<Long, Integer> getMostCommentedPosts(@RequestParam(defaultValue = "5") int limit) {
        return statisticsService.getMostCommentedPosts(limit);
    }

    /**
     * Récupère l'activité des utilisateurs (nombre de publications et commentaires)
     * @param limit Nombre d'utilisateurs à récupérer (par défaut 10)
     * @return Map avec les IDs des utilisateurs comme clés et leur activité comme valeurs
     */
    @GetMapping("users/activity")
    public Map<Long, Map<String, Integer>> getUsersActivity(@RequestParam(defaultValue = "10") int limit) {
        return statisticsService.getUsersActivity(limit);
    }

    @GetMapping("activities/general")
    public ResponseEntity<Map<String, Object>> getActivityStatistics() {
        return ResponseEntity.ok(statisticsService.getActivityStatistics());
    }

    @GetMapping("activities/locations")
    public ResponseEntity<Object> getLocationStatistics() {
        return ResponseEntity.ok(statisticsService.getLocationStatistics());
    }

    @GetMapping("activities/monthly")
    public ResponseEntity<Object> getMonthlyActivityStatistics() {
        return ResponseEntity.ok(statisticsService.getMonthlyActivityStatistics());
    }

    @GetMapping("activities/{activityId}/metrics")
    public ResponseEntity<Map<String, Long>> getActivityMetrics(
            @PathVariable Long activityId) {
        return ResponseEntity.ok(statisticsService.getActivityMetrics(activityId));
    }
    @Operation(summary = "Get rating distribution of complaints")
    @GetMapping("/ratings")
    public ResponseEntity<Map<Integer, Long>> getRatingDistribution() {
        return ResponseEntity.ok(statisticsService.getRatingDistribution());
    }

    @Operation(summary = "Get read/unread status of complaints")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Long>> getComplaintStatusStatistics() {
        return ResponseEntity.ok(statisticsService.getStatusStatistics());
    }

    @Operation(summary = "Get urgency status of complaints")
    @GetMapping("/urgency")
    public ResponseEntity<Map<String, Long>> getUrgencyStatistics() {
        return ResponseEntity.ok(statisticsService.getUrgencyStatistics());
    }

    @Operation(summary = "Get total number of complaints")
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalComplaintsCount() {
        return ResponseEntity.ok(statisticsService.getTotalComplaintsCount());
    }

    @Operation(summary = "Get all complaints statistics")
    @GetMapping("/all-stats")
    public ResponseEntity<Map<String, Object>> getAllComplaintStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", statisticsService.getTotalComplaintsCount());
        stats.put("ratings", statisticsService.getRatingDistribution());
        stats.put("status", statisticsService.getStatusStatistics());
        stats.put("urgency", statisticsService.getUrgencyStatistics());
        return ResponseEntity.ok(stats);
    }
}