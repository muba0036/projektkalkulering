package com.alphasolutions.projectcalc.service;

import com.alphasolutions.projectcalc.model.Project;
import com.alphasolutions.projectcalc.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    private ProjectRepository projectRepository;
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        projectService = new ProjectService(projectRepository);
    }

    @Test
    void getAllProjects_enrichesWithTotalHours() {
        Project p = new Project(1, "Test", "desc", LocalDate.now(), LocalDate.now().plusDays(10), 1);
        when(projectRepository.findAll()).thenReturn(Arrays.asList(p));
        when(projectRepository.sumEstimatedHours(1)).thenReturn(40.0);

        var result = projectService.getAllProjects();

        assertEquals(1, result.size());
        assertEquals(40.0, result.get(0).getTotalHours());
    }

    @Test
    void createProject_throwsWhenNameIsEmpty() {
        Project p = new Project(0, "", null, null, null, 1);
        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(p));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_throwsWhenDeadlineBeforeStart() {
        Project p = new Project(0, "Test", null,
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 5, 1), 1);
        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(p));
    }

    @Test
    void createProject_savesValidProject() {
        Project p = new Project(0, "Valid Name", "desc",
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1), 1);
        when(projectRepository.save(p)).thenReturn(42);

        int id = projectService.createProject(p);

        assertEquals(42, id);
        verify(projectRepository).save(p);
    }

    @Test
    void calculateWorkDays_countsMondayToFriday() {
        // Mon 2026-05-04 to Fri 2026-05-08 = 5 workdays
        Project p = new Project(1, "X", null, LocalDate.of(2026, 5, 4), LocalDate.of(2026, 5, 8), 1);
        assertEquals(5, projectService.calculateWorkDays(p));
    }

    @Test
    void calculateWorkDays_skipsWeekends() {
        // Fri 2026-05-08 to Mon 2026-05-11 = 2 workdays (Fri + Mon)
        Project p = new Project(1, "X", null, LocalDate.of(2026, 5, 8), LocalDate.of(2026, 5, 11), 1);
        assertEquals(2, projectService.calculateWorkDays(p));
    }

    @Test
    void calculateWorkDays_zeroWhenNoDates() {
        Project p = new Project(1, "X", null, null, null, 1);
        assertEquals(0, projectService.calculateWorkDays(p));
    }

    @Test
    void calculateEstimatedEndDate_returnsCorrectDate() {
        Project p = new Project(1, "X", null, LocalDate.of(2026, 5, 4), null, 1);
        when(projectRepository.sumEstimatedHours(1)).thenReturn(40.0);

        LocalDate end = projectService.calculateEstimatedEndDate(p, 8.0);

        // 40h / 8h per day = 5 days, starting Mon -> Mon+5 workdays = next Mon (2026-05-11)
        assertEquals(LocalDate.of(2026, 5, 11), end);
    }

    @Test
    void getProjectById_returnsEmptyWhenNotFound() {
        when(projectRepository.findById(99)).thenReturn(Optional.empty());
        assertTrue(projectService.getProjectById(99).isEmpty());
    }

    @Test
    void deleteProject_callsRepository() {
        when(projectRepository.deleteById(1)).thenReturn(1);
        assertEquals(1, projectService.deleteProject(1));
        verify(projectRepository).deleteById(1);
    }
}
