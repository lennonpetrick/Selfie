package com.test.selfie.login;

import android.content.Context;
import android.content.Intent;

public interface LoginContract {

    interface View {
        void showSignInButtonVisible();
        void startSignInActivity(Intent intent);
        void startGalleryActivity(String oauthCode);
        void showError(String error);
    }

    interface Presenter {
        void silentSignIn(Context context);
        void signIn();
        void handleSignInResult(Intent data);
    }
}
