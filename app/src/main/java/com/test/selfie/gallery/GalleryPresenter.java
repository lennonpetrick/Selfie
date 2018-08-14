package com.test.selfie.gallery;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.test.selfie.R;
import com.test.selfie.domain.model.Picture;
import com.test.selfie.domain.usecase.GalleryUseCase;

import java.io.InputStream;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class GalleryPresenter implements GalleryContract.Presenter {

    private GalleryContract.View mView;
    private CompositeDisposable mDisposable;
    private GalleryUseCase mUseCase;

    public GalleryPresenter(GalleryContract.View view,
                            GalleryUseCase useCase) {
        mView = view;
        mUseCase = useCase;
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void destroy() {
        mDisposable.clear();
        mDisposable = null;
    }

    @Override
    public void savePicture(@Nullable String name,
                            @Nullable InputStream stream) {
        if (stream == null || TextUtils.isEmpty(name))
            return;

        mView.showProgress(R.string.message_gallery_save);
        mDisposable.add(save(name, stream)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> mView.hideProgress())
                .subscribe(picture -> {
                    if (picture != null) {
                        mView.addInGallery(picture);
                    }
                }, e -> {
                    e.printStackTrace();
                    String error = e.getMessage();
                    if (e instanceof VolleyError) {
                        error = new String(((VolleyError) e).networkResponse.data);
                    }
                    mView.showError(error);
                }));
    }

    @Override
    public void loadPictures() {
        mView.showProgress(R.string.message_gallery_load);
        mDisposable.add(fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> mView.hideProgress())
                .subscribe(pictures -> {
                    if (pictures == null) {
                        mView.showGalleryEmpty(true);
                    } else {
                        mView.showGalleryEmpty(false);
                        mView.refreshGallery(pictures);
                    }
                }, e -> {
                    e.printStackTrace();
                    String error = e.getMessage();
                    if (e instanceof VolleyError) {
                        error = new String(((VolleyError) e).networkResponse.data);
                    }
                    mView.showError(error);
                }));
    }

    private Single<Picture> save(String name, InputStream stream) {
        return mUseCase.uploadPicture(name, stream);
    }

    private Single<List<Picture>> fetch() {
        return mUseCase.fetchPictures();
    }

}
