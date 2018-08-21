package com.test.selfie.data.repository;

import com.test.selfie.data.datasource.gallery.GalleryDataSource;
import com.test.selfie.data.entity.PictureEntity;
import com.test.selfie.domain.GalleryRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GalleryRepositoryTest {

    private GalleryRepository mRepository;

    @Mock
    private PictureEntity mEntity;

    private List<PictureEntity> mEntities;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        GalleryDataSource dataSource = Mockito.mock(GalleryDataSource.class);

        mEntities = new ArrayList<>();
        mEntities.add(mEntity);

        when(dataSource.fetchPictures(anyString())).thenReturn(Single.just(mEntities));
        when(dataSource.uploadPicture(anyString(), any(), anyString()))
                .thenReturn(Single.just(mEntity));
        when(dataSource.deletePicture(anyString(), anyString())).thenReturn(Completable.complete());

        mRepository = new GalleryRepositoryImpl(dataSource);
    }

    @Test
    public void fetchPictures() {
        TestObserver<List<PictureEntity>> observer = mRepository
                .fetchPictures(anyString())
                .test();

        observer.assertSubscribed();
        observer.assertValue(mEntities);
        observer.assertValueCount(1);
    }

    @Test
    public void uploadPicture() {
        TestObserver<PictureEntity> observer = mRepository
                .uploadPicture(anyString(), any(), anyString())
                .test();

        observer.assertSubscribed();
        observer.assertValue(mEntity);
    }

    @Test
    public void deletePicture() {
        TestObserver observer = mRepository
                .deletePicture(anyString(), anyString())
                .test();

        observer.assertSubscribed();
        observer.assertComplete();
    }
}