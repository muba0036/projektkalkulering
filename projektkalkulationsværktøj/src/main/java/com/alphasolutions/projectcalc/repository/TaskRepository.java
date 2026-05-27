package com.alphasolutions.projectcalc.repository;

import com.alphasolutions.projectcalc.model.Task;
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
public class TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> {
        Task t = new Task();
        t.setId(rs.getInt("id"));
        t.setName(rs.getString("name"));
        t.setDescription(rs.getString("description"));
        t.setEstimatedHours(rs.getDouble("estimated_hours"));
        t.setActualHours(rs.getDouble("actual_hours"));
        t.setHourlyRate(rs.getDouble("hourly_rate"));
        t.setMaterialUnits(rs.getDouble("material_units"));
        t.setMaterialCostPerUnit(rs.getDouble("material_cost_per_unit"));
        t.setBudgetedCost(rs.getDouble("budgeted_cost"));
        Date deadline = rs.getDate("deadline");
        if (deadline != null) t.setDeadline(deadline.toLocalDate());
        t.setStatus(rs.getString("status"));
        t.setSubProjectId(rs.getInt("sub_project_id"));
        return t;
    };

    public List<Task> findAll() {
        return jdbcTemplate.query("SELECT * FROM tasks ORDER BY id", taskRowMapper);
    }

    public List<Task> findBySubProjectId(int subProjectId) {
        return jdbcTemplate.query(
                "SELECT * FROM tasks WHERE sub_project_id = ? ORDER BY id",
                taskRowMapper, subProjectId);
    }

    public Optional<Task> findById(int id) {
        List<Task> list = jdbcTemplate.query(
                "SELECT * FROM tasks WHERE id = ?", taskRowMapper, id);
        return list.stream().findFirst();
    }

    public int save(Task task) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO tasks (name, description, estimated_hours, actual_hours, hourly_rate, " +
                            "material_units, material_cost_per_unit, budgeted_cost, deadline, status, sub_project_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, task.getName());
            ps.setString(2, task.getDescription());
            ps.setDouble(3, task.getEstimatedHours());
            ps.setDouble(4, task.getActualHours());
            ps.setDouble(5, task.getHourlyRate());
            ps.setDouble(6, task.getMaterialUnits());
            ps.setDouble(7, task.getMaterialCostPerUnit());
            ps.setDouble(8, task.getBudgetedCost());
            ps.setDate(9, task.getDeadline() != null ? Date.valueOf(task.getDeadline()) : null);
            ps.setString(10, task.getStatus());
            ps.setInt(11, task.getSubProjectId());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key != null ? key.intValue() : 0;
    }

    public int update(Task task) {
        return jdbcTemplate.update(
                "UPDATE tasks SET name = ?, description = ?, estimated_hours = ?, actual_hours = ?, " +
                        "hourly_rate = ?, material_units = ?, material_cost_per_unit = ?, budgeted_cost = ?, " +
                        "deadline = ?, status = ?, sub_project_id = ? WHERE id = ?",
                task.getName(),
                task.getDescription(),
                task.getEstimatedHours(),
                task.getActualHours(),
                task.getHourlyRate(),
                task.getMaterialUnits(),
                task.getMaterialCostPerUnit(),
                task.getBudgetedCost(),
                task.getDeadline() != null ? Date.valueOf(task.getDeadline()) : null,
                task.getStatus(),
                task.getSubProjectId(),
                task.getId());
    }

    public int deleteById(int id) {
        return jdbcTemplate.update("DELETE FROM tasks WHERE id = ?", id);
    }
}
