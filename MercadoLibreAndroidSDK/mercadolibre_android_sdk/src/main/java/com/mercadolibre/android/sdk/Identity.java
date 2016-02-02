package com.mercadolibre.android.sdk;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Map;

/**
 * Model class that represents the identity of the user. It contains
 * information related to the user and the access tokens granted to the client
 * application.
 */
public final class Identity {

    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String EXPIRES_IN_KEY = "expires_in";
    private static final String USER_ID_KEY = "user_id";

    private String mAccessTokenValue;
    private long mExpiresInValue;
    private String mClientIdentifier;


    /**
     * Private class constructor to avoid external instantiation.
     *
     * @param accessToken      - the access token that verifies the identity of the application.
     * @param expiresIn        - the lifetime of the access token.
     * @param clientIdentifier - the identifier of the client authenticated.
     */
    private Identity(String accessToken, long expiresIn, String clientIdentifier) {
        mAccessTokenValue = accessToken;
        mExpiresInValue = expiresIn;
        mClientIdentifier = clientIdentifier;
    }


    /**
     * Creates a new instance of the class Identifier.
     *
     * @param data - the Map containing the data associated with this instance.
     * @return - the newly created instance.
     */
    static Identity newInstance(@NonNull Map<String, String> data) {
        String accessToken = data.get(ACCESS_TOKEN_KEY);
        String expiresInOriginalValue = data.get(EXPIRES_IN_KEY);
        String clientIdentifier = data.get(USER_ID_KEY);
        long expiresInValue = 0;

        if (!TextUtils.isEmpty(expiresInOriginalValue)) {
            try {
                expiresInValue = Long.parseLong(expiresInOriginalValue);
            } catch (NumberFormatException ex) {
                expiresInValue = 0;
            }
        }
        return new Identity(accessToken, expiresInValue, clientIdentifier);
    }


    public String getAccessToken() {
        return mAccessTokenValue;
    }


    public long getExpirationTime() {
        return mExpiresInValue;
    }


    public String getUserId() {
        return mClientIdentifier;
    }

}
