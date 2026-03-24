package com.taskreminder.app.service;

import com.taskreminder.app.entity.Task;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {




    public  void sendOTP(String toEmail, String otp) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("aman@gmail.com", "evse wxvg zkss lxhg");
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(""));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Email Verification");
            message.setText("Your OTP for email verification is: " + otp + "\n\nPlease verify your account before login.");

            Transport.send(message);
            System.out.println("✅ OTP sent to: " + toEmail);

        } catch (Exception e) {
            System.out.println("❌ Error sending OTP: " + e.getMessage());
        }
    }


    public void sendTaskReminder(Task task) {
        String to = task.getUser().getEmail();
        String subject = "Task Reminder: " + task.getTitle();
        String body = "Hi " + task.getUser().getName() + ",\n\n" +
                "Your task \"" + task.getTitle() + "\" is due at " +
                task.getDueDate() + ".\nPlease complete it on time.\n\n" +
                "Task Reminder App";

//        sendEmail(to, subject, body);
    }

}
