package com.mercadolibre.android.sdk;

import android.support.annotation.NonNull;

/**
 * Model class that contains information about the Access Token that is
 * provided by the MercadoLibre Oauth API.
 */
public final class AccessToken {


    private String mAccessTokenValue;
    private long mExpiresInValue;


    AccessToken(@NonNull String accessTokenValue, long expiresInValue) {
        mAccessTokenValue = accessTokenValue;
        mExpiresInValue = expiresInValue;
    }


    /**
     * Retrieves the Access Token as a String value.
     *
     * @return - the Access Token as a String value.
     */
    public String getAccessTokenValue() {
        return mAccessTokenValue;
    }


    /**
     * Retrieves the lifetime of the access token in milliseconds.
     *
     * @return - the lifetime of the access token in milliseconds.
     */
    public long getAccessTokenLifetime() {
        return mExpiresInValue;
    }

}
