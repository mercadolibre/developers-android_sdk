package com.mercadolibre.android.sdk.internal;

import android.support.annotation.NonNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * All components com.mercadolibre.android.sdk.internal all for internal use only. These components
 * should not be used from outside the library since this behavior is not supported and it will
 * suffer modifications without previous warning.<br>
 * To make use of this class, use one of the {@link com.mercadolibre.android.sdk.Meli} accessor methods. <br>
 * HttpMethod POST implementation
 */
public final class HttpPost extends HttpOperation {


    private final String mBody;


    public HttpPost(@NonNull String body) {
        mBody = body;
    }


    @Override
    protected HttpsURLConnection getConnectionFromUrl(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setReadTimeout(mReadTimeout);
        connection.setConnectTimeout(mConnectTimeout);
        connection.setRequestMethod("POST");
        connection.setFixedLengthStreamingMode(mBody.getBytes("UTF-8").length);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        return connection;
    }

    @Override
    protected boolean isValidResponseCode(int responseCode) {
        return responseCode == HttpURLConnection.HTTP_CREATED;
    }

    @Override
    protected void performOperationBeforeConnect(HttpsURLConnection connection) throws IOException {
        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
        writer.write(mBody.getBytes("UTF-8"));
        writer.flush();
        writer.close();
    }
}
