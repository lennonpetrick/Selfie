package com.test.selfie;

import android.os.Bundle;

public interface GalleryContract {

    interface View {

        void startCameraActivity();
        void startCropActivity();


    }

    interface Presenter {

        void checkCameraPermission();
        void checkExternalStoragePermission();
        void extractImageFromResult(Bundle data);

    }
}
