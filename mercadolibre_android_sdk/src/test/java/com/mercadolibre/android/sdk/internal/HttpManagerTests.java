package com.mercadolibre.android.sdk.internal;

import android.annotation.TargetApi;
import android.os.Build;

import com.mercadolibre.android.sdk.ApiResponse;
import com.mercadolibre.android.sdk.Meli;
import com.mercadolibre.android.sdk.MeliException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Config(manifest = "src/main/AndroidManifest.xml", sdk = Build.VERSION_CODES.KITKAT)
@RunWith(RobolectricTestRunner.class)
public class HttpManagerTests {


    private HttpGet httpOperation;
    private URL mockUrl;
    private HttpsURLConnection mockConnection;


    @Before
    public void before() {
        Meli.setLoggingEnabled(true);
        httpOperation = new HttpGet();
        mockUrl = getMockUrl();
        mockConnection = mock(HttpsURLConnection.class);
        when(mockConnection.getURL()).thenReturn(mockUrl);
    }


    @Test
    public void get_withHttpInsteadHttps() {
        // execute
        ApiResponse response = httpOperation.execute("http://api.mercadolibre.com/users/123456789");

        // assert
        assertTrue(response.getErrorException() instanceof MeliException);

    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Test
    public void get_withResponseSuccess() {
        try {
            // prepare
            String expectedResponse = "{\"id\": 123456789,\"nickname\": \"TETE4107887\",\"registration_date\": \"2014-06-05T11:49:40.000-04:00\",\"country_id\": \"AR\",\"address\": {\"state\": \"AR-C\",\"city\": \"Palermo\"},\"user_type\": \"normal\",\"tags\": [\"normal\",\"test_user\",\"user_info_verified\"],\"logo\": null,\"points\": 2,\"site_id\": \"MLA\",\"permalink\": \"http://perfil.mercadolibre.com.ar/TETE4107887\",\"seller_reputation\": {\"level_id\": null,\"power_seller_status\": null,\"transactions\": {\"period\": \"historic\",\"total\": 5,\"completed\": 5,\"canceled\": 0,\"ratings\": {\"positive\": 1,\"negative\": 0,\"neutral\": 0}}},\"status\": {\"site_status\": \"active\"}}";
            InputStream stream = new ByteArrayInputStream(expectedResponse.getBytes(StandardCharsets.UTF_8));
            when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(mockConnection.getInputStream()).thenReturn(stream);

            // execute
            ApiResponse response = httpOperation.execute(mockUrl);

            // assert
            assertEquals(ApiResponse.ApiResponseCode.RESPONSE_CODE_SUCCESS, response.getResponseCode());
            assertEquals(expectedResponse, response.getContent());

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Test
    public void get_withResponseError() {
        try {
            // prepare
            String expectedResponse = "Some error string";
            InputStream stream = new ByteArrayInputStream(expectedResponse.getBytes(StandardCharsets.UTF_8));
            when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_METHOD);
            when(mockConnection.getErrorStream()).thenReturn(stream);

            // execute
            ApiResponse response = httpOperation.execute(mockUrl);

            // assert
            assertEquals(ApiResponse.ApiResponseCode.RESPONSE_CODE_ERROR, response.getResponseCode());
            assertEquals(expectedResponse, response.getContent());

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }


    private URL getMockUrl() {
        final URLStreamHandler handler = new URLStreamHandler() {

            @Override
            protected URLConnection openConnection(final URL arg0)
                    throws IOException {
                return mockConnection;
            }
        };
        try {
            return new URL("http://foo.bar", "foo.bar", 80, "", handler);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
