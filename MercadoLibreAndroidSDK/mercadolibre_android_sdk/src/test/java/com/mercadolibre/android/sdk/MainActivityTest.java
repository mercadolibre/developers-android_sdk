package com.mercadolibre.android.sdk;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import android.os.Build;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class MainActivityTest {


    private MainActivity mainActivity;

    @Before
    public void setup() {
        // Convenience method to run MainActivity through the Activity Lifecycle methods:
        // onCreate(...) => onStart() => onPostCreate(...) => onResume()
        mainActivity = Robolectric.setupActivity(MainActivity.class);
    }


    @Test
    public void validateTextViewContent() {
        TextView tvHelloWorld = (TextView) mainActivity.findViewById(R.id.tvHelloWorld);
        assertNotNull("TextView could not be found", tvHelloWorld);
        assertTrue("TextView contains incorrect text",
                "Hello World!".equals(tvHelloWorld.getText().toString()));
    }

}
