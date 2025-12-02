package com.taskreminder.app.service;

import com.taskreminder.app.entity.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    private final List<Task> tasks = new ArrayList<>();
    private static int counter = 100;

    public TaskService() {
        tasks.add(new Task(1, "Learn Spring Boot", "Basics of project", "2025-12-05", "Pending", "High", LocalDateTime.now()));
        tasks.add(new Task(2, "Practice Java", "Collections & OOP", "2025-12-06", "Pending", "Medium", LocalDateTime.now()));
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public static int nextId() {
        return counter++;
    }

}
