package com.mercadolibre.android.sdk.internal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * All components com.mercadolibre.android.sdk.internal all for internal use only. These components
 * should not be used from outside the library since this behavior is not supported and it will
 * suffer modifications without previous warning.<br>
 * To make use of this class, use one of the {@link com.mercadolibre.android.sdk.Meli} accessor methods. <br>
 * HttpMethod GET implementation
 */
public final class HttpGet extends HttpOperation {


    protected HttpsURLConnection getConnectionFromUrl(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setReadTimeout(mReadTimeout);
        connection.setConnectTimeout(mConnectTimeout);
        connection.setRequestMethod("GET");
        return connection;
    }


    protected boolean isValidResponseCode(int responseCode) {
        return responseCode == HttpURLConnection.HTTP_OK;
    }

    protected void performOperationBeforeConnect(HttpsURLConnection connection) throws IOException {
        // DO nothing
    }


}
