package com.mercadolibre.android.sdk;

/**
 * Common interface to set up a receiver for the results of the {@link com.mercadolibre.android.sdk.internal.MeliApiService}
 * you must implement this on every component that uses the service to acces the API
 */
public interface MeliApiReceiverListener {
    /**
     * Look up key to a {@link MeliApiReceiverListener} object that will receive the results from the service
     */
    String RECEIVER_KEY = "getMeliReceiver";


    /**
     * The service executed an unauthenticated GET request on the API
     */
    int UNAUTHENTICATED_GET_PROCESSED = 0x00000101;

    /**
     * The service executed an authenticated GET request on the API
     */
    int AUTHENTICATED_GET_PROCESSED = 0x00000102;

    /**
     * The service executed a POST request on the API
     */
    int POST_PROCESSED = 0x00000103;

    /**
     * The service executed a PUT request on the API
     */
    int PUT_PROCESSED = 0x00000104;

    /**
     * The service executed a DELETE request on the API
     */
    int DELTE_PROCESSED = 0x00000105;

    void onReceiveResult(int resultCode, ApiResponse payload);
}
