package com.mercadolibre.android.sdk.internal;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercadolibre.android.sdk.Identity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Class that encapsulate all information needed to run a request to the API
 * withing the thread pool pattern.
 */
public class HttpRequestParameters {


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MeliHttpVerbs.GET, MeliHttpVerbs.AUTHENTICATED_GET, MeliHttpVerbs.POST, MeliHttpVerbs.PUT, MeliHttpVerbs.DELETE})
    public @interface MeliHttpVerbs {
        /**
         * Perform a GET request.
         */
        int GET = 1;

        /**
         * Perform a Get request with authentication.
         */
        int AUTHENTICATED_GET = 2;

        /**
         * Perform a POST request.
         */
        int POST = 3;

        /**
         * Perform a PUT request.
         */
        int PUT = 4;

        /**
         * Perform a DELETE request.
         */
        int DELETE = 5;
    }

    // indicates the Http verb to execute in the request
    private int requestCode;

    //the path of the resource to access
    private String requestPath;

    //the message to body into the request
    private String requestBody;

    // identity of the current session
    private Identity meliIdentity;

    public HttpRequestParameters(@MeliHttpVerbs int requestCode, @NonNull String requestPath, @Nullable String requestBody, @Nullable Identity meliIdentity) {
        this.requestCode = requestCode;
        this.requestPath = requestPath;
        this.requestBody = requestBody;
        this.meliIdentity = meliIdentity;
    }

    @MeliHttpVerbs
    public int getRequestCode() {
        return requestCode;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public Identity getMeliIdentity() {
        return meliIdentity;
    }


    /**
     * Factory method to create an {@link HttpRequestParameters} object, with the information
     * needed to perform a GET request.
     *
     * @param path         of the resource to access.
     * @param meliIdentity of the current session.
     * @return HttpRequestParameters object with the request parameters
     */
    public static HttpRequestParameters createGetParameters(String path, Identity meliIdentity) {
        return new HttpRequestParameters(MeliHttpVerbs.GET, path, null, meliIdentity);
    }

    /**
     * Factory method to create an {@link HttpRequestParameters} object, with the information
     * needed to perform an authenticated  GET request.
     *
     * @param path         of the resource to access.
     * @param meliIdentity of the current session.
     * @return HttpRequestParameters object with the request parameters
     */
    public static HttpRequestParameters createAuthenticatedGetParameters(@NonNull String path, @Nullable Identity meliIdentity) {
        return new HttpRequestParameters(MeliHttpVerbs.AUTHENTICATED_GET, path, null, meliIdentity);
    }

    /**
     * Factory method to create an {@link HttpRequestParameters} object, with the information
     * needed to perform a PUT request.
     *
     * @param path         of the resource to access.
     * @param meliIdentity of the current session.
     * @param body         to append into the request
     * @return HttpRequestParameters object with the request parameters
     */
    public static HttpRequestParameters createPutParameters(@NonNull String path, @Nullable String body, @Nullable Identity meliIdentity) {
        return new HttpRequestParameters(MeliHttpVerbs.PUT, path, body, meliIdentity);
    }

    /**
     * Factory method to create an {@link HttpRequestParameters} object, with the information
     * needed to perform a POST request.
     *
     * @param path         of the resource to access.
     * @param meliIdentity of the current session.
     * @param body         to append into the request
     * @return HttpRequestParameters object with the request parameters
     */
    public static HttpRequestParameters createPostParameters(@NonNull String path, @Nullable String body, @Nullable Identity meliIdentity) {
        return new HttpRequestParameters(MeliHttpVerbs.POST, path, body, meliIdentity);
    }

    /**
     * Factory method to create an {@link HttpRequestParameters} object, with the information
     * needed to perform a DELETE request.
     *
     * @param path         of the resource to access.
     * @param meliIdentity of the current session.
     * @param body         to append into the request
     * @return HttpRequestParameters object with the request parameters
     */
    public static HttpRequestParameters createDelteParameters(@NonNull String path, @Nullable String body, @Nullable Identity meliIdentity) {
        return new HttpRequestParameters(MeliHttpVerbs.DELETE, path, body, meliIdentity);
    }


}
