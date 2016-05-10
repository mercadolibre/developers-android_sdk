package com.mercadolibre.android.sdk;

import com.mercadolibre.android.sdk.internal.HttpRequestParameters;

/**
 * Common interface to set up a listener that receives the result
 * from the API requests.
 */
public interface ApiRequestListener {

    /**
     * Called when the request has been processed.
     *
     * @param requestCode - identifies the http verb of the request.
     * @param payload - {@link ApiResponse} object as a result of the request.
     */
    void onRequestProcessed(@HttpRequestParameters.MeliHttpVerbs int requestCode, ApiResponse payload);

    /**
     * Called when request to the API is started.
     *
     * @param requestCode - identifies the http verb of the request.
     */
    void onRequestStarted(@HttpRequestParameters.MeliHttpVerbs int requestCode);
}
