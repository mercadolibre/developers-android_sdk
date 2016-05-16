package com.mercadolibre.android.sdk.internal;

import android.support.annotation.IntDef;

import com.mercadolibre.android.sdk.ApiRequestListener;
import com.mercadolibre.android.sdk.ApiResponse;
import com.mercadolibre.android.sdk.Meli;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.SoftReference;

/**
 * All components com.mercadolibre.android.sdk.internal all for internal use only. These components
 * should not be used from outside the library since this behavior is not supported and it will
 * suffer modifications without previous warning. <br>
 * Runnable implementation that performs the actual request to the API.
 */
public class RequestRunnable implements Runnable {

    @IntDef({MeliRequestState.TASK_STARTED, MeliRequestState.TASK_COMPLETED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MeliRequestState {


        /**
         * The request has been started.
         */
        int TASK_STARTED = 0x00000040;
        /**
         * The request has been completed
         */
         int TASK_COMPLETED = 0x00000050;
    }

    // listener that receives the result of the request
    private SoftReference<ApiRequestListener> apiListener;

    // parameter to perform the request
    private final HttpRequestParameters requestParameters;

    // results of the request
    private ApiResponse payload;

    // thread con which this runnable is running
    private Thread workingThread;

    /**
     * An object that contains the ThreadPool singleton.
     */
    private static ApiPoolManager poolManager;

    RequestRunnable(HttpRequestParameters requestParameters) {
        this.requestParameters = requestParameters;
        poolManager = ApiPoolManager.getInstance();

    }

    /**
     * Sets the {@link ApiRequestListener} that will be notified with the result of the request.
     *
     * @param apiListener - listener to attach
     */
    public void setApiListener(ApiRequestListener apiListener) {
        this.apiListener = new SoftReference<>(apiListener);
    }

    @Override
    public void run() {
        if (Thread.interrupted()) {
            return;
        }
        setCurrentThread(Thread.currentThread());
        poolManager.handleRequestState(this, MeliRequestState.TASK_STARTED);
        switch (requestParameters.getRequestCode()) {
            case (HttpRequestParameters.MeliHttpVerbs.GET):
                payload = Meli.get(requestParameters.getRequestPath());
                break;
            case (HttpRequestParameters.MeliHttpVerbs.AUTHENTICATED_GET):
                payload = Meli.getAuth(requestParameters.getRequestPath(), requestParameters.getMeliIdentity());
                break;
            case (HttpRequestParameters.MeliHttpVerbs.POST):
                payload = Meli.post(requestParameters.getRequestPath(), requestParameters.getRequestBody(), requestParameters.getMeliIdentity());
                break;
            case (HttpRequestParameters.MeliHttpVerbs.PUT):
                payload = Meli.put(requestParameters.getRequestPath(), requestParameters.getRequestBody(), requestParameters.getMeliIdentity());
                break;
            case (HttpRequestParameters.MeliHttpVerbs.DELETE):
                payload = Meli.delete(requestParameters.getRequestPath(), requestParameters.getRequestBody(), requestParameters.getMeliIdentity());
                break;
            default:
                return;
        }
        if (Thread.interrupted()) {
            return;
        }
        poolManager.handleRequestState(this, MeliRequestState.TASK_COMPLETED);
    }

    /**
     * Sets the identifier for the current Thread. This must be a synchronized operation; see the
     * notes for getCurrentThread()
     */
    public void setCurrentThread(Thread thread) {
        synchronized (poolManager) {
            this.workingThread = thread;
        }
    }


    /**
     * Returns the Thread that this Task is running on. The method must first get a lock on a
     * static field, in this case the ThreadPool singleton. The lock is needed because the
     * Thread object reference is stored in the Thread object itself, and that object can be
     * changed by processes outside of this app.
     */
    public Thread getCurrentThread() {
        synchronized (poolManager) {
            return this.workingThread;
        }
    }

    /**
     * Notifies the {@link ApiRequestListener} supplied that the request has been completed.
     */
    public void notifyRequestCompleted() {
        if (apiListener.get() != null) {
            apiListener.get().onRequestProcessed(requestParameters.getRequestCode(), payload);
        }
    }

    /**
     * Notifies the {@link ApiRequestListener} supplied that the request has started.
     */
    public void notifyRequestStarted() {
        if (apiListener != null) {
            apiListener.get().onRequestStarted(requestParameters.getRequestCode());
        }
    }

}
