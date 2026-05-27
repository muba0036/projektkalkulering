package com.alphasolutions.projectcalc.service;

import com.alphasolutions.projectcalc.model.Task;
import com.alphasolutions.projectcalc.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    public static final String STATUS_NOT_STARTED = "NOT_STARTED";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_DONE = "DONE";

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getTasksBySubProjectId(int subProjectId) {
        return taskRepository.findBySubProjectId(subProjectId);
    }

    public Optional<Task> getTaskById(int id) {
        return taskRepository.findById(id);
    }

    public int createTask(Task task) {
        validate(task);
        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus(STATUS_NOT_STARTED);
        }
        return taskRepository.save(task);
    }

    public int updateTask(Task task) {
        validate(task);
        return taskRepository.update(task);
    }

    public int deleteTask(int id) {
        return taskRepository.deleteById(id);
    }

    /**
     * Estimate CO2 footprint - 0.5 kg CO2 per hour of work (laptop + office heating average).
     * Simple approximation, useful for ESG reporting.
     */
    public double calculateCo2Footprint(List<Task> tasks) {
        double totalHours = tasks.stream().mapToDouble(Task::getEstimatedHours).sum();
        return totalHours * 0.5;
    }

    private void validate(Task task) {
        if (task.getName() == null || task.getName().isBlank()) {
            throw new IllegalArgumentException("Opgavenavn er paakraevet");
        }
        if (task.getEstimatedHours() < 0) {
            throw new IllegalArgumentException("Estimerede timer kan ikke vaere negative");
        }
        if (task.getSubProjectId() <= 0) {
            throw new IllegalArgumentException("Opgave skal tilhoere et delprojekt");
        }
    }
}
