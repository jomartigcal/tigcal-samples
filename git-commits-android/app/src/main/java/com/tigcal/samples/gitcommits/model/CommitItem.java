package com.tigcal.samples.gitcommits.model;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CommitItem {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD'T'HH:MM:SSZ", Locale.getDefault());

    @SerializedName("sha")
    private String sha;
    @SerializedName("commit")
    private Commit commit;
    @SerializedName("committer")
    private Committer committer;

    private boolean selected;

    public String getDate() {
        if (commit.getCommitCommitter() != null && commit.getCommitCommitter().getDate() != null) {
            return commit.getCommitCommitter().getDate();
        } else {
            return "Date not available";
        }
    }

    public String getMessage() {
        return commit == null ? "Message not available" : commit.getMessage();
    }

    public String getLogin() {
        return committer == null ? "Login not available" : committer.getLogin();
    }

    public String getAvatarUrl() {
        return committer == null ? "" : committer.getAvatarUrl();
    }

    public String getHtmlUrl() {
        return committer == null ? "" : committer.getHtmlUrl();
    }

    public String getSha() {
        return sha;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
