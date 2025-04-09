package com.buzzsocial.service;

import com.buzzsocial.dto.request.CreateAdminRequest;
import com.buzzsocial.dto.response.BuzzDto;
import com.buzzsocial.dto.response.CommentDto;
import com.buzzsocial.dto.response.UserDto;
import com.buzzsocial.exception.BadRequestException;
import com.buzzsocial.exception.ResourceNotFoundException;
import com.buzzsocial.exception.UnauthorizedException;
import com.buzzsocial.model.*;
import com.buzzsocial.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Value("${admin.secret.key}")
    private String adminSecretKey;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BuzzRepository buzzRepository;

    @Autowired
    private BuzzCommentRepository commentRepository;

    @Autowired
    private BuzzReportRepository reportRepository;

    @Autowired
    private UserFollowerRepository followerRepository;

    @Autowired
    private BuzzLikeRepository buzzLikeRepository;

    public User createAdmin(CreateAdminRequest request) {
        // Validate admin secret key
        if (!adminSecretKey.equals(request.getAdminSecretKey())) {
            throw new UnauthorizedException("Invalid admin secret key");
        }

        // Validate username and email uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        // Create new admin user
        User admin = new User();
        admin.setFullName(request.getFullName());
        admin.setBirthDate(request.getBirthDate());
        admin.setEmail(request.getEmail());
        admin.setUsername(request.getUsername());
        admin.setPassword(request.getPassword());
        admin.setIsAdmin(true);

        return userRepository.save(admin);
    }

    public Page<User> getAllUsers(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable);
    }

    public List<User> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }

    public Page<Buzz> getAllBuzzs(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return buzzRepository.findAll(pageable);
    }

    public List<Buzz> searchBuzzs(String query) {
        Pageable pageable = PageRequest.of(0, 100);
        return buzzRepository.searchBuzzs(query, pageable);
    }

    public Page<BuzzComment> getAllComments(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return commentRepository.findAll(pageable);
    }

    public List<BuzzComment> searchComments(String query) {
        return commentRepository.findByContentContainingIgnoreCase(query);
    }

    public Page<BuzzReport> getAllReports(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return reportRepository.findAll(pageable);
    }

    public List<BuzzReport> searchReports(String query) {
        return reportRepository.findByReasonContainingIgnoreCase(query);
    }

    @Transactional
    public void updateReportStatus(String reportId, BuzzReport.ReportStatus status) {
        BuzzReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        report.setStatus(status);
        reportRepository.save(report);
    }

    @Transactional
    public void banUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsBanned(true);
        userRepository.save(user);
    }

    @Transactional
    public void unbanUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsBanned(false);
        userRepository.save(user);
    }

    @Transactional
    public void deleteBuzz(String buzzId) {
        Buzz buzz = buzzRepository.findById(buzzId)
                .orElseThrow(() -> new ResourceNotFoundException("Buzz not found"));
        
        // Update user buzz count
        User user = buzz.getUser();
        user.setBuzzCount(user.getBuzzCount() - 1);
        userRepository.save(user);
        
        // Delete buzz
        buzzRepository.delete(buzz);
    }

    @Transactional
    public void deleteComment(String commentId) {
        BuzzComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        // Update buzz comment count
        Buzz buzz = comment.getBuzz();
        buzz.setCommentCount(buzz.getCommentCount() - 1);
        buzzRepository.save(buzz);
        
        // Delete comment
        commentRepository.delete(comment);
    }

    public List<UserDto> getUserFollowers(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<User> followers = followerRepository.findFollowersByUserId(userId);
        return followers.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getUserFollowings(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<User> followings = followerRepository.findFollowingsByUserId(userId);
        return followings.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalBuzzs = buzzRepository.count();
        long totalComments = commentRepository.count();
        long totalReports = reportRepository.count();
        long pendingReports = reportRepository.countByStatus(BuzzReport.ReportStatus.PENDING);
        long bannedUsers = userRepository.countByIsBanned(true);
        
        return Map.of(
            "totalUsers", totalUsers,
            "totalBuzzs", totalBuzzs,
            "totalComments", totalComments,
            "totalReports", totalReports,
            "pendingReports", pendingReports,
            "bannedUsers", bannedUsers
        );
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .birthDate(user.getBirthDate())
                .avatarUrl(user.getAvatarUrl())
                .bannerUrl(user.getBannerUrl())
                .bio(user.getBio())
                .instagram(user.getInstagram())
                .facebook(user.getFacebook())
                .twitter(user.getTwitter())
                .linkedin(user.getLinkedin())
                .website(user.getWebsite())
                .followersCount(user.getFollowersCount())
                .followingsCount(user.getFollowingsCount())
                .buzzCount(user.getBuzzCount())
                .isBanned(user.getIsBanned())
                .isAdmin(user.getIsAdmin())
                .joinedAt(user.getCreatedAt())
                .build();
    }
}
