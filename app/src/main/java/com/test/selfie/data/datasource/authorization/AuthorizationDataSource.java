package com.test.selfie.data.datasource.authorization;

import com.test.selfie.data.entity.AuthEntity;

import io.reactivex.Single;

public interface AuthorizationDataSource {

    /**
     * Used for getting access token authorization in order to access google APIs
     *
     * @param clientId Server client id
     * @param clientSecret Server client secret
     * @param authCode Auth code returned from google sign in
     * @return Single of {@link AuthEntity} containing access token
     * */
    Single<AuthEntity> getAuthorization(String clientId, String clientSecret, String authCode);

}