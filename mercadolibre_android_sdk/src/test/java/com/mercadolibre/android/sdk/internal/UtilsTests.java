package com.mercadolibre.android.sdk.internal;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UtilsTests {


    @Test
    public void parseValidQueryString() {
        String expectedAccessToken = "expected_access_token";
        String expectedExpiresIn = "21600";
        String expectedUserId = "37986047";
        String expectedDomains = "www.mobilesdkpoc.com";
        String validQueryString = "#access_token=" + expectedAccessToken
                + "&expires_in=" + expectedExpiresIn
                + "&user_id=" + expectedUserId
                + "&domains=" + expectedDomains;
        Map<String, String> params = Utils.parseUrlQueryString(validQueryString);
        assertNotNull(params);
        assertEquals(expectedAccessToken, params.get("access_token"));
        assertEquals(expectedExpiresIn, params.get("expires_in"));
        assertEquals(expectedUserId, params.get("user_id"));
        assertEquals(expectedDomains, params.get("domains"));
    }


}
