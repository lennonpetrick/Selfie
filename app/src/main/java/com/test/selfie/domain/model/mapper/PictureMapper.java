package com.test.selfie.domain.model.mapper;

import com.test.selfie.data.entity.PictureEntity;
import com.test.selfie.domain.model.Picture;

import java.util.ArrayList;
import java.util.List;

public class PictureMapper {

    public static Picture transform(PictureEntity entity) {
        if (entity == null)
            return null;

        Picture model = new Picture();
        model.setName(entity.getName());
        model.setPath(entity.getPath());
        return model;
    }

    public static List<Picture> transform(List<PictureEntity> entities) {
        if (entities == null || entities.isEmpty())
            return null;

        List<Picture> models = new ArrayList<>();
        for (PictureEntity entity : entities) {
            models.add(transform(entity));
        }

        return models;
    }
}