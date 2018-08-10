package com.test.selfie.data.datasource.authorization;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface AuthorizationDataSource {

    /**
     * Used for getting access token authorization in order to access google APIs
     *
     * @param clientId Server client id
     * @param clientSecret Server client secret
     * @param authCode Auth code returned from google sign in
     * @return Single of the json containing access token
     * */
    Single<String> getAuthorization(String clientId, String clientSecret, String authCode);

    /**
     * Stores a json of the authorization entity in local
     *
     * @param authEntityJson A json of {@link com.test.selfie.data.entity.AuthEntity}
     * @return Single of json stored
     * */
    Single<String> storeLocalAuthorization(String authEntityJson);

}