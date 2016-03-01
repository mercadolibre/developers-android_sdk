package com.mercadolibre.android.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    private AccessToken mAccessToken;
    private String mClientIdentifier;


    /**
     * Private class constructor to avoid external instantiation.
     *
     * @param accessToken      - the {@link AccessToken} for this identity.
     * @param clientIdentifier -the identifier of the user.
     */
    private Identity(AccessToken accessToken, String clientIdentifier) {
        mAccessToken = accessToken;
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
        AccessToken accessTokenInstance = new AccessToken(accessToken, expiresInValue);
        return new Identity(accessTokenInstance, clientIdentifier);
    }


    public AccessToken getAccessToken() {
        return mAccessToken;
    }


    public String getUserId() {
        return mClientIdentifier;
    }


    @NonNull
    private static SharedPreferences getPreferencesName(Context context) {
        return context.getSharedPreferences(context.getPackageName() + ".identity", Context.MODE_PRIVATE);
    }

    void store(@NonNull Context context) {
        SharedPreferences.Editor editor = getPreferencesName(context).edit();
        getAccessToken().store(context);
        editor.putString(USER_ID_KEY, getUserId());
        editor.apply();
    }

    @Nullable
    static Identity restore(@NonNull Context context) {
        Identity identity = null;
        AccessToken accessToken = AccessToken.restore(context);
        if (!TextUtils.isEmpty(accessToken.getAccessTokenValue())) {
            String userId = getPreferencesName(context).getString(USER_ID_KEY, null);
            if (userId != null) {
                identity = new Identity(accessToken, userId);
            }
        }

        return identity;
    }


}
