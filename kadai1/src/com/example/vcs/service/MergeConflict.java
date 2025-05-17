package com.example.vcs.service;

public record MergeConflict(String filename, String base, String left, String right) { }