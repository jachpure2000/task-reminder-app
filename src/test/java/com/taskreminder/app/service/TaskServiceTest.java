//package com.taskreminder.app.service;
//
//import com.taskreminder.app.entity.Task;
//import com.taskreminder.app.enums.TaskPriority;
//import com.taskreminder.app.enums.TaskStatus;
//import com.taskreminder.app.repository.TaskRepository;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import org.springframework.data.domain.*;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TaskServiceTest {
//
//    @Mock
//    private TaskRepository taskRepository;
//
//    @InjectMocks
//    private TaskService taskService;
//
//
//    @Test
//    void testGetAllTasks() {
//        List<Task> mockTasks = List.of(new Task(), new Task());
//        when(taskRepository.findAll()).thenReturn(mockTasks);
//
//        List<Task> result = taskService.getAllTasks();
//
//        assertEquals(2, result.size());
//        verify(taskRepository, times(1)).findAll();
//    }
//
//
//    @Test
//    void testFindByIdSuccess() {
//        Task mockTask = new Task();
//        mockTask.setId(1);
//
//        when(taskRepository.findById(1)).thenReturn(Optional.of(mockTask));
//
//        Optional<Task> result = taskService.findById(1);
//
//        assertTrue(result.isPresent());
//        assertEquals(1, result.get().getId());
//    }
//
//    @Test
//    void testFindByIdNotFound() {
//        when(taskRepository.findById(99)).thenReturn(Optional.empty());
//
//        Optional<Task> result = taskService.findById(99);
//
//        assertTrue(result.isEmpty());
//    }
//
////
////    @Test
////    void testAddTask() {
////        Task newTask = new Task();
////        newTask.setTitle("Test Task");
////
////        when(taskRepository.save(newTask)).thenReturn(newTask);
////
////        Task result = taskService.addTask(newTask);
////
////        assertNotNull(result);
////        verify(taskRepository, times(1)).save(newTask);
////    }
//
//    @Test
//    void testUpdateTask() {
//        Task existing = new Task();
//        existing.setId(5);
//
//        when(taskRepository.save(existing)).thenReturn(existing);
//
//        Task result = taskService.updateTask(existing);
//
//        assertEquals(5, result.getId());
//        verify(taskRepository).save(existing);
//    }
//
//    @Test
//    void testDeleteTask() {
//        doNothing().when(taskRepository).deleteById(10);
//
//        taskService.deleteTask(10);
//
//        verify(taskRepository, times(1)).deleteById(10);
//    }
//
//
//    @Test
//    void testFindAllPaged() {
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<Task> mockPage = new PageImpl<>(List.of(new Task()));
//
//        when(taskRepository.findAll(pageable)).thenReturn(mockPage);
//
//        Page<Task> result = taskService.findAll(pageable);
//
//        assertEquals(1, result.getContent().size());
//        verify(taskRepository, times(1)).findAll(pageable);
//    }
//
//
//    @Test
//    void testFindByDueDate() {
//        List<Task> mockTasks = List.of(new Task());
//        when(taskRepository.findByDueDate("2025-01-10")).thenReturn(mockTasks);
//
//        List<Task> result = taskService.findByDueDate("2025-01-10");
//
//        assertEquals(1, result.size());
//        verify(taskRepository).findByDueDate("2025-01-10");
//    }
//
//
//    @Test
//    void testGetPagedTasks_FilterByStatus() {
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<Task> mockPage = new PageImpl<>(List.of(new Task()));
//
//        when(taskRepository.findByStatus(TaskStatus.PENDING, pageable)).thenReturn(mockPage);
//
//        Page<Task> result = taskService.getPagedTasks(pageable, TaskStatus.PENDING, null, null);
//
//        assertEquals(1, result.getContent().size());
//        verify(taskRepository).findByStatus(TaskStatus.PENDING, pageable);
//    }
//
//    @Test
//    void testGetPagedTasks_FilterByPriority() {
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<Task> mockPage = new PageImpl<>(List.of(new Task()));
//
//        when(taskRepository.findByPriority(TaskPriority.HIGH, pageable)).thenReturn(mockPage);
//
//        Page<Task> result = taskService.getPagedTasks(pageable, null, TaskPriority.HIGH, null);
//
//        assertEquals(1, result.getContent().size());
//        verify(taskRepository).findByPriority(TaskPriority.HIGH, pageable);
//    }
//
//
//    @Test
//    void testGetPagedTasks_FilterByKeyword() {
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<Task> mockPage = new PageImpl<>(List.of(new Task()));
//
//        when(taskRepository.findByTitleContainingIgnoreCase("meeting", pageable))
//                .thenReturn(mockPage);
//
//        Page<Task> result = taskService.getPagedTasks(pageable, null, null, "meeting");
//
//        assertEquals(1, result.getContent().size());
//        verify(taskRepository).findByTitleContainingIgnoreCase("meeting", pageable);
//    }
//
//    @Test
//    void testGetPagedTasks_NoFilters() {
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<Task> mockPage = new PageImpl<>(List.of(new Task()));
//
//        when(taskRepository.findAll(pageable)).thenReturn(mockPage);
//
//        Page<Task> result = taskService.getPagedTasks(pageable, null, null, "");
//
//        assertEquals(1, result.getContent().size());
//        verify(taskRepository).findAll(pageable);
//    }
//}
//
