package com.test.selfie.login;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface LoginContract {

    interface View {
        void showSignInButtonVisible();
        void startSignInActivity();
        void startGalleryActivity(String oauthCode);
        void showError(String error);
    }

    interface Presenter {
        void signIn();
        void checkSignInAccount(GoogleSignInAccount account);
    }
}
