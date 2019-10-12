package com.tigcal.samples.gitcommits.model;

import com.google.gson.annotations.SerializedName;

public class CommitCommitter {
    @SerializedName("date")
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
