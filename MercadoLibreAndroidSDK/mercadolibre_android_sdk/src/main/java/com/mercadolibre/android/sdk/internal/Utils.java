package com.mercadolibre.android.sdk.internal;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.mercadolibre.android.sdk.MeliLogger;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * All components com.mercadolibre.android.sdk.internal all for internal use only. These components
 * should not be used from outside the library since this behavior is not supported and it will
 * suffer modifications without previous warning.
 */
final class Utils {


    private final static String UTF8 = "UTF-8";


    /**
     * Process the received String in a URL format to extract the parameters
     * in it.
     *
     * @param urlString - the received url as a String.
     * @return - a Map containing the parameters of the queryString or null
     * if an error is detected.
     */
    static
    @Nullable
    Map<String, String> parseUrl(@NonNull String urlString) {
        Map<String, String> params = null;
        if (!TextUtils.isEmpty(urlString)) {
            Uri u = Uri.parse(urlString);
            String queryString = u.getFragment();
            params = parseUrlQueryString(queryString);
        }
        return params;
    }


    /**
     * Process the received String as a querystring from a URL and puts
     * the parameters of that querystring in a Map.
     *
     * @param queryString - the queryString to process.
     * @return - a Map containing the parameters of the queryString or null
     * if an error is detected.
     */
    static
    @Nullable
    Map<String, String> parseUrlQueryString(@NonNull String queryString) {
        Map<String, String> params = null;
        if (!TextUtils.isEmpty(queryString)) {
            try {
                String[] splitted = queryString.split("&");
                if (splitted.length > 0) {
                    params = new HashMap<>();
                    for (String parameter : splitted) {
                        String[] keyValuePair = parameter.split("=");
                        if (keyValuePair.length == 2) {
                            params.put(URLEncoder.encode(clearKey(keyValuePair[0]), UTF8),
                                    URLEncoder.encode(keyValuePair[1], UTF8));
                        } else if (keyValuePair.length == 1) {
                            params.put(URLEncoder.encode(clearKey(keyValuePair[0]), UTF8),
                                    "");
                        }
                    }
                }
            } catch (Exception ex) {
                params = null;
                MeliLogger.logException(ex);
            }
        }
        return params;
    }


    private static String clearKey(String key) {
        return key.replace("#", "");
    }


}
