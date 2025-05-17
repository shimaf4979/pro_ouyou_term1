package com.example.vcs.service;

import java.sql.SQLException;

public interface MergeStrategy {
    MergeResult merge(int baseCommit, int leftCommit, int rightCommit) throws SQLException;
}