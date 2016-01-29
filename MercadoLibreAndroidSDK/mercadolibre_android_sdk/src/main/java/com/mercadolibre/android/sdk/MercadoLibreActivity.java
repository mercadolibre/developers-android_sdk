package com.mercadolibre.android.sdk;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.mercadolibre.android.sdk.internal.LoginWebDialogFragment;

/**
 * Activity that takes care of interacting with the user every time that
 * a direct interaction is needed (i.e.: login using Oauth).<br>
 * This Activity is mandatory and we strongly recommend it's declaration
 * as follow:
 * * <pre>
 * {@code
 * <activity android:name="com.mercadolibre.android.sdk.MercadoLibreActivity"
 *           android:theme="@android:style/Theme.Translucent.NoTitleBar"
 *           android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
 *           android:label="@string/app_name" />
 * }
 * </pre>
 * This will ensure the proper behavior of the user interface.
 */
public final class MercadoLibreActivity extends FragmentActivity {

    private static final String LOGIN_DIALOG_TAG = "login_dialog_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mercado_libre_activity);


        if (savedInstanceState == null) {
            // Show the LoginDialog only when it's the init, let the
            // framework handle the rotation
            if (Meli.isSDKInitialized()) {
                DialogFragment loginFragment = Meli.getLoginDialogNewInstance();
                loginFragment.show(getSupportFragmentManager(), LOGIN_DIALOG_TAG);
            }
        }

    }
}
