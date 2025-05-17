// File: src/com/example/vcs/app/Main.java
package com.example.vcs.app;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.example.vcs.dao.DaoFactory;
import com.example.vcs.model.Branch;
import com.example.vcs.model.Commit;
import com.example.vcs.service.MergeStrategy;
import com.example.vcs.service.ThreeWayMergeStrategy;
import com.example.vcs.service.VersionControlService;

public class Main {
    public static void main(String[] args) {
        try {
            // DaoFactory とマージ戦略を用意
            var factory = DaoFactory.create();
            MergeStrategy strategy = new ThreeWayMergeStrategy(factory.createFileDao());
            var vcs = new VersionControlService(factory, strategy);

            // 例：ユーザーID=1, リポジトリID=1 が事前にDBにある前提で"main"ブランチを作成
            Branch branch = vcs.createBranch(1, "main", null);
            System.out.println("Created branch: " + branch);

            // 初回コミット: README.txt
            Map<String, String> contents = new HashMap<>();
            contents.put("README.txt", "Hello world!");
            Commit commit1 = vcs.commit(branch.id(), 1, "Initial commit", contents);
            System.out.println("Commit1: " + commit1);

            // 2回目コミット: README.txt 更新
            contents.put("README.txt", "Hello VCS!");
            Commit commit2 = vcs.commit(branch.id(), 1, "Update README", contents);
            System.out.println("Commit2: " + commit2);

            // （マージを試す場合は下記をアンコメント）
            // Commit mergeCommit = vcs.merge(branch.id(), someOtherBranchId, 1, "Merge branch");
            // System.out.println("MergeCommit: " + mergeCommit);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}