package com.test.selfie.data.repository;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.test.selfie.data.datasource.authorization.AuthorizationDataSource;
import com.test.selfie.data.entity.AuthEntity;
import com.test.selfie.data.entity.mapper.JsonMapper;
import com.test.selfie.domain.AuthorizationRepository;

import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

public class AuthorizationRepositoryImpl implements AuthorizationRepository {

    private static AuthorizationRepository mInstance;

    private AuthorizationDataSource mRemoteDataSource,
                                    mLocalDataSource;
    private String mClientId,
                   mClientSecret,
                   mAuthCode;

    private long mInitialExpirationTime;

    @VisibleForTesting
    Map<String, String> mCache;

    public static AuthorizationRepository getInstance(String clientId,
                                                      String clientSecret,
                                                      String authCode,
                                                      AuthorizationDataSource remoteDataSource,
                                                      AuthorizationDataSource localDataSource) {
        if (mInstance == null) {
            mInstance = new AuthorizationRepositoryImpl(clientId,
                    clientSecret, authCode, remoteDataSource, localDataSource);
        }

        return mInstance;
    }

    private AuthorizationRepositoryImpl(String clientId,
                                        String clientSecret,
                                        String authCode,
                                        AuthorizationDataSource remoteDataSource,
                                        AuthorizationDataSource localDataSource) {
        this.mClientId = clientId;
        this.mClientSecret = clientSecret;
        this.mAuthCode = authCode;
        this.mRemoteDataSource = remoteDataSource;
        this.mLocalDataSource = localDataSource;
    }

    public static void destroyInstance() {
        mInstance = null;
    }

    @Override
    public Single<AuthEntity> getAuthorization() {
        return getCachedOrLocalAuthorization()
                .flatMap((Function<String, SingleSource<AuthEntity>>) s -> {
                    AuthEntity entity = JsonMapper.transformAuthEntity(s);
                    if (entity == null || isExpired(entity)) {
                        return getRemoteAuthorization()
                                .map(JsonMapper::transformAuthEntity);
                    }

                    return Single.just(entity);
                });
    }

    private Single<String> getCachedOrLocalAuthorization() {
        if (mCache == null)
            return getLocalAuthorization();

        String json = mCache.get(AuthEntity.class.getName());
        if (json == null)
            return getLocalAuthorization();

        return Single.just(json);
    }

    private Single<String> getLocalAuthorization() {
        return mLocalDataSource
                .getAuthorization(mClientId, mClientSecret, mAuthCode)
                .doOnSuccess(this::insertAuthEntityJsonIntoCache);
    }

    private Single<String> getRemoteAuthorization() {
        return mRemoteDataSource
                .getAuthorization(mClientId, mClientSecret, mAuthCode)
                .flatMap((Function<String, Single<String>>) s -> {
                    insertAuthEntityJsonIntoCache(s);
                    return mLocalDataSource.storeLocalAuthorization(s);
                })
                .doOnSuccess(s -> mInitialExpirationTime = System.currentTimeMillis());
    }

    private void insertAuthEntityJsonIntoCache(String authEntityJson) {
        if (TextUtils.isEmpty(authEntityJson))
            return;

        if (mCache == null) {
            mCache = new LinkedHashMap<>();
        }

        mCache.put(AuthEntity.class.getName(), authEntityJson);
    }

    @VisibleForTesting
    boolean isExpired(AuthEntity entity) {
        return (System.currentTimeMillis() - mInitialExpirationTime)
                > (entity.getTimeExpiration() * 1000);
    }
}
