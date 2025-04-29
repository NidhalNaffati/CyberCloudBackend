package tn.esprit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.entity.BlogComment;
import tn.esprit.entity.BlogPost;
import tn.esprit.entity.Reaction;
import tn.esprit.repository.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService implements IStatisticsService {

    @Autowired
    private BlogPostRepository blogPostRepository;
    @Autowired
    private IStatisticsRepository statisticsRepository;
    @Autowired
    private BlogCommentRepository blogCommentRepository;
    @Autowired
    private StatisticsBlogRepository statisticsBlogRepository;

    @Autowired
    private StatisticsCommentsRepository statisticsCommentsRepository;

    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);

    @Override
    public Map<String, Long> getPostsCountByMonth(Date startDate, Date endDate) {
        List<Object[]> results = statisticsBlogRepository.countPostsByMonth(startDate, endDate);

        Map<String, Long> postsPerMonth = new LinkedHashMap<>();
        for (Object[] result : results) {
            int month = (Integer) result[0];
            int year = (Integer) result[1];
            Long count = ((Number) result[2]).longValue();
            String monthStr = String.format("%d-%02d", year, month);
            postsPerMonth.put(monthStr, count);
        }

        return postsPerMonth;
    }

    @Override
    public Map<String, Long> getPostsCountByDay(Date startDate, Date endDate) {
        List<Object[]> results = statisticsBlogRepository.countPostsByDay(startDate, endDate);

        Map<String, Long> postsPerDay = new LinkedHashMap<>();
        for (Object[] result : results) {
            int day = (Integer) result[0];
            int month = (Integer) result[1];
            int year = (Integer) result[2];
            Long count = ((Number) result[3]).longValue();
            String dayStr = String.format("%d-%02d-%02d", year, month, day);
            postsPerDay.put(dayStr, count);
        }
        
        return postsPerDay;
    }

    @Override
    public Map<String, Long> getPostsCountByYear(Date startDate, Date endDate) {
        List<Object[]> results = statisticsBlogRepository.countPostsByYear(startDate, endDate);

        Map<String, Long> postsPerYear = new LinkedHashMap<>();
        for (Object[] result : results) {
            int year = (Integer) result[0];
            Long count = ((Number) result[1]).longValue();
            postsPerYear.put(String.valueOf(year), count);
        }
        
        return postsPerYear;
    }

    @Override
    public Map<String, Long> getCommentsCountByMonth(Date startDate, Date endDate) {
        List<Object[]> results = statisticsCommentsRepository.countCommentsByMonth(startDate, endDate);
        
        Map<String, Long> commentsPerMonth = new LinkedHashMap<>();
        for (Object[] result : results) {
            int month = (Integer) result[0];
            int year = (Integer) result[1];
            Long count = ((Number) result[2]).longValue();
            String monthStr = String.format("%d-%02d", year, month);
            commentsPerMonth.put(monthStr, count);
        }
        
        return commentsPerMonth;
    }

    @Override
    public Map<String, Long> getCommentsCountByDay(Date startDate, Date endDate) {
        List<Object[]> results = statisticsCommentsRepository.countCommentsByDay(startDate, endDate);
        
        Map<String, Long> commentsPerDay = new LinkedHashMap<>();
        for (Object[] result : results) {
            int day = (Integer) result[0];
            int month = (Integer) result[1];
            int year = (Integer) result[2];
            Long count = ((Number) result[3]).longValue();
            String dayStr = String.format("%d-%02d-%02d", year, month, day);
            commentsPerDay.put(dayStr, count);
        }
        
        return commentsPerDay;
    }

    @Override
    public Map<String, Long> getCommentsCountByYear(Date startDate, Date endDate) {
        List<Object[]> results = statisticsCommentsRepository.countCommentsByYear(startDate, endDate);

        Map<String, Long> commentsPerYear = new LinkedHashMap<>();
        for (Object[] result : results) {
            int year = (Integer) result[0];
            Long count = ((Number) result[1]).longValue();
            commentsPerYear.put(String.valueOf(year), count);
        }
        
        return commentsPerYear;
    }
    
    @Override
    public Map<String, Long> getReactionsDistribution() {
        List<Object[]> results = statisticsBlogRepository.countReactionsByType();
        
        Map<String, Long> reactionsDistribution = new LinkedHashMap<>();
        for (Object[] result : results) {
            Reaction reactionType = (Reaction) result[0];
            Long count = ((Number) result[1]).longValue();
            reactionsDistribution.put(reactionType != null ? reactionType.toString() : "NONE", count);
        }
        
        return reactionsDistribution;
    }

   //mariem

    @Override
    public Map<String, Object> getActivityStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("fullActivities", statisticsRepository.countFullActivities());
        statistics.put("availableActivities", statisticsRepository.countAvailableActivities());
        statistics.put("averagePrice", statisticsRepository.getAverageActivityPrice());
        return statistics;
    }

    @Override
    public List<Object[]> getLocationStatistics() {
        return statisticsRepository.countActivitiesByLocation();
    }

    @Override
    public List<Object[]> getMonthlyActivityStatistics() {
        return statisticsRepository.countActivitiesByMonth();
    }

    @Override
    public Map<String, Long> getActivityMetrics(Long activityId) {
        Map<String, Long> metrics = new HashMap<>();
        metrics.put("waitlistCount", statisticsRepository.countWaitlistByActivity(activityId));
        metrics.put("reservationsCount", statisticsRepository.countReservationsByActivity(activityId));
        return metrics;
    }

    @Override
    public Map<Integer, Long> getRatingDistribution() {
        Map<Integer, Long> ratingStats = new HashMap<>();
        for (int i = 0; i <= 5; i++) {
            ratingStats.put(i, statisticsRepository.countByRating(i));
        }
        return ratingStats;
    }

    @Override
    public Map<String, Long> getStatusStatistics() {
        Map<String, Long> statusStats = new HashMap<>();
        statusStats.put("read", statisticsRepository.countByReadStatus(true));
        statusStats.put("unread", statisticsRepository.countByReadStatus(false));
        return statusStats;
    }

    @Override
    public Map<String, Long> getUrgencyStatistics() {
        Map<String, Long> urgencyStats = new HashMap<>();
        urgencyStats.put("urgent", statisticsRepository.countByUrgencyStatus(true));
        urgencyStats.put("normal", statisticsRepository.countByUrgencyStatus(false));
        return urgencyStats;
    }

    @Override
    public long getTotalComplaintsCount() {
        return statisticsRepository.countAllComplaints();
    }
}
