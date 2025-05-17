package com.example.vcs.dao.sqlite;

import com.example.vcs.dao.UserDao;
import com.example.vcs.db.ConnectionFactory;
import com.example.vcs.model.User;
import java.sql.*;
import java.util.Optional;

public class SqliteUserDao implements UserDao {
    private static final String INSERT_SQL = """
        INSERT INTO name(username)
        VALUES(?);
        """;
    private static final String SELECT_BY_ID = "SELECT id, username FROM name WHERE id = ?;";
    private static final String SELECT_BY_USERNAME = "SELECT id, username FROM name WHERE username = ?;";

    @Override public User insert(User user) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.username());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return new User(rs.getInt(1), user.username());
                throw new SQLException("Failed to insert user");
            }
        }
    }
    @Override public Optional<User> findById(int id) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                    ? Optional.of(new User(rs.getInt("id"), rs.getString("username")))
                    : Optional.empty();
            }
        }
    }
    @Override public Optional<User> findByUsername(String username) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                    ? Optional.of(new User(rs.getInt("id"), rs.getString("username")))
                    : Optional.empty();
            }
        }
    }
}