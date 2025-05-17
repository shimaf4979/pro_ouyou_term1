package com.example.vcs.model;

public record Branch(int id, int repositoryId, String name, Integer headCommitId) { }