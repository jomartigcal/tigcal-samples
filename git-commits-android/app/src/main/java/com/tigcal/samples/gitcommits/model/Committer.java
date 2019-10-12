package com.tigcal.samples.gitcommits.model;

import com.google.gson.annotations.SerializedName;

public class Committer {
    @SerializedName("html_url")
    private String htmlUrl;
    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("login")
    private String login;

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatar_url) {
        this.avatarUrl = avatar_url;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
