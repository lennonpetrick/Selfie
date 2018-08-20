package com.test.selfie.domain.usecase;

import com.test.selfie.domain.model.Picture;

import java.io.InputStream;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface GalleryUseCase {

    Single<List<Picture>> fetchPictures();

    Single<Picture> uploadPicture(String name, InputStream stream);

    Completable deletePicture(String id);

}
