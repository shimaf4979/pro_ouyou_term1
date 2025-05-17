package com.example.vcs.dao;

import com.example.vcs.model.Commit;
import java.sql.SQLException;
import java.util.Optional;

public interface CommitDao {
    Commit insert(Commit commit) throws SQLException;
    Optional<Commit> findById(int id) throws SQLException;
}