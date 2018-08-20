package com.test.selfie.data.datasource.gallery;

import com.test.selfie.data.datasource.authorization.AuthorizationDataSource;
import com.test.selfie.data.entity.PictureEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface GalleryDataSource {

    /**
     * Fetches a list of pictures from Google Drive
     *
     * @param accessToken The access token retrieved from {@link AuthorizationDataSource}
     * @return A list of {@link PictureEntity}
     * */
    Single<List<PictureEntity>> fetchPictures(String accessToken);

    /**
     * Uploads a picture into Google Drive
     *
     * @param name The image's name
     * @param imageData The image's data
     * @param accessToken The access token retrieved from {@link AuthorizationDataSource}
     * @return The object {@link PictureEntity} uploaded
     * */
    Single<PictureEntity> uploadPicture(String name, byte[] imageData, String accessToken);

    /**
     * Deletes a picture from Google Drive
     *
     * @param id The picture's id
     * @param accessToken The access token retrieved from {@link AuthorizationDataSource}
     * @return A completable
     * */
    Completable deletePicture(String id, String accessToken);

}
