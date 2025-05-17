package com.example.vcs.dao.sqlite;

import com.example.vcs.dao.FileDao;
import com.example.vcs.db.ConnectionFactory;
import com.example.vcs.model.FileEntry;
import java.sql.*;
import java.util.*;

public class SqliteFileDao implements FileDao {
    private static final String INSERT_SQL = """
        INSERT INTO file(commit_id, filename, content)
        VALUES(?, ?, ?);
        """;
    private static final String SELECT_BY_COMMIT = "SELECT id, commit_id, filename, content FROM file WHERE commit_id = ?;";

    @Override public FileEntry insert(FileEntry file) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, file.commitId());
            ps.setString(2, file.filename());
            ps.setString(3, file.content());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return new FileEntry(rs.getInt(1), file.commitId(), file.filename(), file.content());
                throw new SQLException("Failed to insert file");
            }
        }
    }
    @Override public List<FileEntry> findByCommitId(int commitId) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_COMMIT)) {
            ps.setInt(1, commitId);
            try (ResultSet rs = ps.executeQuery()) {
                List<FileEntry> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new FileEntry(rs.getInt("id"), rs.getInt("commit_id"), rs.getString("filename"), rs.getString("content")));
                }
                return list;
            }
        }
    }
}