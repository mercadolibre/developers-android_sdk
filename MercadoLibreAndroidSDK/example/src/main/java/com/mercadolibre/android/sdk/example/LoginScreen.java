package com.mercadolibre.android.sdk.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.mercadolibre.android.sdk.Identity;
import com.mercadolibre.android.sdk.Meli;

public class LoginScreen extends AppCompatActivity {


    // Request code used to receive callbacks from the SDK
    private static final int REQUEST_CODE = 999;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        Meli.startLogin(this, REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                processLoginProcessCompleted();
            } else {
                processLoginProcessWithError();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void processLoginProcessCompleted() {
        Identity identity = Meli.getCurrentIdentity();
        if (identity != null) {
            ((TextView) findViewById(R.id.txt_user_id)).setText(getString(R.string.user_id_text, identity.getUserId()));
            ((TextView) findViewById(R.id.txt_access_token)).setText(getString(R.string.access_token_text, identity.getAccessToken()));
            ((TextView) findViewById(R.id.txt_expires_in)).setText(getString(R.string.expires_in_text, String.valueOf(identity.getExpirationTime())));
        }
    }


    private void processLoginProcessWithError() {
        Toast.makeText(LoginScreen.this, "Oooops, something went wrong with the login process", Toast.LENGTH_SHORT).show();
    }
}
