package com.test.selfie.data.datasource.authorization;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.test.selfie.data.entity.AuthEntity;

import io.reactivex.Single;

public class LocalAuthorizationDataSource implements AuthorizationDataSource {

    private static final String SHARED_PREFERENCES_NAME = "authorization_preferences";
    private static final String STORED_TIME_KEY = "stored_time_key";

    private SharedPreferences mSharedPreferences;

    public LocalAuthorizationDataSource(@NonNull Context context) {
        this.mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
    }

    @Override
    public Single<Pair> getAuthorization(String clientId,
                                         String clientSecret,
                                         String authCode) {
        return Single.create(e -> {
            final String json = mSharedPreferences.getString(AuthEntity.class.getName(), "");
            final long storedTime = mSharedPreferences.getLong(STORED_TIME_KEY, 0);
            e.onSuccess(new Pair<>(json, storedTime));
        });
    }

    @Override
    public Single<String> storeLocalAuthorization(final String authEntityJson,
                                                  final long requestedTime) {
        return Single.create(e -> {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(AuthEntity.class.getName(), authEntityJson);
            editor.putLong(STORED_TIME_KEY, requestedTime);
            editor.apply();
            e.onSuccess(authEntityJson);
        });
    }
}
