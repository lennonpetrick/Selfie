package com.test.selfie.gallery;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.test.selfie.domain.model.Picture;

import java.io.InputStream;
import java.util.List;

public interface GalleryContract {

    interface View {
        void showError(@NonNull String error);
        void showProgress(@StringRes int stringRes);
        void hideProgress();
        void showGalleryEmpty(boolean param);
        void refreshGallery(@NonNull List<Picture> pictures);
        void addInGallery(@NonNull Picture picture);
    }

    interface Presenter {
        void destroy();
        void savePicture(@Nullable String name,
                         @Nullable InputStream stream);
        void loadPictures();
    }
}
