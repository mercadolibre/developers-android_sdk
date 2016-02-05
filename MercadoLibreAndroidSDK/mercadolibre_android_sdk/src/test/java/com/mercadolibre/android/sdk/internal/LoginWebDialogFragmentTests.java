package com.mercadolibre.android.sdk.internal;


import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.mercadolibre.android.sdk.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@Config(manifest = "src/main/AndroidManifest.xml", sdk = Build.VERSION_CODES.LOLLIPOP, shadows = ExtendedShadowWebView.class)
@RunWith(RobolectricTestRunner.class)
public class LoginWebDialogFragmentTests {


    private static final String FRAGMENT_TAG = "fragment";
    private ActivityController controller;
    private FragmentActivity activity;
    private FragmentManager fragmentManager;

    @Before
    public void setup() {
        controller = Robolectric.buildActivity(FragmentActivity.class);
        activity = (FragmentActivity) controller.create().start().resume().visible().get();
        fragmentManager = activity.getSupportFragmentManager();
    }


    @Test
    public void viewCreatedProperlyTest() {
        LoginWebDialogFragment loginFragment = new LoginWebDialogFragment();
        loginFragment.show(fragmentManager, FRAGMENT_TAG);
        View fView = loginFragment.getView();
        if (fView != null) {
            View wbMeliLogin = fView.findViewById(R.id.wb_meli_login);
            View pgMeliLogin = fView.findViewById(R.id.pg_meli_login);
            assertNotNull(wbMeliLogin);
            assertNotNull(pgMeliLogin);
        } else {
            fail("The fragment's view is not created.");
        }
    }

}
