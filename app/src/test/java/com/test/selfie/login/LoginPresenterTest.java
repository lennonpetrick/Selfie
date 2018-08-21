package com.test.selfie.login;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginPresenterTest {

    private LoginContract.Presenter mPresenter;

    @Mock
    private LoginContract.View mView;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mPresenter = new LoginPresenter(mView);
    }

    @Test
    public void signIn() {
        mPresenter.signIn();
        verify(mView).startSignInActivity();
    }

    @Test
    public void checkSignInAccount() {
        GoogleSignInAccount account = Mockito.mock(GoogleSignInAccount.class);
        when(account.getServerAuthCode()).thenReturn("oauth_code");

        mPresenter.checkSignInAccount(account);
        verify(mView).startGalleryActivity("oauth_code");
        verify(mView, never()).showSignInButtonVisible();
    }

    @Test
    public void checkSignInAccount_noAccount() {
        mPresenter.checkSignInAccount(null);
        verify(mView).showSignInButtonVisible();
        verify(mView, never()).startGalleryActivity(anyString());
    }

}