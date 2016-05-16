package com.mercadolibre.android.sdk.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.mercadolibre.android.sdk.ApiRequestListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * All components com.mercadolibre.android.sdk.internal all for internal use only. These components
 * should not be used from outside the library since this behavior is not supported and it will
 * suffer modifications without previous warning.<br>
 * This class creates pools of background threads that perform requests to the API.
 */
public class ApiPoolManager {
    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 5;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;


    private static final int CORE_POOL_SIZE = 5;


    private static final int MAXIMUM_POOL_SIZE = 5;

    // A managed pool of API request threads
    private final ThreadPoolExecutor apiThreadPoolExecutor;

    // A queue of Runnable for the api request pool
    private final BlockingQueue<Runnable> requestWorkQueue;

    // An object that manages Messages in a Thread
    private Handler mHandler;

    private static ApiPoolManager sInstance = null;

    static {

        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        sInstance = new ApiPoolManager();
    }

    private ApiPoolManager() {

        requestWorkQueue = new LinkedBlockingDeque<>();

        apiThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, requestWorkQueue);


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                RequestRunnable requestRunnable = (RequestRunnable) msg.obj;
                switch (msg.what) {
                    case RequestRunnable.MeliRequestState.TASK_STARTED:
                        requestRunnable.notifyRequestStarted();
                        break;
                    case RequestRunnable.MeliRequestState.TASK_COMPLETED:
                        requestRunnable.notifyRequestCompleted();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * Returns the ApiPoolManager object
     * @return The global ApiPoolManager object
     */
    public static ApiPoolManager getInstance() {
        return sInstance;
    }


    /**
     * Static method to execute a parallel API request through the {@link ThreadPoolExecutor}
     * @param httpRequestParameters - parameters of the request to be executed.
     * @param listener - to receive the results of the request.
     */
    public static void requestApi(@NonNull HttpRequestParameters httpRequestParameters, @NonNull ApiRequestListener listener) {
        RequestRunnable requestRunnable = new RequestRunnable(httpRequestParameters);
        requestRunnable.setApiListener(listener);
        sInstance.apiThreadPoolExecutor.execute(requestRunnable);
    }


    /**
     * Notifies the state of the current request
     * @param requestRunnable - responsible of perform the actual request.
     * @param state -state of the request.
     */
    public void handleRequestState(@NonNull RequestRunnable requestRunnable, @RequestRunnable.MeliRequestState int state) {
        Message message =  mHandler.obtainMessage(state,requestRunnable);
        message.sendToTarget();
    }

    /**
     * Cancel all request queued on the {@link ThreadPoolExecutor} instance.
     */
    public static void cancelAll() {
        /*
         * Creates an array of Runnables that's the same size as the
         * thread pool work queue
         */
        Runnable[] runnableArray = new RequestRunnable[sInstance.requestWorkQueue.size()];
        // Populates the array with the Runnables in the queue
        sInstance.requestWorkQueue.toArray(runnableArray);
        // Stores the array length in order to iterate over the array
        int len = runnableArray.length;
        /*
         * Iterates over the array of Runnables and interrupts each one's Thread.
         */
        synchronized (sInstance) {
            for (Runnable runnable : runnableArray){
                Thread thread = ((RequestRunnable)runnable).getCurrentThread();
                // if the Thread exists, post an interrupt to it
                if (null != thread) {
                    thread.interrupt();
                }
            }
        }
    }
}
