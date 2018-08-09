package com.test.selfie.data.entity;

import com.google.gson.annotations.SerializedName;

public class PictureEntity {

    private String id;
    private String name;

    @SerializedName("mediaLink")
    private String path;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

}
