package com.example.vcs.dao;

import com.example.vcs.model.Repository;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RepositoryDao {
    Repository insert(Repository repository) throws SQLException;
    Optional<Repository> findById(int id) throws SQLException;
    List<Repository> findByOwnerId(int ownerId) throws SQLException;
}