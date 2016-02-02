package com.mercadolibre.android.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import java.util.Map;

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


    /**
     * Starts the {@link MercadoLibreActivity} with the login process set as current
     * workflow.
     *
     * @param client
     * @param requestCode
     */
    static void login(@NonNull Activity client, int requestCode) {
        Intent intent = new Intent(client, MercadoLibreActivity.class);
        client.startActivityForResult(intent, requestCode);
    }


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


    /**
     * Called when the login process is completed.
     *
     * @param loginInfo - the data associated with the login process.
     */
    public void onLoginCompleted(@NonNull Map<String, String> loginInfo) {
        Meli.setIdentity(loginInfo);
        setResult(RESULT_OK);
        finish();
    }


    /**
     * Called when the login process has suffered an error in order to
     * leave the process properly.
     */
    public void onLoginErrorDetected() {
        Meli.setIdentity(null);
        setResult(RESULT_CANCELED);
        finish();
    }
}
