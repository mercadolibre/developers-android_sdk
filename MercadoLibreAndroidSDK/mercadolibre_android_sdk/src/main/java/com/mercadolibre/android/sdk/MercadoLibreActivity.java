package com.mercadolibre.android.sdk;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

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
public class MercadoLibreActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mercado_libre_activity);
    }
}
