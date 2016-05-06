package com.mercadolibre.android.sdk;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

import com.mercadolibre.android.sdk.internal.MeliApiService;

/**
 * Implementation to receive results thrown by the{@link MeliApiService}.
 */

@SuppressLint("ParcelCreator")
public class MeliApiReceiver extends ResultReceiver {

    /**
     * Component listening for the results.
     */
    private MeliApiReceiverListener listener;

    /**
     * Factory method yo get an instance of this object
     *
     * @return new instance of this class
     */
    public static MeliApiReceiver newInstance() {
        return new MeliApiReceiver(new Handler());
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (listener != null) {
            ApiResponse payload = resultData.getParcelable(MeliApiService.API_RESPONSE_KEY);
            listener.onReceiveResult(resultCode, payload);
        }
    }

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public MeliApiReceiver(Handler handler) {
        super(handler);
    }

    /**
     * Sets the listener that will receive the results thrown by the service.
     *
     * @param listener
     */
    public void setListener(MeliApiReceiverListener listener) {
        this.listener = listener;
    }
}

