package com.alphasolutions.projectcalc.repository;

import com.alphasolutions.projectcalc.model.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@Import({ProjectRepository.class, SubProjectRepository.class, TaskRepository.class, UserRepository.class})
class ProjectRepositoryIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void findAll_returnsSeededProjects() {
        List<Project> projects = projectRepository.findAll();
        assertFalse(projects.isEmpty(), "data.sql should seed projects");
    }

    @Test
    void save_andFindById_returnsSameData() {
        Project p = new Project(0, "New Project", "Test desc",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 7, 1), 1);

        int id = projectRepository.save(p);
        assertTrue(id > 0);

        Optional<Project> fetched = projectRepository.findById(id);
        assertTrue(fetched.isPresent());
        assertEquals("New Project", fetched.get().getName());
        assertEquals(LocalDate.of(2026, 6, 1), fetched.get().getStartDate());
    }

    @Test
    void update_modifiesProject() {
        Project p = new Project(0, "ToUpdate", "desc",
                LocalDate.now(), LocalDate.now().plusDays(30), 1);
        int id = projectRepository.save(p);
        p.setId(id);
        p.setName("Updated Name");

        int rowsAffected = projectRepository.update(p);
        assertEquals(1, rowsAffected);

        Optional<Project> fetched = projectRepository.findById(id);
        assertTrue(fetched.isPresent());
        assertEquals("Updated Name", fetched.get().getName());
    }

    @Test
    void delete_removesProject() {
        Project p = new Project(0, "ToDelete", "desc",
                LocalDate.now(), LocalDate.now().plusDays(10), 1);
        int id = projectRepository.save(p);

        int deleted = projectRepository.deleteById(id);
        assertEquals(1, deleted);
        assertTrue(projectRepository.findById(id).isEmpty());
    }

    @Test
    void sumEstimatedHours_returnsCorrectTotal() {
        // Seeded project 1 has subprojects 1 and 2 with tasks totaling 16+40+24+32 = 112 hours
        double total = projectRepository.sumEstimatedHours(1);
        assertEquals(112.0, total, 0.001);
    }

    @Test
    void findByUserId_filtersCorrectly() {
        List<Project> projects = projectRepository.findByUserId(2);
        assertFalse(projects.isEmpty());
        assertTrue(projects.stream().allMatch(p -> p.getUserId() == 2));
    }
}
