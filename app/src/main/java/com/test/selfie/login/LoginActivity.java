package com.test.selfie.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
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

    @BindView(R.id.btnSignIn_login) SignInButton mBtnSignIn;

    private LoginContract.Presenter mPresenter;
    private CompositeDisposable mListenersDisposable;
    private GoogleSignInClient mSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mListenersDisposable = new CompositeDisposable();
        mSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(new Scope(Scopes.DRIVE_FILE),
                                new Scope(Scopes.DRIVE_APPFOLDER))
                        .requestServerAuthCode(BuildConfig.SERVER_CLIENT_ID)
                        .requestEmail()
                        .build());

        mPresenter = new LoginPresenter(this);
        silentSignIn();
        setListeners();
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
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn
                        .getSignedInAccountFromIntent(data);

                GoogleSignInAccount account = task.getResult(ApiException.class);
                mPresenter.checkSignInAccount(account);
            } catch (ApiException e) {
                showError(e.getMessage());
                mPresenter.checkSignInAccount(null);
            }
        }
    }

    @Override
    public void showSignInButtonVisible() {
        mBtnSignIn.setVisibility(View.VISIBLE);
    }

    @Override
    public void startSignInActivity() {
        startActivityForResult(mSignInClient.getSignInIntent(), SIGN_IN_REQUEST_CODE);
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

    /* This could be in the presenter, but as GoogleSignInClient needs some Android dependencies
    * I can't test it in the presenter. So putting it here, the presenter is totally testable
    * */
    private void silentSignIn() {
        mSignInClient.silentSignIn().addOnCompleteListener(task -> {
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                mPresenter.checkSignInAccount(account);
            } catch (ApiException e) {
                e.printStackTrace();
                mPresenter.checkSignInAccount(null);
            }
        });
    }

}
