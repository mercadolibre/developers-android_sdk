package com.mercadolibre.android.sdk.internal;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;


import com.mercadolibre.android.sdk.ApiResponse;
import com.mercadolibre.android.sdk.Meli;
import com.mercadolibre.android.sdk.MeliApiReceiverListener;
import com.mercadolibre.android.sdk.MeliLogger;

/**
 * All components com.mercadolibre.android.sdk.internal all for internal use only. These components
 * should not be used from outside the library since this behavior is not supported and it will
 * suffer modifications without previous warning.<br>
 * Implementation of {@link IntentService} to access Mercado Libre's API asynchronously.
 * The result of this service will be channelled through a {@link com.mercadolibre.android.sdk.MeliApiReceiver}
 */
public class MeliApiService extends IntentService {

    public static final String API_RESPONSE_KEY = "apiResponseKey";

    /**
     * Intent action indicating a request to the Rest API
     */
    public final static String MELI_API_ACTION = "apiIntentAction";

    /**
     * The lookup key for a int that represent the Http verb requested to the API
     * {@link Intent#getParcelableExtra(String)}.
     */
    public static final String MELI_API_VERB_KEY = "requestKey";

    /**
     * The lookup key for string representing the path of the resource to access.
     */
    public static final String GET_PATH_KEY = "getResourcePath";

    /**
     * The lookup key for a string representing the body to include in the request
     */
    public static final String GET_MESSAGE_KEY = "getMessage";


    /**
     * There is no request associated for this service
     */
    public final static int NO_ACTION = 0x00000000;

    /**
     * The service must perform an Unauthenticated GET request to the API
     */
    public final static int UNAUTHENTICATED_GET = 0x00000001;

    /**
     * The service must perform an Authenticated GET request to the API
     */
    public final static int AUTHENTICATED_GET = 0x00000002;

    /**
     * The service must perform a POST request to the API
     */
    public final static int POST = 0x00000003;

    /**
     * The service must perform aPUT request to the API
     */
    public final static int PUT = 0x000000004;

    /**
     * The service must perform a DELETE request to the API
     */
    public final static int DELETE = 0x000000005;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MeliApiService() {
        super(MeliApiService.class.getName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (MeliApiService.MELI_API_ACTION.equals(intent.getAction())) {
            final int action = intent.getIntExtra(MELI_API_VERB_KEY, NO_ACTION);
            final Bundle payloadBundle = new Bundle();
            ApiResponse payload;
            final ResultReceiver receiver = intent.getParcelableExtra(MeliApiReceiverListener.RECEIVER_KEY);
            if (receiver != null) {// no need to process the request if there is no receiver
                switch (action) {
                    case UNAUTHENTICATED_GET:
                        payload = Meli.get(intent.getStringExtra(GET_PATH_KEY));
                        payloadBundle.putParcelable(API_RESPONSE_KEY, payload);
                        receiver.send(MeliApiReceiverListener.UNAUTHENTICATED_GET_PROCESSED, payloadBundle);
                        break;
                    case AUTHENTICATED_GET:
                        payload = Meli.getAuth(intent.getStringExtra(GET_PATH_KEY), getApplicationContext());
                        payloadBundle.putParcelable(API_RESPONSE_KEY, payload);
                        receiver.send(MeliApiReceiverListener.AUTHENTICATED_GET_PROCESSED, payloadBundle);
                        break;
                    case POST:
                        payload = Meli.post(intent.getStringExtra(GET_PATH_KEY), intent.getStringExtra(GET_MESSAGE_KEY)
                                , getApplicationContext());
                        payloadBundle.putParcelable(API_RESPONSE_KEY, payload);
                        receiver.send(MeliApiReceiverListener.POST_PROCESSED, payloadBundle);
                        break;
                    case PUT:
                        payload = Meli.put(intent.getStringExtra(GET_PATH_KEY), intent.getStringExtra(GET_MESSAGE_KEY)
                                , getApplicationContext());
                        payloadBundle.putParcelable(API_RESPONSE_KEY, payload);
                        receiver.send(MeliApiReceiverListener.PUT_PROCESSED, payloadBundle);
                        break;
                    default:
                        MeliLogger.warn("No valid action received in the intent");
                        return;
                }
            }
        }
    }
}
