package com.tigcal.samples.gitcommits.model;

import com.google.gson.annotations.SerializedName;

public class Commit {

    @SerializedName("committer")
    private CommitCommitter commitCommitter;
    @SerializedName("message")
    private String message;

    public CommitCommitter getCommitCommitter() {
        return commitCommitter;
    }

    public void setCommitCommitter(CommitCommitter commitCommitter) {
        this.commitCommitter = commitCommitter;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
