package com.example.vcs.dao;

import com.example.vcs.model.FileEntry;
import java.sql.SQLException;
import java.util.List;

public interface FileDao {
    FileEntry insert(FileEntry file) throws SQLException;
    List<FileEntry> findByCommitId(int commitId) throws SQLException;
}