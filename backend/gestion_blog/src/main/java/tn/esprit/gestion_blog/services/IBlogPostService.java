package tn.esprit.gestion_blog.services;

import tn.esprit.gestion_blog.entities.BlogPost;

import java.util.List;

public interface IBlogPostService {
    List<BlogPost> getAllPosts();
    BlogPost getPostById(Long id);  // Modifier ici pour retourner un BlogPost
    BlogPost createPost(BlogPost post);
    BlogPost updatePost(Long id, BlogPost updatedPost);
    void deletePost(Long id);
}
