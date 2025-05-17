package com.example.vcs.dao.sqlite;

import com.example.vcs.dao.BranchDao;
import com.example.vcs.db.ConnectionFactory;
import com.example.vcs.model.Branch;
import java.sql.*;
import java.util.Optional;

public class SqliteBranchDao implements BranchDao {
    private static final String INSERT_SQL = """
        INSERT INTO branch(repository_id, name, head_commit_id)
        VALUES(?, ?, ?);
        """;
    private static final String SELECT_BY_ID = "SELECT id, repository_id, name, head_commit_id FROM branch WHERE id = ?;";
    private static final String UPDATE_HEAD = "UPDATE branch SET head_commit_id = ? WHERE id = ?;";

    @Override public Branch insert(Branch branch) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, branch.repositoryId());
            ps.setString(2, branch.name());
            if (branch.headCommitId() != null) ps.setInt(3, branch.headCommitId()); else ps.setNull(3, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return new Branch(rs.getInt(1), branch.repositoryId(), branch.name(), branch.headCommitId());
                throw new SQLException("Failed to insert branch");
            }
        }
    }
    @Override public Optional<Branch> findById(int id) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                    ? Optional.of(new Branch(rs.getInt("id"), rs.getInt("repository_id"), rs.getString("name"),
                        rs.getObject("head_commit_id") != null ? rs.getInt("head_commit_id") : null))
                    : Optional.empty();
            }
        }
    }
    @Override public void updateHead(int branchId, int commitId) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_HEAD)) {
            ps.setInt(1, commitId);
            ps.setInt(2, branchId);
            ps.executeUpdate();
        }
    }
}