package com.example.vcs.service;

import com.example.vcs.model.FileEntry;
import java.util.List;

public sealed interface MergeResult permits MergeResult.Merged, MergeResult.Conflict {
    record Merged(List<FileEntry> mergedFiles) implements MergeResult { }
    record Conflict(List<MergeConflict> conflicts) implements MergeResult { }
}