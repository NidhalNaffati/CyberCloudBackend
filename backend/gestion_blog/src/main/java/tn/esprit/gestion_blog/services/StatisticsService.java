package tn.esprit.gestion_blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.gestion_blog.entities.BlogComment;
import tn.esprit.gestion_blog.entities.BlogPost;
import tn.esprit.gestion_blog.entities.Reaction;
import tn.esprit.gestion_blog.repositories.BlogCommentRepository;
import tn.esprit.gestion_blog.repositories.BlogPostRepository;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService implements IStatisticsService {

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private BlogCommentRepository blogCommentRepository;

    @Override
    public Map<String, Long> getPostsCountByMonth(Date startDate, Date endDate) {
        // Récupérer tous les posts
        List<BlogPost> allPosts = blogPostRepository.findAll();
        
        // Filtrer par date si nécessaire
        if (startDate != null || endDate != null) {
            Date finalStartDate = startDate != null ? startDate : new Date(0); // Date minimale si null
            Date finalEndDate = endDate != null ? endDate : new Date(); // Date actuelle si null
            
            allPosts = allPosts.stream()
                    .filter(post -> {
                        Date postDate = post.getCreatedAt();
                        return postDate.after(finalStartDate) && postDate.before(finalEndDate) || 
                               postDate.equals(finalStartDate) || postDate.equals(finalEndDate);
                    })
                    .collect(Collectors.toList());
        }
        
        // Si aucun post n'est trouvé, retourner une map vide
        if (allPosts.isEmpty()) {
            return new LinkedHashMap<>();
        }
        
        // Grouper par mois et compter
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        Map<String, Long> postsPerMonth = allPosts.stream()
                .collect(Collectors.groupingBy(
                        post -> monthFormat.format(post.getCreatedAt()),
                        Collectors.counting()
                ));
        
        // Trier par mois et ne retourner que les mois avec des données
        return postsPerMonth.entrySet().stream()
                .filter(entry -> entry.getValue() > 0) // Ne garder que les mois avec des données
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
    
    @Override
    public Map<String, Long> getPostsCountByDay(Date startDate, Date endDate) {
        // Récupérer tous les posts
        List<BlogPost> allPosts = blogPostRepository.findAll();
        
        // Filtrer par date si nécessaire
        if (startDate != null || endDate != null) {
            Date finalStartDate = startDate != null ? startDate : new Date(0); // Date minimale si null
            Date finalEndDate = endDate != null ? endDate : new Date(); // Date actuelle si null
            
            allPosts = allPosts.stream()
                    .filter(post -> {
                        Date postDate = post.getCreatedAt();
                        return postDate.after(finalStartDate) && postDate.before(finalEndDate) || 
                               postDate.equals(finalStartDate) || postDate.equals(finalEndDate);
                    })
                    .collect(Collectors.toList());
        }
        
        // Si aucun post n'est trouvé, retourner une map vide
        if (allPosts.isEmpty()) {
            return new LinkedHashMap<>();
        }
        
        // Grouper par jour et compter
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Long> postsPerDay = allPosts.stream()
                .collect(Collectors.groupingBy(
                        post -> dayFormat.format(post.getCreatedAt()),
                        Collectors.counting()
                ));
        
        // Trier par jour et ne retourner que les jours avec des données
        return postsPerDay.entrySet().stream()
                .filter(entry -> entry.getValue() > 0) // Ne garder que les jours avec des données
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
    
    @Override
    public Map<String, Long> getPostsCountByYear(Date startDate, Date endDate) {
        // Récupérer tous les posts
        List<BlogPost> allPosts = blogPostRepository.findAll();
        
        // Filtrer par date si nécessaire
        if (startDate != null || endDate != null) {
            Date finalStartDate = startDate != null ? startDate : new Date(0); // Date minimale si null
            Date finalEndDate = endDate != null ? endDate : new Date(); // Date actuelle si null
            
            allPosts = allPosts.stream()
                    .filter(post -> {
                        Date postDate = post.getCreatedAt();
                        return postDate.after(finalStartDate) && postDate.before(finalEndDate) || 
                               postDate.equals(finalStartDate) || postDate.equals(finalEndDate);
                    })
                    .collect(Collectors.toList());
        }
        
        // Si aucun post n'est trouvé, retourner une map vide
        if (allPosts.isEmpty()) {
            return new LinkedHashMap<>();
        }
        
        // Grouper par année et compter
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        Map<String, Long> postsPerYear = allPosts.stream()
                .collect(Collectors.groupingBy(
                        post -> yearFormat.format(post.getCreatedAt()),
                        Collectors.counting()
                ));
        
        // Trier par année et ne retourner que les années avec des données
        return postsPerYear.entrySet().stream()
                .filter(entry -> entry.getValue() > 0) // Ne garder que les années avec des données
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
    
    @Override
    public Map<String, Long> getCommentsCountByMonth(Date startDate, Date endDate) {
        // Récupérer tous les commentaires
        List<BlogComment> allComments = blogCommentRepository.findAll();
        
        // Filtrer par date si nécessaire
        if (startDate != null || endDate != null) {
            Date finalStartDate = startDate != null ? startDate : new Date(0); // Date minimale si null
            Date finalEndDate = endDate != null ? endDate : new Date(); // Date actuelle si null
            
            allComments = allComments.stream()
                    .filter(comment -> {
                        Date commentDate = comment.getCreatedAt();
                        return commentDate.after(finalStartDate) && commentDate.before(finalEndDate) || 
                               commentDate.equals(finalStartDate) || commentDate.equals(finalEndDate);
                    })
                    .collect(Collectors.toList());
        }
        
        // Si aucun commentaire n'est trouvé, retourner une map vide
        if (allComments.isEmpty()) {
            return new LinkedHashMap<>();
        }
        
        // Grouper par mois et compter
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        Map<String, Long> commentsPerMonth = allComments.stream()
                .collect(Collectors.groupingBy(
                        comment -> monthFormat.format(comment.getCreatedAt()),
                        Collectors.counting()
                ));
        
        // Trier par mois et ne retourner que les mois avec des données
        return commentsPerMonth.entrySet().stream()
                .filter(entry -> entry.getValue() > 0) // Ne garder que les mois avec des données
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
    
    @Override
    public Map<String, Long> getCommentsCountByDay(Date startDate, Date endDate) {
        // Récupérer tous les commentaires
        List<BlogComment> allComments = blogCommentRepository.findAll();
        
        // Filtrer par date si nécessaire
        if (startDate != null || endDate != null) {
            Date finalStartDate = startDate != null ? startDate : new Date(0); // Date minimale si null
            Date finalEndDate = endDate != null ? endDate : new Date(); // Date actuelle si null
            
            allComments = allComments.stream()
                    .filter(comment -> {
                        Date commentDate = comment.getCreatedAt();
                        return commentDate.after(finalStartDate) && commentDate.before(finalEndDate) || 
                               commentDate.equals(finalStartDate) || commentDate.equals(finalEndDate);
                    })
                    .collect(Collectors.toList());
        }
        
        // Si aucun commentaire n'est trouvé, retourner une map vide
        if (allComments.isEmpty()) {
            return new LinkedHashMap<>();
        }
        
        // Grouper par jour et compter
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Long> commentsPerDay = allComments.stream()
                .collect(Collectors.groupingBy(
                        comment -> dayFormat.format(comment.getCreatedAt()),
                        Collectors.counting()
                ));
        
        // Trier par jour et ne retourner que les jours avec des données
        return commentsPerDay.entrySet().stream()
                .filter(entry -> entry.getValue() > 0) // Ne garder que les jours avec des données
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
    
    @Override
    public Map<String, Long> getCommentsCountByYear(Date startDate, Date endDate) {
        // Récupérer tous les commentaires
        List<BlogComment> allComments = blogCommentRepository.findAll();
        
        // Filtrer par date si nécessaire
        if (startDate != null || endDate != null) {
            Date finalStartDate = startDate != null ? startDate : new Date(0); // Date minimale si null
            Date finalEndDate = endDate != null ? endDate : new Date(); // Date actuelle si null
            
            allComments = allComments.stream()
                    .filter(comment -> {
                        Date commentDate = comment.getCreatedAt();
                        return commentDate.after(finalStartDate) && commentDate.before(finalEndDate) || 
                               commentDate.equals(finalStartDate) || commentDate.equals(finalEndDate);
                    })
                    .collect(Collectors.toList());
        }
        
        // Si aucun commentaire n'est trouvé, retourner une map vide
        if (allComments.isEmpty()) {
            return new LinkedHashMap<>();
        }
        
        // Grouper par année et compter
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        Map<String, Long> commentsPerYear = allComments.stream()
                .collect(Collectors.groupingBy(
                        comment -> yearFormat.format(comment.getCreatedAt()),
                        Collectors.counting()
                ));
        
        // Trier par année et ne retourner que les années avec des données
        return commentsPerYear.entrySet().stream()
                .filter(entry -> entry.getValue() > 0) // Ne garder que les années avec des données
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    @Override
    public Map<String, Long> getReactionsDistribution() {
        // Récupérer tous les posts
        List<BlogPost> allPosts = blogPostRepository.findAll();
        
        // Initialiser le compteur pour chaque type de réaction
        Map<String, Long> reactionCounts = Arrays.stream(Reaction.values())
                .collect(Collectors.toMap(
                        Reaction::name,
                        reaction -> 0L
                ));
        
        // Compter les réactions
        allPosts.stream()
                .filter(post -> post.getReaction() != null)
                .forEach(post -> {
                    String reactionName = post.getReaction().name();
                    reactionCounts.put(reactionName, reactionCounts.getOrDefault(reactionName, 0L) + 1);
                });
        
        return reactionCounts;
    }

    @Override
    public Map<Long, Integer> getMostCommentedPosts(int limit) {
        // Récupérer tous les commentaires
        List<BlogComment> allComments = blogCommentRepository.findAll();
        
        // Grouper par postId et compter
        Map<Long, Integer> commentsPerPost = allComments.stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getBlogPost().getPostId(),
                        Collectors.summingInt(comment -> 1)
                ));
        
        // Trier par nombre de commentaires (décroissant) et limiter
        return commentsPerPost.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    @Override
    public Map<Long, Map<String, Integer>> getUsersActivity(int limit) {
        // Récupérer tous les posts et commentaires
        List<BlogPost> allPosts = blogPostRepository.findAll();
        List<BlogComment> allComments = blogCommentRepository.findAll();
        
        // Compter les posts par utilisateur
        Map<Long, Integer> postsPerUser = allPosts.stream()
                .collect(Collectors.groupingBy(
                        BlogPost::getUserId,
                        Collectors.summingInt(post -> 1)
                ));
        
        // Compter les commentaires par utilisateur
        Map<Long, Integer> commentsPerUser = allComments.stream()
                .collect(Collectors.groupingBy(
                        BlogComment::getUserId,
                        Collectors.summingInt(comment -> 1)
                ));
        
        // Fusionner les deux maps
        Map<Long, Integer> totalActivityPerUser = new HashMap<>();
        
        // Ajouter tous les utilisateurs qui ont des posts
        postsPerUser.forEach((userId, postCount) -> {
            totalActivityPerUser.put(userId, postCount);
        });
        
        // Ajouter ou mettre à jour les utilisateurs qui ont des commentaires
        commentsPerUser.forEach((userId, commentCount) -> {
            totalActivityPerUser.put(userId, totalActivityPerUser.getOrDefault(userId, 0) + commentCount);
        });
        
        // Trier par activité totale et limiter
        List<Long> mostActiveUsers = totalActivityPerUser.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        // Créer le résultat final
        Map<Long, Map<String, Integer>> result = new LinkedHashMap<>();
        
        for (Long userId : mostActiveUsers) {
            Map<String, Integer> userActivity = new HashMap<>();
            userActivity.put("posts", postsPerUser.getOrDefault(userId, 0));
            userActivity.put("comments", commentsPerUser.getOrDefault(userId, 0));
            userActivity.put("total", totalActivityPerUser.get(userId));
            result.put(userId, userActivity);
        }
        
        return result;
    }
}