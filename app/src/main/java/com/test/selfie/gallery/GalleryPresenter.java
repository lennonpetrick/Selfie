package com.test.selfie.gallery;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.selfie.R;
import com.test.selfie.domain.model.Picture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class GalleryPresenter implements GalleryContract.Presenter {

    private GalleryContract.View mView;
    private CompositeDisposable mDisposable;

    private List<Picture> mPictures;
    private SharedPreferences mPreferences;

    public GalleryPresenter(GalleryContract.View view, SharedPreferences preferences) {
        mView = view;
        mDisposable = new CompositeDisposable();

        mPreferences = preferences;
    }

    @Override
    public void destroy() {
        mDisposable.clear();
        mDisposable = null;
    }

    @Override
    public void savePicture(InputStream stream) {
        if (stream == null)
            return;

        if (mPictures.isEmpty()) {
            mView.showGalleryEmpty(false);
        }

        mView.showProgress(R.string.message_gallery_save);
        mDisposable.add(save(stream)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> mView.hideProgress())
                .subscribe(picture -> mView.addInGallery(picture),
                        e -> mView.showError(e.getMessage())));
    }

    @Override
    public void loadPictures() {
        mView.showProgress(R.string.message_gallery_load);
        mDisposable.add(fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> mView.hideProgress())
                .subscribe(pictures -> {
                    mPictures = pictures;
                    if (mPictures.isEmpty()) {
                        mView.showGalleryEmpty(true);
                    } else {
                        mView.showGalleryEmpty(false);
                        mView.refreshGallery(mPictures);
                    }
                }, e -> mView.showError(e.getMessage())));
    }

    private Single<Picture> save(final InputStream stream) {
        return Single.create(e -> {
            Picture picture = new Picture();
            picture.setData(getBytes(stream));
            mPictures.add(picture);

            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(Picture.class.getName(), new Gson().toJson(mPictures));
            editor.apply();

            e.onSuccess(picture);
        });
    }

    private Flowable<List<Picture>> fetch() {
        return Flowable.create(e -> {
            String json = mPreferences.getString(Picture.class.getName(), "");
            Type type = new TypeToken<List<Picture>>(){}.getType();
            List<Picture>  pictures = new Gson().fromJson(json, type);
            if (pictures == null) {
                pictures = new ArrayList<>();
            }
            e.onNext(pictures);
            e.onComplete();
        }, BackpressureStrategy.BUFFER);
    }

    private byte[] getBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = stream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}
