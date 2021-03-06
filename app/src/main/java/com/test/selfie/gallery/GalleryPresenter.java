package com.test.selfie.gallery;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.test.selfie.R;
import com.test.selfie.domain.model.Picture;
import com.test.selfie.domain.usecase.GalleryUseCase;
import com.test.selfie.shared.schedulers.BaseSchedulers;

import java.io.InputStream;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

public class GalleryPresenter implements GalleryContract.Presenter {

    private GalleryContract.View mView;
    private GalleryUseCase mUseCase;
    private BaseSchedulers mSchedulers;
    private CompositeDisposable mDisposable;

    public GalleryPresenter(GalleryContract.View view,
                            GalleryUseCase useCase,
                            BaseSchedulers schedulers) {
        mView = view;
        mUseCase = useCase;
        mSchedulers = schedulers;
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
                .subscribeOn(mSchedulers.io())
                .observeOn(mSchedulers.mainThread())
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
                    Log.e("TESTE", error);
                    mView.showError(error);
                }));
    }

    @Override
    public void loadPictures() {
        mView.showProgress(R.string.message_gallery_load);
        mDisposable.add(fetch()
                .subscribeOn(mSchedulers.io())
                .observeOn(mSchedulers.mainThread())
                .doFinally(() -> mView.hideProgress())
                .subscribe(pictures -> {
                    if (pictures.isEmpty()) {
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
                    Log.e("TESTE", error);
                    mView.showError(error);
                }));
    }

    @Override
    public void deletePicture(@NonNull String pictureId, final int position) {
        mView.showProgress(R.string.message_gallery_delete);
        mDisposable.add(delete(pictureId)
                .subscribeOn(mSchedulers.io())
                .observeOn(mSchedulers.mainThread())
                .doFinally(() -> mView.hideProgress())
                .subscribe(() -> mView.removeFromGallery(position),
                        e -> {
                            e.printStackTrace();
                            String error = e.getMessage();
                            if (e instanceof VolleyError) {
                                error = new String(((VolleyError) e).networkResponse.data);
                            }
                            Log.e("TESTE", error);
                            mView.showError(error);
                        }));
    }

    private Single<Picture> save(String name, InputStream stream) {
        return mUseCase.uploadPicture(name, stream);
    }

    private Single<List<Picture>> fetch() {
        return mUseCase.fetchPictures();
    }

    private Completable delete(String id) {
        return mUseCase.deletePicture(id);
    }

}
