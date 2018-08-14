package com.test.selfie.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthEntity that = (AuthEntity) o;
        return timeExpiration == that.timeExpiration &&
                Objects.equals(accessToken, that.accessToken) &&
                Objects.equals(tokenType, that.tokenType) &&
                Objects.equals(refreshToken, that.refreshToken) &&
                Objects.equals(tokenId, that.tokenId) &&
                Objects.equals(scope, that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, tokenType, refreshToken, tokenId, timeExpiration, scope);
    }
}
