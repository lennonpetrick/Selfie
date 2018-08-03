package com.test.selfie;

public class GalleryPresenter implements GalleryContract.Presenter {

    private GalleryContract.View mView;

    public GalleryPresenter(GalleryContract.View view) {
        this.mView = view;
    }

}
