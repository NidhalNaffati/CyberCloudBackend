package tn.esprit.gestion_blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO pour les différentes statistiques du blog
 */
public class StatisticsDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyPostsDTO {
        private Map<String, Long> postsPerMonth;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReactionsDistributionDTO {
        private Map<String, Long> reactionsCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MostCommentedPostsDTO {
        private Map<Long, Integer> commentCounts;
        private Map<Long, String> postTitles; // Pour afficher les titres des posts dans le graphique
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserActivityDTO {
        private Long userId;
        private int postsCount;
        private int commentsCount;
        private int totalActivity;
    }
}