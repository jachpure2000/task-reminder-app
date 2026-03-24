package com.taskreminder.app.service;

import com.taskreminder.app.controller.TaskController;
import com.taskreminder.app.entity.Task;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.repository.TaskRepository;
import com.taskreminder.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    public Optional<Task> findById(Integer id) {
        return taskRepository.findById(id);
    }

    public Task addTask(Task task,Integer userId) {

        Optional<User> user = userRepository.findById(userId);
        task.setUser(user.get());
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        task.setCreatedAt(LocalDateTime.now());
        task.setCreatedAt(LocalDateTime.now());
        String passEncode= bCryptPasswordEncoder.encode(user.get().getPassword());
        return taskRepository.save(task);
    }

    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Integer id) {
        taskRepository.deleteById(id);
    }

    public Page<Task> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

//    public List<Task> findByStatus(TaskStatus status) {
//        return taskRepository.findByStatus(status,null);
//    }
//
//    public List<Task> findByPriority(TaskPriority priority) {
//        return taskRepository.findByPriority(priority,null);
//    }
//
//    public List<Task> searchByTitle(String keyword) {
//        return taskRepository.findByTitleContainingIgnoreCase(keyword);
//    }

    public List<Task> findByDueDate(String date) {
        return taskRepository.findByDueDate(date);
    }

    public Page<Task> getPagedTasksForUser(
            Integer userId,
            Pageable pageable,
            TaskStatus status,
            TaskPriority priority,
            String keyword) {

        if (status != null) {
            return taskRepository.findByUserIdAndStatus(userId, status, pageable);
        } else if (priority != null) {
            return taskRepository.findByUserIdAndPriority(userId, priority, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            return taskRepository.findByUserIdAndTitleContainingIgnoreCase(userId, keyword, pageable);
        }

        return taskRepository.findByUserId(userId, pageable);
    }

    public List<Task> getTasksDueToday() {
        LocalDate today = LocalDate.now();
        return taskRepository.findByDueDate(today.toString());
    }

    public List<Task> getUpcomingTasks(int days) {
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(days);
        return taskRepository.findByDueDateBetween(today.toString(), end.toString());
    }

    public List<Task> getOverdueTasks() {
        LocalDate today = LocalDate.now();
        return taskRepository.findByDueDateBefore(today.toString());
    }

    public void checkAndMarkDueTasks() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next30Min = now.plusMinutes(30);

        List<Task> tasks = taskRepository.findTasksDueSoon(now, next30Min);

        for (Task task : tasks) {
            if (!task.isReminderSent()) {
                task.setReminderSent(true);
                task.setReminderTime(LocalDateTime.now());
                taskRepository.save(task);
            }
        }
    }


    public long countAllTasks(Integer userId) {
        return taskRepository.countByUserId(userId);
    }

    public long countCompleted(Integer userId) {
        return taskRepository.countByUserIdAndStatus(userId, TaskStatus.DONE);
    }

    public long countPending(Integer userId) {
        return taskRepository.countByUserIdAndStatus(userId, TaskStatus.PENDING);
    }

    public long countOverdue(Integer userId) {
        return taskRepository.countOverdueTasks(userId, LocalDateTime.now());
    }

    public List<Task> getTasksByUser(Integer userId) {
        return taskRepository.findByUserId(userId);
    }



}
