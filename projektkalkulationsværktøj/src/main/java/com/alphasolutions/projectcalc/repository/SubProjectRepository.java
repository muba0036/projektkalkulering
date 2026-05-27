package com.alphasolutions.projectcalc.repository;

import com.alphasolutions.projectcalc.model.SubProject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class SubProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<SubProject> subProjectRowMapper = (rs, rowNum) -> {
        SubProject sp = new SubProject();
        sp.setId(rs.getInt("id"));
        sp.setName(rs.getString("name"));
        sp.setDescription(rs.getString("description"));
        Date deadline = rs.getDate("deadline");
        if (deadline != null) sp.setDeadline(deadline.toLocalDate());
        sp.setProjectId(rs.getInt("project_id"));
        sp.setStatus(rs.getString("status"));
        return sp;
    };

    public List<SubProject> findAll() {
        return jdbcTemplate.query("SELECT * FROM sub_projects ORDER BY id", subProjectRowMapper);
    }

    public List<SubProject> findByProjectId(int projectId) {
        return jdbcTemplate.query(
                "SELECT * FROM sub_projects WHERE project_id = ? ORDER BY id",
                subProjectRowMapper, projectId);
    }

    public Optional<SubProject> findById(int id) {
        List<SubProject> list = jdbcTemplate.query(
                "SELECT * FROM sub_projects WHERE id = ?", subProjectRowMapper, id);
        return list.stream().findFirst();
    }

    public int save(SubProject subProject) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO sub_projects (name, description, deadline, project_id) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, subProject.getName());
            ps.setString(2, subProject.getDescription());
            ps.setDate(3, subProject.getDeadline() != null ? Date.valueOf(subProject.getDeadline()) : null);
            ps.setInt(4, subProject.getProjectId());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key != null ? key.intValue() : 0;
    }

    public int update(SubProject subProject) {
        return jdbcTemplate.update(
                "UPDATE sub_projects SET name = ?, description = ?, deadline = ?, status = ?, project_id = ? WHERE id = ?",
                subProject.getName(),
                subProject.getDescription(),
                subProject.getDeadline() != null ? Date.valueOf(subProject.getDeadline()) : null,
                subProject.getStatus(),
                subProject.getProjectId(),
                subProject.getId());
    }

    public int updateStatus(int id, String status) {
        return jdbcTemplate.update("UPDATE sub_projects SET status = ? WHERE id = ?", status, id);
    }

    public int deleteById(int id) {
        return jdbcTemplate.update("DELETE FROM sub_projects WHERE id = ?", id);
    }

    /**
     * Sum total estimated hours for all tasks under a specific sub-project.
     */
    public double sumEstimatedHours(int subProjectId) {
        Double sum = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(estimated_hours), 0) FROM tasks WHERE sub_project_id = ?",
                Double.class, subProjectId);
        return sum != null ? sum : 0.0;
    }
}
