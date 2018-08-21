package com.test.selfie.domain.usecase;

import com.test.selfie.data.entity.AuthEntity;
import com.test.selfie.data.entity.PictureEntity;
import com.test.selfie.domain.AuthorizationRepository;
import com.test.selfie.domain.GalleryRepository;
import com.test.selfie.domain.model.Picture;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GalleryUseCaseTest {

    private GalleryUseCase mUseCase;

    @Mock
    private GalleryRepository mGalleyRepository;

    @Mock
    private AuthorizationRepository mAuthRepository;

    @Mock
    private PictureEntity mPictureEntity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        AuthEntity authEntity = Mockito.mock(AuthEntity.class);
        when(authEntity.getAccessToken()).thenReturn("accessToken");
        when(mAuthRepository.getAuthorization()).thenReturn(Single.just(authEntity));

        // I used WHEN here because this class PictureEntity doesn't have setters
        when(mPictureEntity.getId()).thenReturn("id");
        when(mPictureEntity.getTitle()).thenReturn("title");
        when(mPictureEntity.getThumbnail()).thenReturn("thumbnail");
        when(mPictureEntity.getUrl()).thenReturn("url");

        List<PictureEntity> pictureEntities = new ArrayList<>();
        pictureEntities.add(mPictureEntity);
        when(mGalleyRepository.fetchPictures(anyString()))
                .thenReturn(Single.just(pictureEntities));

        when(mGalleyRepository.uploadPicture(anyString(), any(), anyString()))
                .thenReturn(Single.just(mPictureEntity));

        when(mGalleyRepository.deletePicture(anyString(), anyString()))
                .thenReturn(Completable.complete());

        mUseCase = new GalleryUseCaseImpl(mGalleyRepository, mAuthRepository);
    }

    @Test
    public void fetchPictures() {
        TestObserver<List<Picture>> observer = mUseCase.fetchPictures().test();

        verify(mAuthRepository).getAuthorization();
        observer.assertValueCount(1);
        observer.assertValue(pictures -> {
            Picture picture = pictures.get(0);
            return picture.getId().equals(mPictureEntity.getId())
                    && picture.getTitle().equals(mPictureEntity.getTitle())
                    && picture.getThumbnail().equals(mPictureEntity.getThumbnail())
                    && picture.getUrl().equals(mPictureEntity.getUrl());
        });
    }

    @Test
    public void uploadPicture() throws IOException {
        InputStream stream = Mockito.mock(InputStream.class);
        when(stream.read(any())).thenReturn(-1);

        TestObserver<Picture> observer = mUseCase.uploadPicture("", stream).test();

        verify(mAuthRepository).getAuthorization();
        observer.assertValue(picture -> picture.getId().equals(mPictureEntity.getId())
                && picture.getTitle().equals(mPictureEntity.getTitle())
                && picture.getThumbnail().equals(mPictureEntity.getThumbnail())
                && picture.getUrl().equals(mPictureEntity.getUrl()));
    }

    @Test
    public void deletePicture() {
        TestObserver observer = mUseCase.deletePicture("").test();

        verify(mAuthRepository).getAuthorization();
        observer.assertComplete();
    }
}