package com.test.selfie.login;

import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View mView;

    public LoginPresenter(@NonNull LoginContract.View view) {
        mView = view;
    }

    @Override
    public void signIn() {
        mView.startSignInActivity();
    }

    @Override
    public void checkSignInAccount(GoogleSignInAccount account) {
        if (account == null) {
            mView.showSignInButtonVisible();
        } else {
            mView.startGalleryActivity(account.getServerAuthCode());
        }
    }

}
