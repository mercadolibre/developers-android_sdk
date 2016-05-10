package com.mercadolibre.android.sdk.internal;

import com.mercadolibre.android.sdk.ApiResponse;

/**
 * Common interface to set up a listener that receives the result
 * from the API requests.
 */
public interface ApiRequestListener {
    void onRquestProcessed(int requestCode, ApiResponse payload);
    void onRequestStarted(int requestCode);
}
