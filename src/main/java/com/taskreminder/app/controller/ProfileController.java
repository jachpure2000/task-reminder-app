package com.taskreminder.app.controller;


import com.taskreminder.app.dto.UpdateProfileRequest;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String viewProfile(Model model, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.getUserById(userId);
        model.addAttribute("user", user);

        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.getUserById(userId);

        UpdateProfileRequest dto = new UpdateProfileRequest();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        model.addAttribute("profile", dto);
        return "profile-edit";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("profile") UpdateProfileRequest dto,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        userService.updateProfile(userId, dto);

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");

        return "redirect:/profile";
    }

    @GetMapping("/profile/image/{fileName}")
    public ResponseEntity<Resource> serveImage(@PathVariable String fileName) throws IOException {

        Path imagePath = Path.of("uploads/profile-images/" + fileName);

        if (!Files.exists(imagePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(imagePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

}
