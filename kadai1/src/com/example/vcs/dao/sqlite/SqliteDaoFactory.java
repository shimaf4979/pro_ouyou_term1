package com.example.vcs.dao.sqlite;

import com.example.vcs.dao.*;

public class SqliteDaoFactory extends DaoFactory {
    @Override public UserDao createUserDao() { return new SqliteUserDao(); }
    @Override public RepositoryDao createRepositoryDao() { return new SqliteRepositoryDao(); }
    @Override public BranchDao createBranchDao() { return new SqliteBranchDao(); }
    @Override public CommitDao createCommitDao() { return new SqliteCommitDao(); }
    @Override public FileDao createFileDao() { return new SqliteFileDao(); }
}