package com.mercadolibre.android.sdk.internal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.mercadolibre.android.sdk.R;

/**
 * All components com.mercadolibre.android.sdk.internal all for internal use only. These components
 * should not be used from outside the library since this behavior is not supported and it will
 * suffer modifications without previous warning.
 */
public class LoginWebDialogFragment extends DialogFragment {


    // View instance variable to control UI
    private WebView wbMeliLogin;
    private ProgressBar pgMeliLogin;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fView = inflater.inflate(R.layout.login_web_dialog_fragment, container, false);
        wbMeliLogin = (WebView) fView.findViewById(R.id.wb_meli_login);
        pgMeliLogin = (ProgressBar) fView.findViewById(R.id.pg_meli_login);

        //TODO prepare views and start loading URL with callbacks
        return fView;
    }
}
