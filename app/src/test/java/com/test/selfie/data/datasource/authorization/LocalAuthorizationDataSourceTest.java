package com.test.selfie.data.datasource.authorization;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.reactivex.observers.TestObserver;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class LocalAuthorizationDataSourceTest {

    private AuthorizationDataSource mDataSource;

    private String mJson = "json_returned";
    private long mRequestedTime = 1234567890;

    @Before
    public void setUp() {
        Context context = Mockito.mock(Context.class);
        SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);

        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        when(sharedPreferences.getString(anyString(), anyString())).thenReturn(mJson);
        when(sharedPreferences.getLong(anyString(), anyLong())).thenReturn(mRequestedTime);

        SharedPreferences.Editor mockedEditor = Mockito.mock(SharedPreferences.Editor.class);
        when(sharedPreferences.edit()).thenReturn(mockedEditor);

        mDataSource = new LocalAuthorizationDataSource(context);
    }

    @Test
    public void getAuthorization() {
        TestObserver<Pair> observer = mDataSource
                .getAuthorization(null, null, null)
                .test();

        observer.assertSubscribed();
        observer.assertValue(new Pair<>(mJson, mRequestedTime));
    }

    @Test
    public void storeLocalAuthorization() {
        TestObserver<String> observer = mDataSource
                .storeLocalAuthorization(mJson, mRequestedTime)
                .test();

        observer.assertSubscribed();
        observer.assertValue(mJson);
    }
}