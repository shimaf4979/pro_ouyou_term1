package com.example.vcs.model;

public record FileEntry(int id, int commitId, String filename, String content) { }
