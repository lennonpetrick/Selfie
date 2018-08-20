package com.test.selfie.data.entity;

import com.google.gson.annotations.SerializedName;

public class PictureEntity {

    private String id;
    private String title;

    @SerializedName("embedLink")
    private String url;

    @SerializedName("thumbnailLink")
    private String thumbnail;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
