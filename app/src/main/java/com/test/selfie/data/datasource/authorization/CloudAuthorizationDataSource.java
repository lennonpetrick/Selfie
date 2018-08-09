package com.test.selfie.data.datasource.authorization;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.test.selfie.EndpointUtils;
import com.test.selfie.application.AppController;
import com.test.selfie.data.entity.AuthEntity;
import com.test.selfie.data.entity.mapper.JsonMapper;

import java.util.HashMap;

import io.reactivex.Single;

public class CloudAuthorizationDataSource implements AuthorizationDataSource {

    @Override
    public Single<AuthEntity> getAuthorization(final String clientId,
                                               final String clientSecret,
                                               final String authCode) {
        return Single.create(e -> {
            final String route = "oauth2/v4/token";

            HashMap<String, Object> params = new HashMap<>();
            params.put("client_id", clientId);
            params.put("client_secret", clientSecret);
            params.put("code", authCode);
            params.put("redirect_uri", "");
            params.put("grant_type", "authorization_code");

            final String url = EndpointUtils.getUrl(route, params);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    url, null, response -> e.onSuccess(JsonMapper
                            .transformAuthEntity(response.toString())), e::tryOnError);

            AppController.getInstance().addToRequestQueue(request);
        });
    }

}
