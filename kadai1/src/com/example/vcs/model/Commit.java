package com.example.vcs.model;

public record Commit(int id, int branchId, int authorId, String message, Integer parentCommitId) { }