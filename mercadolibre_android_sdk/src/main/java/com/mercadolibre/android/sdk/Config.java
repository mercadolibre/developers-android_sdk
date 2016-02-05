package com.mercadolibre.android.sdk;

import android.support.annotation.NonNull;

/**
 * Protected class that holds configurations for the SDK
 */
final class Config {


    // The Login URL
    private static final String LOGIN_URL = "http://auth.mercadolibre.com.ar/authorization?response_type=token&client_id=";


    /**
     * Prepare and retrieve the Login URL for the given application identifier.
     *
     * @param appId - the application identifier to create the URL for.
     * @return - the created URL as a String value.
     */
    static
    @NonNull
    String getLoginUrlForApplicationIdentifier(@NonNull String appId) {
        return LOGIN_URL.concat(appId);
    }

}
