package com.test.selfie.data.datasource.gallery;

import com.test.selfie.data.datasource.authorization.AuthorizationDataSource;
import com.test.selfie.data.entity.PictureEntity;

import java.util.List;

import io.reactivex.Single;

public interface GalleryDataSource {

    /**
     * Fetch a list of pictures from Google Storage
     *
     * @param accessToken The access token retrieved from {@link AuthorizationDataSource}
     * @return A list of {@link PictureEntity}
     * */
    Single<List<PictureEntity>> fetchPictures(String accessToken);

    /**
     * Fetch a list of pictures from Google Storage
     *
     * @param name The image's name
     * @param imageData The image's data
     * @param accessToken The access token retrieved from {@link AuthorizationDataSource}
     * @return The object {@link PictureEntity} uploaded
     * */
    Single<PictureEntity> uploadPicture(String name, byte[] imageData, String accessToken);

}
