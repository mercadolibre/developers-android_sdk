package com.mercadolibre.android.sdk.example;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mercadolibre.android.sdk.ApiResponse;
import com.mercadolibre.android.sdk.Identity;
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

        // Set SDK to log events
        Meli.setLoggingEnabled(true);

        // Initialize the MercadoLibre SDK
        Meli.initializeSDK(getApplicationContext());

    }


    private void setupUi() {

        Toolbar mainScreenToolbar = (Toolbar) findViewById(R.id.main_screen_toolbar);
        setSupportActionBar(mainScreenToolbar);

        Button btnLogin = (Button) findViewById(R.id.btn_login_example);
        btnLogin.setOnClickListener(this);

        Button btnSimpleGet = (Button) findViewById(R.id.btn_simple_get);
        btnSimpleGet.setOnClickListener(this);

        Button btnAuthGet = (Button) findViewById(R.id.btn_auth_get);
        btnAuthGet.setOnClickListener(this);

        Button btnPost = (Button) findViewById(R.id.btn_simple_post);
        btnPost.setOnClickListener(this);

        Button btnPut = (Button) findViewById(R.id.btn_simple_put);
        btnPut.setOnClickListener(this);

        Button btnThread = (Button) findViewById(R.id.btn_simple_thread);
        btnThread.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_login_example:
                Intent intent = new Intent(this, LoginScreen.class);
                startActivity(intent);
                break;
            case R.id.btn_simple_get:
                new GetAsycTask().execute(new Command() {
                    @Override
                    ApiResponse executeCommand() {
                        String userId = getUserID();
                        if (userId != null) {
                            return Meli.get("/users/" + userId);
                        } else {
                            return null;
                        }
                    }
                });
                break;
            case R.id.btn_auth_get:
                new GetAsycTask().execute(new Command() {
                    @Override
                    ApiResponse executeCommand() {
                        String userId = getUserID();
                        if (userId != null) {
                            return Meli.getAuth("/users/" + userId + "/addresses",Meli.fetchMeliIdentity(getApplicationContext()));
                        } else {
                            return null;
                        }

                    }
                });
                break;
            case R.id.btn_simple_post:
                new GetAsycTask().execute(new Command() {
                    @Override
                    ApiResponse executeCommand() {
                        return Meli.post("/items", ITEM_JSON, Meli.fetchMeliIdentity(getApplicationContext()));
                    }
                });
                break;
            case R.id.btn_simple_put:
                new GetAsycTask().execute(new Command() {
                    @Override
                    ApiResponse executeCommand() {
                        return Meli.put("/items/MLA608718494", PUT_JSON, Meli.fetchMeliIdentity(getApplicationContext()));
                    }
                });
                break;
            case R.id.btn_simple_thread:
                startActivity(new Intent(this, ThreadPoolScreen.class));
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

    private class GetAsycTask extends AsyncTask<Command, Void, ApiResponse> {

        @Override
        protected void onPreExecute() {
            findViewById(R.id.pg_loading).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ApiResponse apiResponse) {
            findViewById(R.id.pg_loading).setVisibility(View.GONE);
            if (apiResponse == null) {
                Toast.makeText(MainScreen.this, "Authenticate first!!!", Toast.LENGTH_SHORT).show();
            } else {
                ResultDialogFragment fragment = ResultDialogFragment.newInstance(apiResponse);
                fragment.show(getSupportFragmentManager(), "RESULT");
            }
        }

        @Override
        protected ApiResponse doInBackground(Command... params) {
            return params[0].executeCommand();
        }
    }


    private abstract class Command {
        abstract ApiResponse executeCommand();
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
