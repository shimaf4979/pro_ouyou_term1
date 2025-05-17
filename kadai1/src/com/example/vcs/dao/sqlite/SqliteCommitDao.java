package com.example.vcs.dao.sqlite;

import com.example.vcs.dao.CommitDao;
import com.example.vcs.db.ConnectionFactory;
import com.example.vcs.model.Commit;
import java.sql.*;
import java.util.Optional;

public class SqliteCommitDao implements CommitDao {
    private static final String INSERT_SQL = """
        INSERT INTO commit(branch_id, author_id, message, parent_commit_id)
        VALUES(?, ?, ?, ?);
        """;
    private static final String SELECT_BY_ID = "SELECT id, branch_id, author_id, message, parent_commit_id FROM commit WHERE id = ?;";

    @Override public Commit insert(Commit commit) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, commit.branchId());
            ps.setInt(2, commit.authorId());
            ps.setString(3, commit.message());
            if (commit.parentCommitId() != null) ps.setInt(4, commit.parentCommitId()); else ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return new Commit(rs.getInt(1), commit.branchId(), commit.authorId(), commit.message(), commit.parentCommitId());
                throw new SQLException("Failed to insert commit");
            }
        }
    }
    @Override public Optional<Commit> findById(int id) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                    ? Optional.of(new Commit(rs.getInt("id"), rs.getInt("branch_id"), rs.getInt("author_id"), rs.getString("message"),
                        rs.getObject("parent_commit_id") != null ? rs.getInt("parent_commit_id") : null))
                    : Optional.empty();
            }
        }
    }
}