package com.example.vcs.service;

import com.example.vcs.dao.FileDao;
import com.example.vcs.model.FileEntry;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

public class ThreeWayMergeStrategy implements MergeStrategy {
    private final FileDao fileDao;
    public ThreeWayMergeStrategy(FileDao fileDao) { this.fileDao = fileDao; }

    @Override public MergeResult merge(int base, int left, int right) throws SQLException {
        List<FileEntry> baseFiles  = fileDao.findByCommitId(base);
        List<FileEntry> leftFiles  = fileDao.findByCommitId(left);
        List<FileEntry> rightFiles = fileDao.findByCommitId(right);
        Set<String> names = new HashSet<>();
        Stream.of(baseFiles, leftFiles, rightFiles)
            .flatMap(Collection::stream)
            .map(FileEntry::filename)
            .forEach(names::add);
        List<FileEntry> merged    = new ArrayList<>();
        List<MergeConflict> conflicts = new ArrayList<>();
        for (String name : names) {
            String b = findContent(baseFiles,  name);
            String l = findContent(leftFiles,  name);
            String r = findContent(rightFiles, name);
            if (Objects.equals(l, r)) {
                merged.add(new FileEntry(0,0,name,l));
            } else if (Objects.equals(b, l)) {
                merged.add(new FileEntry(0,0,name,r));
            } else if (Objects.equals(b, r)) {
                merged.add(new FileEntry(0,0,name,l));
            } else {
                conflicts.add(new MergeConflict(name, b, l, r));
            }
        }
        if (!conflicts.isEmpty()) return new MergeResult.Conflict(conflicts);
        return new MergeResult.Merged(merged);
    }
    private String findContent(List<FileEntry> list, String name) {
        return list.stream()
            .filter(f -> f.filename().equals(name))
            .map(FileEntry::content)
            .findFirst().orElse("");
    }
}