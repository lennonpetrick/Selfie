package com.test.selfie.data.entity;

import com.google.gson.annotations.SerializedName;

public class AuthEntity {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("id_token")
    private String tokenId;

    @SerializedName("expires_in")
    private int timeExpiration;

    private String scope;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenId() {
        return tokenId;
    }

    public int getTimeExpiration() {
        return timeExpiration;
    }

    public String getScope() {
        return scope;
    }
}
