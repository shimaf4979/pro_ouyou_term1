package com.example.vcs.dao;

public abstract class DaoFactory {
    public abstract UserDao createUserDao();
    public abstract RepositoryDao createRepositoryDao();
    public abstract BranchDao createBranchDao();
    public abstract CommitDao createCommitDao();
    public abstract FileDao createFileDao();

    public static DaoFactory create() {
        return new com.example.vcs.dao.sqlite.SqliteDaoFactory();
    }
}