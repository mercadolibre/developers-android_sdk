package com.mercadolibre.android.sdk.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.mercadolibre.android.sdk.Meli;

/**
 * This is the home screen for the example application. In it, you will find all you
 * need to start integrating the MercadoLibre android SDK into your project.
 */
public class MainScreen extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        // prepare the UI.
        setupUi();

        // Initialize the MercadoLibre SDK
        Meli.initializeSDK(getApplicationContext());

    }


    private void setupUi() {

        Toolbar mainScreenToolbar = (Toolbar) findViewById(R.id.main_screen_toolbar);
        setSupportActionBar(mainScreenToolbar);

        Button btnLogin = (Button) findViewById(R.id.btn_login_example);
        btnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_login_example:
                break;
            default:
                break;
        }

    }
}
