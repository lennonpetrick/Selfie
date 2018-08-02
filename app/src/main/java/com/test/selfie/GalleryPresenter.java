package com.test.selfie;

import android.os.Bundle;

public class GalleryPresenter implements GalleryContract.Presenter {

    private GalleryContract.View mView;

    public GalleryPresenter(GalleryContract.View view) {
        this.mView = view;
    }

    @Override
    public void checkCameraPermission() {

    }

    @Override
    public void checkExternalStoragePermission() {

    }

    @Override
    public void extractImageFromResult(Bundle data) {

    }
}
