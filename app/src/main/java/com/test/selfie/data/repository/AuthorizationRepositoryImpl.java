package com.test.selfie.data.repository;

import com.test.selfie.data.datasource.authorization.AuthorizationDataSource;
import com.test.selfie.data.entity.AuthEntity;
import com.test.selfie.domain.AuthorizationRepository;

import io.reactivex.Single;

public class AuthorizationRepositoryImpl implements AuthorizationRepository {

    private AuthorizationDataSource mDataSource;

    public AuthorizationRepositoryImpl(AuthorizationDataSource dataSource) {
        this.mDataSource = dataSource;
    }

    @Override
    public Single<AuthEntity> getAuthorization(String clientId,
                                               String clientSecret,
                                               String authCode) {
        return mDataSource.getAuthorization(clientId, clientSecret, authCode);
    }
}
