package com.mercadolibre.android.sdk.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mercadolibre.android.sdk.ApiResponse;
import com.mercadolibre.android.sdk.Identity;
import com.mercadolibre.android.sdk.Meli;
import com.mercadolibre.android.sdk.MeliApiReceiver;
import com.mercadolibre.android.sdk.MeliApiReceiverListener;
import com.mercadolibre.android.sdk.MeliApiServiceFactory;

/**
 * This screen provides the necessary steps to start communicating with the API
 * through the IntentService approach
 */
public class ServiceScreen extends AppCompatActivity implements View.OnClickListener, MeliApiReceiverListener {


    MeliApiReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_screen);

        // prepare the UI.
        setupUi();

        // Set SDK to log events
        Meli.setLoggingEnabled(true);

        // Initialize the MercadoLibre SDK
        Meli.initializeSDK(getApplicationContext());

        receiver = MeliApiReceiver.newInstance();
        receiver.setListener(this);

    }


    private void setupUi() {

        Toolbar mainScreenToolbar = (Toolbar) findViewById(R.id.main_screen_toolbar);
        setSupportActionBar(mainScreenToolbar);

        Button btnSimpleGet = (Button) findViewById(R.id.btn_simple_get);
        btnSimpleGet.setOnClickListener(this);

        Button btnAuthGet = (Button) findViewById(R.id.btn_auth_get);
        btnAuthGet.setOnClickListener(this);

        Button btnPost = (Button) findViewById(R.id.btn_simple_post);
        btnPost.setOnClickListener(this);

        Button btnPut = (Button) findViewById(R.id.btn_simple_put);
        btnPut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        findViewById(R.id.pg_loading).setVisibility(View.VISIBLE);
        final String userId = getUserID();
        switch (viewId) {
            case R.id.btn_simple_get:
                startService(MeliApiServiceFactory.createUnAuthenticatedGetIntent(getApplicationContext(), "/users/" + userId, receiver));
                break;
            case R.id.btn_auth_get:
                startService(MeliApiServiceFactory.createAuthenticatedGetIntent(getApplicationContext(), "/users/" + userId + "/addresses", receiver));
                break;
            case R.id.btn_simple_post:
                startService(MeliApiServiceFactory.createPostIntent(getApplicationContext(), "/items", ITEM_JSON, receiver));
                break;
            case R.id.btn_simple_put:
                startService(MeliApiServiceFactory.createPutIntent(getApplicationContext(), "/items/MLA608718494", PUT_JSON, receiver));
                break;
            default:
                break;
        }

    }


    private String getUserID() {
        Identity identity = Meli.getCurrentIdentity();
        if (identity == null) {
            return null;
        } else {
            return identity.getUserId();
        }
    }

    @Override
    public void onReceiveResult(int resultCode, ApiResponse payload) {
        findViewById(R.id.pg_loading).setVisibility(View.GONE);
        if (payload == null) {
            Toast.makeText(ServiceScreen.this, "Authenticate first!!!", Toast.LENGTH_SHORT).show();
        } else {
            ResultDialogFragment fragment = ResultDialogFragment.newInstance(payload);
            fragment.show(getSupportFragmentManager(), "RESULT");
        }
    }

    private static final String ITEM_JSON = "{\n" +
            "  \"title\": \"Item De Testeo, Por Favor No Ofertar\",\n" +
            "  \"category_id\":\"MLA3530\",\n" +
            "  \"price\":10,\n" +
            "  \"currency_id\":\"ARS\",\n" +
            "  \"available_quantity\":1,\n" +
            "  \"buying_mode\":\"buy_it_now\",\n" +
            "  \"listing_type_id\":\"bronze\",\n" +
            "  \"condition\":\"new\",\n" +
            "  \"description\": \"Lorem ipsum dolor sit amet, an est odio timeam quaerendum.\",\n" +
            "  \"video_id\": \"YOUTUBE_ID_HERE\",\n" +
            "  \"warranty\": \"12 months\",\n" +
            "  \"pictures\":[\n" +
            "    {\"source\":\"http://www.apertura.com/export/sites/revistaap/img/Tecnologia/Logo_ML_NUEVO.jpg_33442984.jpg\"},\n" +
            "    {\"source\":\"http://a5.mzstatic.com/us/r30/Purple3/v4/d4/e1/e6/d4e1e620-295a-1efb-3854-8f0da339f91d/mzl.ozuxflxa.175x175-75.jpg\"}\n" +
            "  ]\n" +
            "}";

    private static final String PUT_JSON = "{\n" +
            "  \"status\":\"paused\"\n" +
            "}";

}
