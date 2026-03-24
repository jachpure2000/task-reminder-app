package com.taskreminder.app.repository;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface  TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByDueDate(String date);
    List<Task> findByDueDateBetween(String start, String end);

    List<Task> findByDueDateBefore(String date);

    Page<Task> findByUserId(Integer userId, Pageable pageable);

    Page<Task> findByUserIdAndStatus(Integer userId, TaskStatus status, Pageable pageable);

    Page<Task> findByUserIdAndPriority(Integer userId, TaskPriority priority, Pageable pageable);

    Page<Task> findByUserIdAndTitleContainingIgnoreCase(Integer userId, String keyword, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :now AND :next")
    List<Task> findTasksDueSoon(LocalDateTime now, LocalDateTime next);

    List<Task> findByReminderSentFalseAndReminderTimeBefore(LocalDateTime now);

    long countByUserId(Integer userId);
    long countByUserIdAndStatus(Integer userId, TaskStatus status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.dueDate < :now AND t.status <> 'DONE'")
    long countOverdueTasks(@Param("userId") Integer userId, @Param("now") LocalDateTime now);

    List<Task> findByUserId(Integer userId);


}
