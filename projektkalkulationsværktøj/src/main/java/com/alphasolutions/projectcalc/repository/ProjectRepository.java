package com.alphasolutions.projectcalc.repository;

import com.alphasolutions.projectcalc.model.Project;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class ProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Project> projectRowMapper = (rs, rowNum) -> {
        Project p = new Project();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setStatus(rs.getString("status"));
        p.setBudget(rs.getDouble("budget"));
        Date startDate = rs.getDate("start_date");
        if (startDate != null) p.setStartDate(startDate.toLocalDate());
        Date deadline = rs.getDate("deadline");
        if (deadline != null) p.setDeadline(deadline.toLocalDate());
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) p.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) p.setUpdatedAt(updatedAt.toLocalDateTime());
        p.setUserId(rs.getInt("user_id"));
        return p;
    };

    public List<Project> findAll() {
        return jdbcTemplate.query("SELECT * FROM projects ORDER BY id", projectRowMapper);
    }

    public List<Project> findByUserId(int userId) {
        return jdbcTemplate.query("SELECT * FROM projects WHERE user_id = ? ORDER BY id",
                projectRowMapper, userId);
    }

    public Optional<Project> findById(int id) {
        List<Project> projects = jdbcTemplate.query(
                "SELECT * FROM projects WHERE id = ?", projectRowMapper, id);
        return projects.stream().findFirst();
    }

    public int save(Project project) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO projects (name, description, status, budget, start_date, deadline, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setString(3, project.getStatus());
            ps.setDouble(4, project.getBudget());
            ps.setDate(5, project.getStartDate() != null ? Date.valueOf(project.getStartDate()) : null);
            ps.setDate(6, project.getDeadline() != null ? Date.valueOf(project.getDeadline()) : null);
            ps.setInt(7, project.getUserId());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key != null ? key.intValue() : 0;
    }

    public int update(Project project) {
        return jdbcTemplate.update(
                "UPDATE projects SET name = ?, description = ?, status = ?, budget = ?, start_date = ?, deadline = ?, user_id = ? WHERE id = ?",
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getBudget(),
                project.getStartDate() != null ? Date.valueOf(project.getStartDate()) : null,
                project.getDeadline() != null ? Date.valueOf(project.getDeadline()) : null,
                project.getUserId(),
                project.getId());
    }

    public int deleteById(int id) {
        return jdbcTemplate.update("DELETE FROM projects WHERE id = ?", id);
    }

    public double sumEstimatedHours(int projectId) {
        Double sum = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(t.estimated_hours), 0) FROM tasks t " +
                        "JOIN sub_projects sp ON t.sub_project_id = sp.id " +
                        "WHERE sp.project_id = ?",
                Double.class, projectId);
        return sum != null ? sum : 0.0;
    }
}
