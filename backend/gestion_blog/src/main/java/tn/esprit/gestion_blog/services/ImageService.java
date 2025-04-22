package tn.esprit.gestion_blog.services;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.gestion_blog.entities.BlogPost;
import tn.esprit.gestion_blog.entities.Image;
import tn.esprit.gestion_blog.exceptions.ResourceNotFoundException;
import tn.esprit.gestion_blog.repositories.BlogPostRepository;
import tn.esprit.gestion_blog.repositories.ImageRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageService implements IImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private Cloudinary cloudinary;
    // Dossier où les images seront stockées
    private final String uploadDir = "uploads";

    @Override
    public Image uploadImage(Long postId, MultipartFile file, String description, Integer orderIndex) throws IOException {
        // Vérifier si le post existe
        BlogPost blogPost = blogPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post non trouvé avec l'ID : " + postId));

        Map<?,?> uploadFile= cloudinary.uploader().upload(file.getBytes(),Map.of("public_id",UUID.randomUUID().toString()));
        String Url= uploadFile.get("url").toString();
        Image image = new Image();
        image.setBlogPost(blogPost);
        image.setUrl(Url);
        image.setDescription(description);
        image.setOrderIndex(orderIndex);


        return imageRepository.save(image);
    }

    @Override
    public List<Image> uploadMultipleImages(Long postId, MultipartFile[] files, String[] descriptions, Integer[] orderIndices) throws IOException {
        List<Image> uploadedImages = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            String description = (i < descriptions.length) ? descriptions[i] : "";
            Integer orderIndex = (i < orderIndices.length) ? orderIndices[i] : i;
            uploadedImages.add(uploadImage(postId, files[i], description, orderIndex));
        }

        return uploadedImages;
    }

    @Override
    public List<Image> getImagesByPostId(Long postId) {
        // Vérifier si le post existe
        if (!blogPostRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post non trouvé avec l'ID : " + postId);
        }
        return imageRepository.findByBlogPost_PostId(postId);
    }

    @Override
    public Image getImageById(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image non trouvée avec l'ID : " + imageId));
    }

    @Override
    public Image updateImage(Long imageId, String description, Integer orderIndex) {
        Image image = getImageById(imageId);
        image.setDescription(description);
        image.setOrderIndex(orderIndex);
        return imageRepository.save(image);
    }

    @Override
    public void deleteImage(Long imageId) {
        Image image = getImageById(imageId);
        
        // Supprimer le fichier physique
        try {
            // Extraire le nom du fichier de l'URL
            String fileName = image.getUrl().substring(image.getUrl().lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Logger l'erreur mais continuer la suppression de l'entrée en base
            System.err.println("Erreur lors de la suppression du fichier : " + e.getMessage());
        }
        
        // Supprimer l'entrée en base de données
        imageRepository.deleteById(imageId);
    }

    @Override
    @Transactional
    public void deleteImagesByPostId(Long postId) {
        // Récupérer toutes les images du post
        List<Image> images = getImagesByPostId(postId);
        
        // Supprimer les fichiers physiques
        for (Image image : images) {
            try {
                // Extraire le nom du fichier de l'URL
                String fileName = image.getUrl().substring(image.getUrl().lastIndexOf("/") + 1);
                Path filePath = Paths.get(uploadDir).resolve(fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Logger l'erreur mais continuer
                System.err.println("Erreur lors de la suppression du fichier : " + e.getMessage());
            }
        }
        
        // Supprimer les entrées en base de données
        imageRepository.deleteByBlogPost_PostId(postId);
    }
}



