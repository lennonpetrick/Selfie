package com.test.selfie.data.repository;

import com.test.selfie.data.datasource.gallery.GalleryDataSource;
import com.test.selfie.data.entity.PictureEntity;
import com.test.selfie.domain.GalleryRepository;

import java.util.List;

import io.reactivex.Single;

public class GalleryRepositoryImpl implements GalleryRepository {

    private GalleryDataSource mDataSource;

    public GalleryRepositoryImpl(GalleryDataSource dataSource) {
        this.mDataSource = dataSource;
    }

    @Override
    public Single<List<PictureEntity>> fetchPictures(String accessToken) {
        return mDataSource.fetchPictures(accessToken);
    }

    @Override
    public Single<PictureEntity> uploadPicture(String name,
                                               byte[] imageData,
                                               String accessToken) {
        return mDataSource.uploadPicture(name, imageData, accessToken);
    }
}
