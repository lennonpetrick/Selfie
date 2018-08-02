package com.test.selfie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class GalleryActivity extends AppCompatActivity implements GalleryContract.View {

    private GalleryContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mPresenter = new GalleryPresenter(this);
    }

    @Override
    protected void onDestroy() {
        mPresenter = null;
        super.onDestroy();
    }

    @Override
    public void startCameraActivity() {

    }

    @Override
    public void startCropActivity() {

    }
}
