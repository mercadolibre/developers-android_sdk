package com.mercadolibre.android.sdk.internal;

import android.app.Dialog;
import android.content.Context;

/**
 * All components com.mercadolibre.android.sdk.internal all for internal use only. These components
 * should not be used from outside the library since this behavior is not supported and it will
 * suffer modifications without previous warning.
 */
public class LoginWebDialog extends Dialog {


    /**
     * {@inheritDoc}
     */
    public LoginWebDialog(Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    public LoginWebDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    /**
     * {@inheritDoc}
     */
    protected LoginWebDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
