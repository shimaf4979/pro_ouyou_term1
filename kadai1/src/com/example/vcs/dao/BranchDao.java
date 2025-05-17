package com.example.vcs.dao;

import com.example.vcs.model.Branch;
import java.sql.SQLException;
import java.util.Optional;

public interface BranchDao {
    Branch insert(Branch branch) throws SQLException;
    Optional<Branch> findById(int id) throws SQLException;
    void updateHead(int branchId, int commitId) throws SQLException;
}