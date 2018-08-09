package com.test.selfie.domain;

import com.test.selfie.data.entity.PictureEntity;

import java.util.List;

import io.reactivex.Single;

public interface GalleryRepository {

    Single<List<PictureEntity>> fetchPictures(String accessToken);

    Single<PictureEntity> uploadPicture(String name, byte[] imageData, String accessToken);

}
