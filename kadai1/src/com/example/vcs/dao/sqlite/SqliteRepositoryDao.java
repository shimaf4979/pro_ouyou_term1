package com.example.vcs.dao.sqlite;

import com.example.vcs.dao.RepositoryDao;
import com.example.vcs.db.ConnectionFactory;
import com.example.vcs.model.Repository;
import java.sql.*;
import java.util.*;

public class SqliteRepositoryDao implements RepositoryDao {
    private static final String INSERT_SQL = """
        INSERT INTO repository(owner_id, name)
        VALUES(?, ?);
        """;
    private static final String SELECT_BY_ID = "SELECT id, owner_id, name FROM repository WHERE id = ?;";
    private static final String SELECT_BY_OWNER = "SELECT id, owner_id, name FROM repository WHERE owner_id = ?;";

    @Override public Repository insert(Repository repo) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, repo.ownerId());
            ps.setString(2, repo.name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return new Repository(rs.getInt(1), repo.ownerId(), repo.name());
                throw new SQLException("Failed to insert repository");
            }
        }
    }
    @Override public Optional<Repository> findById(int id) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                    ? Optional.of(new Repository(rs.getInt("id"), rs.getInt("owner_id"), rs.getString("name")))
                    : Optional.empty();
            }
        }
    }
    @Override public List<Repository> findByOwnerId(int ownerId) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_OWNER)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Repository> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Repository(rs.getInt("id"), rs.getInt("owner_id"), rs.getString("name")));
                }
                return list;
            }
        }
    }
}