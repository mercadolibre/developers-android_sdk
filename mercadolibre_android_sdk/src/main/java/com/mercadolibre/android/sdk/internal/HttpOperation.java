package com.mercadolibre.android.sdk.internal;

import android.support.annotation.NonNull;

import com.mercadolibre.android.sdk.ApiResponse;
import com.mercadolibre.android.sdk.MeliException;
import com.mercadolibre.android.sdk.MeliLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * All components com.mercadolibre.android.sdk.internal all for internal use only. These components
 * should not be used from outside the library since this behavior is not supported and it will
 * suffer modifications without previous warning.<br>
 * To make use of this class, use one of the {@link com.mercadolibre.android.sdk.Meli} accessor methods. <br>
 * Class that wraps all the network access to perform Http operations.
 */
public abstract class HttpOperation {

    private static final String TAG = HttpOperation.class.getName();

    // 10 seconds as default
    private static final int DEFAULT_READ_TIMEOUT = 10000;

    // 15 seconds as default
    private static final int DEFAULT_CONNECTION_TIMEOUT = 15000;

    private static final String MELI_API_URL = "https://api.mercadolibre.com";


    protected final int mConnectTimeout;
    protected final int mReadTimeout;


    /**
     * Creates an instance of the object with the given parameters.
     *
     * @param connectionTimeout - the timeout connection
     * @param readTimeout       - the read timeout.
     */
    public HttpOperation(int connectionTimeout, int readTimeout) {
        mConnectTimeout = connectionTimeout;
        mReadTimeout = readTimeout;
    }


    /**
     * Creates an instance of the object with the default timeout values (15 seconds for connection and 10 seconds for read)
     */
    public HttpOperation() {
        this(DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }


    /**
     * Performs a get access to the MercadoLibre's API.
     *
     * @param path - the path of the resource to access.
     * @return - the {@link ApiResponse} retrieved from the API.
     */
    @NonNull
    public ApiResponse execute(@NonNull String path) {
        ApiResponse apiResponse;
        try {
            URL url = new URL(MELI_API_URL + path);
            apiResponse = execute(url);
        } catch (MalformedURLException ex) {
            apiResponse = ApiResponse.newErrorResponseWithException(new MeliException("Invalid URL: " + path, ex));
        }

        return apiResponse;
    }


    /**
     * Performs a get access to the MercadoLibre's API.
     *
     * @param url - the URL to access.
     * @return - the {@link ApiResponse} retrieved from the API.
     */
    @NonNull
    protected ApiResponse execute(@NonNull URL url) {
        ApiResponse apiResponse = null;
        InputStream input = null;
        try {

            // Prepare
            HttpsURLConnection connection = getConnectionFromUrl(url);


            // Do something before connecting (i.e.: POST operation)
            performOperationBeforeConnect(connection);

            connection.connect();
            int responseCode = connection.getResponseCode();

            MeliLogger.log(TAG, "RESPONSE CODE ON {" + connection.getURL().toString() + "} => " + responseCode);

            String responseContent = null;

            if (isValidResponseCode(responseCode)) {
                input = connection.getInputStream();
            } else {
                input = connection.getErrorStream();
            }

            if (input != null) {
                responseContent = readInputIntoString(input);
            }

            MeliLogger.log(TAG, "RESPONSE MESSAGE ON {" + connection.getURL().toString() + "} => " + responseContent);

            apiResponse = ApiResponse.newResponse(responseCode, responseContent);

            connection.disconnect();
        } catch (ClassCastException ex) {
            MeliLogger.logException(ex);
            apiResponse = ApiResponse.newErrorResponseWithException(new MeliException("Invalid URL provided: " + url.toString(), ex));
        } catch (MalformedURLException ex) {
            MeliLogger.logException(ex);
            apiResponse = ApiResponse.newErrorResponseWithException(new MeliException("Invalid URL: " + url.toString(), ex));
        } catch (IOException ex) {
            MeliLogger.logException(ex);
            apiResponse = ApiResponse.newErrorResponseWithException(new MeliException("Unable to open connection: " + url.toString(), ex));
        } finally {
            closeInput(url.toString(), input);
        }


        return apiResponse;
    }


    /**
     * Called when the preparation of the {@link HttpsURLConnection} is needed.
     *
     * @param url - the {@link URL} to connect to.
     * @return -  a valid {@link HttpsURLConnection} to perform operations to.
     * @throws ProtocolException
     * @throws IOException
     */
    protected abstract HttpsURLConnection getConnectionFromUrl(URL url) throws IOException;


    /**
     * Helper method to determinate if the response code retrieved is valid or not.
     *
     * @param responseCode - the response code retrieved
     * @return - true if it is valid, false any other case.
     */
    protected abstract boolean isValidResponseCode(int responseCode);


    /**
     * Called just before calling {@link HttpsURLConnection#connect()} to perform any operation needed before doing
     * the actual connection.
     *
     * @param connection - the {@link HttpsURLConnection} to perform operations to.
     * @throws IOException
     */
    protected abstract void performOperationBeforeConnect(HttpsURLConnection connection) throws IOException;


    /**
     * Attempts to close the InputStream given as parameter.
     */
    private void closeInput(String url, InputStream input) {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                throw new MeliException("Something went wrong when closing the input on " + url, e);
            }
        }
    }


    private String readInputIntoString(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }


}
