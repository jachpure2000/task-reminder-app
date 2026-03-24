package com.taskreminder.app.controller;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.service.TaskService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public String listTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String keyword,
            Model model, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        List<Task> tasks=null;
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskService.getPagedTasksForUser(userId,pageable, status, priority, keyword);
        model.addAttribute("taskPage", taskPage);
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("size", size);
        int totalPages = taskPage.getTotalPages();
        List<Integer> pageNumbers = IntStream.range(0, totalPages).boxed().toList();
        model.addAttribute("pageNumbers", pageNumbers);
        return "tasks";
    }

    @GetMapping("/add")
    public String showAddForm(Model model,HttpSession session) {
        model.addAttribute("task", new Task());
        return "add-task";
    }

    @PostMapping("/add")
    public String saveTask(@ModelAttribute Task task, Model model, RedirectAttributes ra,HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            ra.addFlashAttribute("errorMessage", "You must be logged in!");
            return "redirect:/login";
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Title is required!");
            model.addAttribute("task", task);
            return "add-task";
        }

        if (task.getDueDate() == null) {
            model.addAttribute("errorMessage", "Due Date is required!");
            model.addAttribute("task", task);
            return "add-task";
        }
        taskService.addTask(task,userId);
        ra.addFlashAttribute("successMessage", "Task added successfully!");
        return "redirect:/api/tasks";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model,HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }

        Task task = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        model.addAttribute("task", task);
        return "edit-task";
    }

    @PostMapping("/edit/{id}")
    public String updateTask(@PathVariable Integer id, @ModelAttribute Task task, Model model,HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }

        Task existing = taskService.findById(id)
                .orElse(null);

        if (existing == null) {
            model.addAttribute("errorMessage", "Task not found!");
            return "edit-task";
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Title is required!");
            return "edit-task";
        }

        task.setId(id);
        task.setCreatedAt(existing.getCreatedAt());

        taskService.updateTask(task);
        return "redirect:/api/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Integer id, Model model,HttpSession session) {

        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        if (!taskService.findById(id).isPresent()) {
            model.addAttribute("errorMessage", "Task not found!");
            return "tasks";
        }
        taskService.deleteTask(id);
        return "redirect:/api/tasks";
    }

    @GetMapping("/markdone/{id}")
    public String markAsDone(@PathVariable Integer id,HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        Task task = taskService.findById(id).orElse(null);
        if (task != null && task.getStatus()!= TaskStatus.DONE) {
//            task.setStatus("Done");
            task.setStatus(TaskStatus.DONE);
            task.setCompletedAt(LocalDateTime.now());
            taskService.updateTask(task);
        }

        return "redirect:/api/tasks";
    }

    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable Integer id, Model model, RedirectAttributes ra,HttpSession session) {

        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        Task task = taskService.findById(id).orElse(null);
        if (task == null) {
            ra.addFlashAttribute("errorMessage", "Task not found!");
            return "redirect:/api/tasks";
        }

        model.addAttribute("task", task);
        return "view-task";
    }

    @GetMapping("/export")
    public void exportTasksToCsv(HttpServletResponse response, HttpSession session,@RequestParam String exportway) throws IOException {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect("/login");
            return;
        }
        response.setContentType("text/csv");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=tasks.csv"
        );

        List<Task> tasks = taskService.getTasksByUser(userId);

        PrintWriter writer = response.getWriter();

        writer.println("ID,Title,Description,Status,Priority,Due Date,Created At,Completed At");

        for (Task task : tasks) {
            writer.println(
                    task.getId() + "," +
                            escape(task.getTitle()) + "," +
                            escape(task.getDescription()) + "," +
                            task.getStatus() + "," +
                            task.getPriority() + "," +
                            task.getDueDate() + "," +
                            task.getCreatedAt() + "," +
                            task.getCompletedAt()
            );
        }

        writer.flush();
        writer.close();
    }

    /**
     * Prevent CSV breaking when text contains commas
     */
    private String escape(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }


}
