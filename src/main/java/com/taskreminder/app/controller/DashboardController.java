package com.taskreminder.app.controller;

import com.taskreminder.app.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.taskreminder.app.entity.Task;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.enums.TaskPriority;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final TaskService taskService;

    public DashboardController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Task> tasks = taskService.getTasksByUser(userId);

        long total = tasks.size();
        long completed = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        long pending = total - completed;

        long overdue = tasks.stream()
                .filter(t -> t.getDueDate() != null &&
                        t.getDueDate().toLocalDate().isBefore(LocalDate.now()) &&
                        t.getStatus() != TaskStatus.DONE)
                .count();

        long dueToday = tasks.stream()
                .filter(t -> t.getDueDate() != null &&
                        t.getDueDate().toLocalDate().isEqual(LocalDate.now()))
                .count();

        long highPriority = tasks.stream()
                .filter(t -> t.getPriority() == TaskPriority.HIGH)
                .count();

        List<Task> recent = tasks.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("total", total);
        model.addAttribute("completed", completed);
        model.addAttribute("pending", pending);
        model.addAttribute("overdue", overdue);
        model.addAttribute("dueToday", dueToday);
        model.addAttribute("highPriority", highPriority);
        model.addAttribute("recentTasks", recent);

        return "dashboard";
    }
}
