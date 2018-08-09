package com.test.selfie.data.entity.mapper;

import com.google.gson.reflect.TypeToken;
import com.test.selfie.data.entity.AuthEntity;
import com.test.selfie.data.entity.PictureEntity;
import com.test.selfie.shared.GsonSingleton;

import java.lang.reflect.Type;
import java.util.List;

public class JsonMapper {

    public static AuthEntity transformAuthEntity(String json) {
        return GsonSingleton.getGson().fromJson(json, AuthEntity.class);
    }

    public static List<PictureEntity> transformPictureEntities(String json) {
        Type type = new TypeToken<List<PictureEntity>>(){}.getType();
        return GsonSingleton.getGson().fromJson(json, type);
    }

    public static PictureEntity transformPictureEntity(String json) {
        return GsonSingleton.getGson().fromJson(json, PictureEntity.class);
    }

}
