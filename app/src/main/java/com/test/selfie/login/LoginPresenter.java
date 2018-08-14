package com.test.selfie.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View mView;
    private GoogleSignInClient mSignInClient;

    public LoginPresenter(@NonNull LoginContract.View view,
                          @NonNull GoogleSignInClient signInClient) {
        mView = view;
        mSignInClient = signInClient;
    }

    @Override
    public void silentSignIn(Context context) {
        mSignInClient.silentSignIn().addOnCompleteListener(task -> {
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                checkSignInAccount(account);
            } catch (ApiException e) {
                e.printStackTrace();
                checkSignInAccount(null);
            }
        });
    }

    @Override
    public void signIn() {
        mView.startSignInActivity(mSignInClient.getSignInIntent());
    }

    @Override
    public void handleSignInResult(Intent data) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            GoogleSignInAccount account = task.getResult(ApiException.class);
            checkSignInAccount(account);
        } catch (ApiException e) {
            mView.showError(e.getMessage());
            checkSignInAccount(null);
        }
    }

    private void checkSignInAccount(GoogleSignInAccount account) {
        if (account == null) {
            mView.showSignInButtonVisible();
        } else {
            mView.startGalleryActivity(account.getServerAuthCode());
        }
    }

}
