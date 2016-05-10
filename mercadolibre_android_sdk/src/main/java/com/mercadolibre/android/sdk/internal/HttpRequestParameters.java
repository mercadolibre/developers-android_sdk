package com.mercadolibre.android.sdk.internal;

import com.mercadolibre.android.sdk.Identity;

/**
 * Class that encapsulate all information needed to run a request to the API
 * withing the thread pool pattern.
 */
public class HttpRequestParameters {

    /**
     * Perform a GET request.
     */
    public static final int GET = 1;

    /**
     * Perform a Get request with authentication.
     */
    public static final int AUTHENTICATED_GET = 2;

    /**
     * Perform a POST request.
     */
    public static final int POST = 3;

    /**
     * Perform a PUT request.
     */
    public static final int PUT = 4;

    /**
     * Perform a DELETE request.
     */
    public static final int DELETE = 5;

    // indicates the Http verb to execute in the request
    private int requestCode;

    //the path of the resource to access
    private String requestPath;

    //the message to body into the request
    private String requestBody;

    // identity of the current session
    private Identity meliIdentity;

    public HttpRequestParameters(int requestCode, String requestPath, String requestBody, Identity meliIdentity) {
        this.requestCode = requestCode;
        this.requestPath = requestPath;
        this.requestBody = requestBody;
        this.meliIdentity = meliIdentity;
    }

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
        return new HttpRequestParameters(GET, path, null, meliIdentity);
    }

    /**
     * Factory method to create an {@link HttpRequestParameters} object, with the information
     * needed to perform an authenticated  GET request.
     *
     * @param path         of the resource to access.
     * @param meliIdentity of the current session.
     * @return HttpRequestParameters object with the request parameters
     */
    public static HttpRequestParameters createAuthenticatedGetParameters(String path, Identity meliIdentity) {
        return new HttpRequestParameters(AUTHENTICATED_GET, path, null, meliIdentity);
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
    public static HttpRequestParameters createPutParameters(String path, String body, Identity meliIdentity) {
        return new HttpRequestParameters(PUT, path, body, meliIdentity);
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
    public static HttpRequestParameters createPostParameters(String path, String body, Identity meliIdentity) {
        return new HttpRequestParameters(POST, path, body, meliIdentity);
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
    public static HttpRequestParameters createDelteParameters(String path, String body, Identity meliIdentity) {
        return new HttpRequestParameters(DELETE, path, body, meliIdentity);
    }


}
