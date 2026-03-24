package com.taskreminder.app.service;

import com.taskreminder.app.dto.UpdateProfileRequest;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User register(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(false);
        String otpCode = String.valueOf((int)(Math.random()*9000) + 1000);

        user.setOtp(otpCode);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        emailService.sendOTP(user.getEmail(),otpCode);
        return userRepository.save(user);
    }

    public String verifyOtp(String email, String otp) {

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return "User not found";
        }

        if (user.get().isVerified()) {
            return "User already verified";
        }

        if (user.get().getOtp() == null) {
            return "OTP not generated";
        }

        if (!user.get().getOtp().equals(otp)) {
            return "Invalid OTP";
        }

        if (user.get().getOtpExpiry().isBefore(LocalDateTime.now())) {
            return "OTP expired";
        }

        user.get().setVerified(true);

        user.get().setOtp(null);
        user.get().setOtpExpiry(null);

        userRepository.save(user.get());

        return "OTP verified successfully";
    }

    public String loginUser(String email, String password, HttpSession session) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return "User not found";
        }
        if (!user.get().isVerified()) {
            return "User is not verified. Please verify OTP first.";
        }

        if (passwordEncoder.matches(password,user.get().getPassword())) {
            return "Invalid password";
        }
        session.setAttribute("loggedInUser", user.get().getId());
        session.setAttribute("userId", user.get().getId());
        session.setAttribute("email", user.get().getEmail());
        session.setAttribute("name", user.get().getName());


        return "Login successful";
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void updateProfile(Integer userId, UpdateProfileRequest dto) {
        User user = getUserById(userId);

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
//        user.setMobile(dto.getMobile());
        MultipartFile image = dto.getProfileImage();

        if (image != null && !image.isEmpty()) {

            try {
                String uploadDir = "uploads/profile-images/";
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String fileName = "user_" + userId + "_" + System.currentTimeMillis() + ".jpg";
                Path filePath = Path.of(uploadDir + fileName);
                Files.write(filePath, image.getBytes());
                user.setProfileImage(fileName);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }

        userRepository.save(user);
    }


}


