package com.test.selfie.data.repository;

import com.test.selfie.data.datasource.authorization.AuthorizationDataSource;
import com.test.selfie.data.entity.AuthEntity;
import com.test.selfie.domain.AuthorizationRepository;

import io.reactivex.Single;

public class AuthorizationRepositoryImpl implements AuthorizationRepository {

    private static AuthorizationRepository mInstance;

    private AuthorizationDataSource mDataSource;
    private String mClientId,
                   mClientSecret,
                   mAuthCode;

    public static AuthorizationRepository getInstance(String clientId,
                                               String clientSecret,
                                               String authCode,
                                               AuthorizationDataSource dataSource) {
        if (mInstance == null) {
            mInstance = new AuthorizationRepositoryImpl(clientId, clientSecret,
                    authCode, dataSource);
        }

        return mInstance;
    }

    private AuthorizationRepositoryImpl(String clientId,
                                       String clientSecret,
                                       String authCode,
                                       AuthorizationDataSource dataSource) {
        this.mClientId = clientId;
        this.mClientSecret = clientSecret;
        this.mAuthCode = authCode;
        this.mDataSource = dataSource;
    }

    @Override
    public Single<AuthEntity> getAuthorization() {
        return mDataSource.getAuthorization(mClientId, mClientSecret, mAuthCode);
    }
}
