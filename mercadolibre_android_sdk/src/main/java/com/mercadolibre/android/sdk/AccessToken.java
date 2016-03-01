package com.mercadolibre.android.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Model class that contains information about the Access Token that is
 * provided by the MercadoLibre Oauth API.
 */
public final class AccessToken {


    private static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY";
    private static final String EXPIRES_IN_KEY = "EXPIRES_IN_KEY";

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


    @NonNull
    private static SharedPreferences getPreferencesName(Context context) {
        return context.getSharedPreferences(context.getPackageName() + ".access_token", Context.MODE_PRIVATE);
    }

    void store(@NonNull Context context) {
        SharedPreferences.Editor editor = getPreferencesName(context).edit();
        editor.putString(ACCESS_TOKEN_KEY, getAccessTokenValue());
        editor.putLong(EXPIRES_IN_KEY, getAccessTokenLifetime());
        editor.apply();
    }

    @NonNull
    static AccessToken restore(@NonNull Context context) {
        SharedPreferences preferences = getPreferencesName(context);
        return new AccessToken(preferences.getString(ACCESS_TOKEN_KEY, ""), preferences.getLong(EXPIRES_IN_KEY, 0));
    }

}
