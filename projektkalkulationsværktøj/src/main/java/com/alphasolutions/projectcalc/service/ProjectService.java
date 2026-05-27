package com.alphasolutions.projectcalc.service;

import com.alphasolutions.projectcalc.model.Project;
import com.alphasolutions.projectcalc.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        // Enrich with calculated totals
        for (Project p : projects) {
            p.setTotalHours(projectRepository.sumEstimatedHours(p.getId()));
        }
        return projects;
    }

    public List<Project> getProjectsByUserId(int userId) {
        List<Project> projects = projectRepository.findByUserId(userId);
        for (Project p : projects) {
            p.setTotalHours(projectRepository.sumEstimatedHours(p.getId()));
        }
        return projects;
    }

    public Optional<Project> getProjectById(int id) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        projectOpt.ifPresent(p -> p.setTotalHours(projectRepository.sumEstimatedHours(p.getId())));
        return projectOpt;
    }

    public int createProject(Project project) {
        validateProject(project);
        return projectRepository.save(project);
    }

    public int updateProject(Project project) {
        validateProject(project);
        return projectRepository.update(project);
    }

    public int deleteProject(int id) {
        return projectRepository.deleteById(id);
    }

    /**
     * Calculate number of workdays (Mon-Fri) between project start and deadline.
     */
    public long calculateWorkDays(Project project) {
        if (project.getStartDate() == null || project.getDeadline() == null) {
            return 0;
        }
        LocalDate start = project.getStartDate();
        LocalDate end = project.getDeadline();
        long workDays = 0;
        while (!start.isAfter(end)) {
            int dayOfWeek = start.getDayOfWeek().getValue();
            if (dayOfWeek < 6) { // Mon-Fri = 1-5
                workDays++;
            }
            start = start.plusDays(1);
        }
        return workDays;
    }

    /**
     * Estimate end date based on total hours and 8 hours per workday.
     */
    public LocalDate calculateEstimatedEndDate(Project project, double hoursPerDay) {
        if (project.getStartDate() == null || hoursPerDay <= 0) {
            return null;
        }
        double totalHours = projectRepository.sumEstimatedHours(project.getId());
        long requiredWorkDays = (long) Math.ceil(totalHours / hoursPerDay);
        LocalDate date = project.getStartDate();
        long added = 0;
        while (added < requiredWorkDays) {
            date = date.plusDays(1);
            if (date.getDayOfWeek().getValue() < 6) {
                added++;
            }
        }
        return date;
    }

    private void validateProject(Project project) {
        if (project.getName() == null || project.getName().isBlank()) {
            throw new IllegalArgumentException("Projektnavn er paakraevet");
        }
        if (project.getStartDate() != null && project.getDeadline() != null
                && project.getDeadline().isBefore(project.getStartDate())) {
            throw new IllegalArgumentException("Deadline kan ikke vaere foer startdato");
        }
    }
}
