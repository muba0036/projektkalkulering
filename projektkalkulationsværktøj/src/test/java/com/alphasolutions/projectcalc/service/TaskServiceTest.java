package com.alphasolutions.projectcalc.service;

import com.alphasolutions.projectcalc.model.Task;
import com.alphasolutions.projectcalc.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskService = new TaskService(taskRepository);
    }

    @Test
    void createTask_setsDefaultStatusWhenMissing() {
        Task t = new Task();
        t.setName("Test");
        t.setSubProjectId(1);
        t.setEstimatedHours(8);
        when(taskRepository.save(t)).thenReturn(1);

        taskService.createTask(t);

        assertEquals("NOT_STARTED", t.getStatus());
        verify(taskRepository).save(t);
    }

    @Test
    void createTask_throwsWhenNameEmpty() {
        Task t = new Task();
        t.setName("");
        t.setSubProjectId(1);
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(t));
    }

    @Test
    void createTask_throwsWhenNoSubProject() {
        Task t = new Task();
        t.setName("Test");
        t.setSubProjectId(0);
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(t));
    }

    @Test
    void createTask_throwsWhenEstimatedHoursNegative() {
        Task t = new Task();
        t.setName("Test");
        t.setSubProjectId(1);
        t.setEstimatedHours(-5);
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(t));
    }

    @Test
    void calculateCo2Footprint_calculates() {
        Task t1 = new Task();
        t1.setEstimatedHours(10);
        Task t2 = new Task();
        t2.setEstimatedHours(20);

        double co2 = taskService.calculateCo2Footprint(Arrays.asList(t1, t2));

        // 30 hours * 0.5 = 15 kg CO2
        assertEquals(15.0, co2, 0.001);
    }

    @Test
    void calculateCo2Footprint_zeroForEmptyList() {
        assertEquals(0.0, taskService.calculateCo2Footprint(Arrays.asList()));
    }
}
