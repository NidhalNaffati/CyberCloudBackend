package tn.esprit.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ActivityImageService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadActivityImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString();
    }

    public void deleteActivityImage(String imageUrl) throws IOException {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    public String updateActivityImage(String oldImageUrl, MultipartFile newFile) throws IOException {
        // Delete old image if it exists
        if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
            deleteActivityImage(oldImageUrl);
        }

        // Upload new image
        return uploadActivityImage(newFile);
    }

    private String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        String[] urlParts = imageUrl.split("/");
        String lastPart = urlParts[urlParts.length - 1];
        return lastPart.substring(0, lastPart.lastIndexOf("."));
    }
}