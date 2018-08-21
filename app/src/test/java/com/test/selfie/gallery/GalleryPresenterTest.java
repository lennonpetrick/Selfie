package com.test.selfie.gallery;

import com.test.selfie.R;
import com.test.selfie.domain.model.Picture;
import com.test.selfie.domain.usecase.GalleryUseCase;
import com.test.selfie.shared.schedulers.TestSchedulers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GalleryPresenterTest {

    private GalleryContract.Presenter mPresenter;

    @Mock
    private GalleryContract.View mView;

    @Mock
    private GalleryUseCase mUseCase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mPresenter = new GalleryPresenter(mView, mUseCase,
                TestSchedulers.getInstance());
    }

    @After
    public void destroy() {
        mPresenter.destroy();
    }

    @Test
    public void savePicture() {
        InputStream stream = Mockito.mock(InputStream.class);
        when(mUseCase.uploadPicture(anyString(), any()))
                .thenReturn(Single.just(new Picture()));

        mPresenter.savePicture("pictureName", stream);

        verify(mView).showProgress(R.string.message_gallery_save);
        verify(mView).addInGallery(any());
        verify(mView).hideProgress();
        verify(mView, never()).showError(anyString());
    }

    @Test
    public void loadPictures() {
        List<Picture> pictures = new ArrayList<>();
        pictures.add(new Picture());
        when(mUseCase.fetchPictures()).thenReturn(Single.just(pictures));

        mPresenter.loadPictures();

        verify(mView).showProgress(R.string.message_gallery_load);
        verify(mView).showGalleryEmpty(false);
        verify(mView).refreshGallery(anyList());
        verify(mView).hideProgress();
        verify(mView, never()).showGalleryEmpty(true);
        verify(mView, never()).showError(anyString());
    }

    @Test
    public void loadPictures_empty() {
        when(mUseCase.fetchPictures()).thenReturn(Single.just(new ArrayList<>()));

        mPresenter.loadPictures();

        verify(mView).showProgress(R.string.message_gallery_load);
        verify(mView).showGalleryEmpty(true);
        verify(mView).hideProgress();
        verify(mView, never()).refreshGallery(anyList());
        verify(mView, never()).showGalleryEmpty(false);
        verify(mView, never()).showError(anyString());
    }

    @Test
    public void deletePicture() {
        when(mUseCase.deletePicture(anyString())).thenReturn(Completable.complete());

        mPresenter.deletePicture("id", 3);

        verify(mView).showProgress(R.string.message_gallery_delete);
        verify(mView).removeFromGallery(3);
        verify(mView).hideProgress();
        verify(mView, never()).showError(anyString());
    }
}