// File: src/com/example/vcs/service/VersionControlService.java
package com.example.vcs.service;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.vcs.dao.BranchDao;
import com.example.vcs.dao.CommitDao;
import com.example.vcs.dao.DaoFactory;
import com.example.vcs.dao.FileDao;
import com.example.vcs.model.Branch;
import com.example.vcs.model.Commit;
import com.example.vcs.model.FileEntry;

/**
 * シンプルTXT専用バージョン管理のサービス層
 * Strategyパターンでマージアルゴリズムを切り替え可能
 */
public class VersionControlService {
    private final BranchDao branchDao;
    private final CommitDao commitDao;
    private final FileDao fileDao;
    private final MergeStrategy mergeStrategy;

    public VersionControlService(DaoFactory daoFactory, MergeStrategy mergeStrategy) {
        this.branchDao = daoFactory.createBranchDao();
        this.commitDao = daoFactory.createCommitDao();
        this.fileDao = daoFactory.createFileDao();
        this.mergeStrategy = mergeStrategy;
    }

    /**
     * ブランチを作成
     */
    public Branch createBranch(int repositoryId, String name, Integer startPoint) throws SQLException {
        Branch branch = new Branch(0, repositoryId, name, startPoint);
        return branchDao.insert(branch);
    }

    /**
     * コミットを作成（全ファイルのスナップショット）
     */
    public Commit commit(int branchId, int authorId, String message, Map<String, String> contents) throws SQLException {
        // 親コミット取得
        Branch branch = branchDao.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + branchId));
        Integer parentId = branch.headCommitId();
        // 新規コミット作成
        Commit newCommit = new Commit(0, branchId, authorId, message, parentId);
        Commit inserted = commitDao.insert(newCommit);
        // ファイルを全件保存
        for (var entry : contents.entrySet()) {
            FileEntry fe = new FileEntry(0, inserted.id(), entry.getKey(), entry.getValue());
            fileDao.insert(fe);
        }
        // ブランチHEAD更新
        branchDao.updateHead(branchId, inserted.id());
        return inserted;
    }

    /**
     * 3-wayマージを行い、マージコミットを作成
     * コンフリクトがあれば例外で通知
     */
    public Commit merge(int targetBranchId, int sourceBranchId, int authorId, String message)
            throws SQLException, MergeConflictException {
        Branch target = branchDao.findById(targetBranchId)
                .orElseThrow(() -> new IllegalArgumentException("Target branch not found: " + targetBranchId));
        Branch source = branchDao.findById(sourceBranchId)
                .orElseThrow(() -> new IllegalArgumentException("Source branch not found: " + sourceBranchId));
        int leftHead = source.headCommitId();
        int rightHead = target.headCommitId();
        int base = findCommonAncestor(leftHead, rightHead);
        // 3-wayマージ
        MergeResult result = mergeStrategy.merge(base, leftHead, rightHead);
        if (result instanceof MergeResult.Conflict c) {
            // アクセサは conflicts() です
            throw new MergeConflictException(c.conflicts());
        } else if (result instanceof MergeResult.Merged m) {
            // アクセサは mergedFiles() です
            Map<String, String> mergedContents = m.mergedFiles().stream()
                    .collect(Collectors.toMap(FileEntry::filename, FileEntry::content));
            return commit(targetBranchId, authorId, message, mergedContents);
        }
        throw new IllegalStateException("未知のマージ結果");
    }

    /**
     * 単純な共通祖先探索（親を再帰でたどる幅優先）
     */
    private int findCommonAncestor(int commitA, int commitB) throws SQLException {
        Set<Integer> ancestorsA = new HashSet<>();
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(commitA);
        while (!queue.isEmpty()) {
            int cid = queue.poll();
            if (cid <= 0) continue;
            ancestorsA.add(cid);
            commitDao.findById(cid)
                     .map(Commit::parentCommitId)
                     .filter(Objects::nonNull)
                     .ifPresent(queue::add);
        }
        queue.add(commitB);
        while (!queue.isEmpty()) {
            int cid = queue.poll();
            if (ancestorsA.contains(cid)) return cid;
            commitDao.findById(cid)
                     .map(Commit::parentCommitId)
                     .filter(Objects::nonNull)
                     .ifPresent(queue::add);
        }
        return 0; // 祖先なし（初回マージなど）
    }

    // マージ例外
    public static class MergeConflictException extends Exception {
        private final List<MergeConflict> conflicts;
        public MergeConflictException(List<MergeConflict> conflicts) {
            super("Merge conflicts detected");
            this.conflicts = conflicts;
        }
        public List<MergeConflict> getConflicts() {
            return conflicts;
        }
    }
}