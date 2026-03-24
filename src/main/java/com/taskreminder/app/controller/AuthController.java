package com.taskreminder.app.controller;


import com.taskreminder.app.dto.LoginRequest;
import com.taskreminder.app.dto.OtpRequest;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, RedirectAttributes redirect) {

        try {
            userService.register(user);
            redirect.addFlashAttribute("success", "Account created! Please verify OTP.");
            return "redirect:/verify-otp";
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/verify-otp")
    public String showVerifyOtp(Model model) {
        model.addAttribute("otpRequest", new OtpRequest());
        return "verify-otp";
    }


    @PostMapping("/verify")
    public String verify(@ModelAttribute("otpRequest") OtpRequest request,
                         RedirectAttributes redirect) {

        try {
            String response = userService.verifyOtp(request.getEmail(), request.getOtp());
            redirect.addFlashAttribute("success", response + " Please login.");
            return "redirect:/login";
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/verify-otp";
        }
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                                   @RequestParam String password,
                                   HttpSession session,
                                   RedirectAttributes redirect) {

        String response = userService.loginUser(
                email,
                password,
                session
        );
        return  "redirect:/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }




}

