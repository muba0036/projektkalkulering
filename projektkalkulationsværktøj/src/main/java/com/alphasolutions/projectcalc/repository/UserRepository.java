package com.alphasolutions.projectcalc.repository;

import com.alphasolutions.projectcalc.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("role")
    );

    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", userRowMapper);
    }

    public Optional<User> findById(int id) {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users WHERE id = ?", userRowMapper, id);
        return users.stream().findFirst();
    }

    public Optional<User> findByUsername(String username) {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users WHERE username = ?", userRowMapper, username);
        return users.stream().findFirst();
    }

    public int save(User user) {
        return jdbcTemplate.update(
                "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
                user.getUsername(), user.getPassword(), user.getRole());
    }

    public int update(User user) {
        return jdbcTemplate.update(
                "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?",
                user.getUsername(), user.getPassword(), user.getRole(), user.getId());
    }

    public int deleteById(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }
}
