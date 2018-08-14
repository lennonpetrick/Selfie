package com.test.selfie.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Scope;
import com.jakewharton.rxbinding2.view.RxView;
import com.test.selfie.BuildConfig;
import com.test.selfie.R;
import com.test.selfie.gallery.GalleryActivity;
import com.test.selfie.utils.MessageUtils;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    private static final int SIGN_IN_REQUEST_CODE = 1;

    private static final String STORAGE_SCOPE = "https://www.googleapis.com/auth/devstorage.read_write";

    @BindView(R.id.btnSignIn_login) SignInButton mBtnSignIn;

    private LoginContract.Presenter mPresenter;
    private CompositeDisposable mListenersDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mListenersDisposable = new CompositeDisposable();
        mPresenter = new LoginPresenter(this, GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(new Scope(STORAGE_SCOPE))
                        .requestServerAuthCode(BuildConfig.SERVER_CLIENT_ID)
                        .requestEmail()
                        .build()));

        setListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.silentSignIn(this);
    }

    @Override
    protected void onDestroy() {
        mListenersDisposable.clear();
        mListenersDisposable = null;
        mPresenter = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            mPresenter.handleSignInResult(data);
        }
    }

    @Override
    public void showSignInButtonVisible() {
        mBtnSignIn.setVisibility(View.VISIBLE);
    }

    @Override
    public void startSignInActivity(Intent intent) {
        startActivityForResult(intent, SIGN_IN_REQUEST_CODE);
    }

    @Override
    public void startGalleryActivity(String oauthCode) {
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra(GalleryActivity.OAUTH_CODE_EXTRA, oauthCode);

        startActivity(intent);
        finish();
    }

    @Override
    public void showError(String error) {
        MessageUtils.showError(this, error);
    }

    private void setListeners() {
        mListenersDisposable.add(RxView.clicks(mBtnSignIn)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mPresenter.signIn()));
    }
}
