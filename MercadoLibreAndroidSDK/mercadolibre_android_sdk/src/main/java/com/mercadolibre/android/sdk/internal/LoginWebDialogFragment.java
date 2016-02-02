package com.mercadolibre.android.sdk.internal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mercadolibre.android.sdk.MeliException;
import com.mercadolibre.android.sdk.MeliLogger;
import com.mercadolibre.android.sdk.MercadoLibreActivity;
import com.mercadolibre.android.sdk.R;

import java.util.Map;

/**
 * All components com.mercadolibre.android.sdk.internal all for internal use only. These components
 * should not be used from outside the library since this behavior is not supported and it will
 * suffer modifications without previous warning.<br>
 * This fragment should be used only from {@link com.mercadolibre.android.sdk.MercadoLibreActivity}.
 */
public final class LoginWebDialogFragment extends DialogFragment {


    private static final String URL_PARAM_KEY = "url_param_key";
    private static final String REDIRECT_URL_PARAM_KEY = "redirect_url_param_key";

    static final String BAD_LIBRARY_USAGE = "Please, do not attempt to use this fragment from outside the MercadoLibre Android SDK"
            + " You must call any of the methods from the Meli class in order to interact with this components";


    // View instance variable to control UI
    private WebView wbMeliLogin;
    private ProgressBar pgMeliLogin;

    // Login URL to load
    private String mUrl;

    // Redirect URL
    private String mRedirectUrl;

    public LoginWebDialogFragment() {

    }


    public static
    @NonNull
    LoginWebDialogFragment newInstance(@NonNull String url, @NonNull String redirectUrl) {

        LoginWebDialogFragment newInstance = new LoginWebDialogFragment();

        Bundle args = new Bundle();
        args.putString(URL_PARAM_KEY, url);
        args.putString(REDIRECT_URL_PARAM_KEY, redirectUrl);
        newInstance.setArguments(args);

        return newInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // avoid recreation when rotating
            setRetainInstance(true);

            Bundle args = getArguments();
            if (args != null && args.containsKey(URL_PARAM_KEY)) {
                mUrl = args.getString(URL_PARAM_KEY);
                mRedirectUrl = args.getString(REDIRECT_URL_PARAM_KEY);
            }
        }
    }


    @Override
    public
    @NonNull
    Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // remove the title bar
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fView = inflater.inflate(R.layout.login_web_dialog_fragment, container, false);
        wbMeliLogin = (WebView) fView.findViewById(R.id.wb_meli_login);
        pgMeliLogin = (ProgressBar) fView.findViewById(R.id.pg_meli_login);

        // Setup the web view
        prepareWebView();

        if (mUrl != null) {
            wbMeliLogin.loadUrl(mUrl);
        }

        return fView;
    }


    /**
     * Prepares the webview and it's settings.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void prepareWebView() {
        // Disable scroll bars
        wbMeliLogin.setHorizontalScrollBarEnabled(false);
        wbMeliLogin.setVerticalScrollBarEnabled(false);

        wbMeliLogin.setFocusable(true);
        wbMeliLogin.setFocusableInTouchMode(true);
        wbMeliLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!v.hasFocus()) {
                    v.requestFocus();
                }
                return false;
            }
        });


        // Prepare settings
        final WebSettings webSettings = wbMeliLogin.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set the webview client to catch the data
        wbMeliLogin.setWebViewClient(new LoginWebViewClient());

        // Set a web chrome client to update the UI
        wbMeliLogin.setWebChromeClient(new LoginWebChromeClient());
    }


    /**
     * A {@link WebViewClient} used to listen for changes in the WebView
     * being used to load the MercadoLibre login page.
     */
    private class LoginWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            MeliLogger.log("LOADING URL: " + url);
            if (url.startsWith(LoginWebDialogFragment.this.mRedirectUrl)) {
                MeliLogger.log("Redirect URL: " + url);
                Map<String, String> params = Utils.parseUrl(url);
                if (params != null) {
                    processLoginCompleted(params);
                } else {
                    processErrorFound();
                }

                return true;
            }
            return false;
        }


        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            processErrorFound();
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            processErrorFound();
        }
    }


    /**
     * A @{link WebChromeClient} implementation used to listen for process
     * updates when the WebView is loading a URL.
     */
    private class LoginWebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress < 100 && pgMeliLogin.getVisibility() == View.GONE) {
                pgMeliLogin.setVisibility(View.VISIBLE);
            }
            pgMeliLogin.setProgress(newProgress);
            if (newProgress == 100) {
                pgMeliLogin.setVisibility(View.GONE);
            }
        }
    }


    /**
     * Called when the library detects that the login process has been completed.
     *
     * @param loginInfo - a Map that contains information about the login process.
     */
    private void processLoginCompleted(Map<String, String> loginInfo) {
        Activity parent = getActivity();
        if (parent instanceof MercadoLibreActivity) {
            ((MercadoLibreActivity) parent).onLoginCompleted(loginInfo);
        } else {
            throw new MeliException(BAD_LIBRARY_USAGE);
        }
    }


    private void processErrorFound() {
        Activity parent = getActivity();
        if (parent instanceof MercadoLibreActivity) {
            ((MercadoLibreActivity) parent).onLoginErrorDetected();
        } else {
            throw new MeliException(BAD_LIBRARY_USAGE);
        }
    }
}
