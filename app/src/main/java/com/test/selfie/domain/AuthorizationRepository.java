package com.test.selfie.domain;

import com.test.selfie.data.entity.AuthEntity;

import io.reactivex.Single;

public interface AuthorizationRepository {

    Single<AuthEntity> getAuthorization(String clientId, String clientSecret, String authCode);

}
