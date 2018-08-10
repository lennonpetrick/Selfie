package com.test.selfie.data.datasource.authorization;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.test.selfie.data.entity.AuthEntity;

import io.reactivex.Single;

public class LocalAuthorizationDataSource implements AuthorizationDataSource {

    private static final String SHARED_PREFERENCES_NAME = "authorization_preferences";

    private SharedPreferences mSharedPreferences;

    public LocalAuthorizationDataSource(@NonNull Context context) {
        this.mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
    }

    @Override
    public Single<String> getAuthorization(String clientId,
                                           String clientSecret,
                                           String authCode) {
        return Single.create(e -> e.onSuccess(mSharedPreferences
                .getString(AuthEntity.class.getName(), "")));
    }

    @Override
    public Single<String> storeLocalAuthorization(final String authEntityJson) {
        return Single.create(e -> {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(AuthEntity.class.getName(), authEntityJson);
            editor.apply();
            e.onSuccess(authEntityJson);
        });
    }

}
