package com.mercadolibre.android.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.mercadolibre.android.sdk.internal.MeliApiService;

/**
 * Factory class for the creation of {@link Intent} that will be used to initiate a communication
 * with the {@link MeliApiService}.
 * You should use the this class functionality instead of providing your own Intents.
 */
public class MeliApiServiceFactory {

    /**
     * Factory method to create an Intent that starts a non authenticated GET request.
     *
     * @param context         - a Context instance.
     * @param path            the path of the resource to access.
     * @param serviceReceiver that receives the results from the service.
     * @return Intent to start the service
     */
    public static Intent createUnAuthenticatedGetIntent(@NonNull Context context, @NonNull String path, @NonNull MeliApiReceiver serviceReceiver) {
        Intent serviceIntent = new Intent(context.getApplicationContext(), MeliApiService.class);
        serviceIntent.setAction(MeliApiService.MELI_API_ACTION);
        serviceIntent.putExtra(MeliApiService.MELI_API_VERB_KEY, MeliApiService.UNAUTHENTICATED_GET);
        serviceIntent.putExtra(MeliApiService.GET_PATH_KEY, path);
        serviceIntent.putExtra(MeliApiReceiverListener.RECEIVER_KEY, serviceReceiver);
        return serviceIntent;
    }

    /**
     * Factory method to create an Intent that starts an authenticated GET request.
     *
     * @param context         - a Context instance.
     * @param path            path the path of the resource to access.
     * @param serviceReceiver that receives the results from the service.
     * @return intent to start the service
     */

    public static Intent createAuthenticatedGetIntent(@NonNull Context context, @NonNull String path, @NonNull MeliApiReceiver serviceReceiver) {
        Intent serviceIntent = new Intent(context.getApplicationContext(), MeliApiService.class);
        serviceIntent.setAction(MeliApiService.MELI_API_ACTION);
        serviceIntent.putExtra(MeliApiService.MELI_API_VERB_KEY, MeliApiService.AUTHENTICATED_GET);
        serviceIntent.putExtra(MeliApiService.GET_PATH_KEY, path);
        serviceIntent.putExtra(MeliApiReceiverListener.RECEIVER_KEY, serviceReceiver);
        return serviceIntent;
    }

    /**
     * Factory method to create an Intent that starts a POST request. All POST operations
     * need the user to be previously authorized, that's why you need to authorize the user by using
     * {@link Meli#startLogin(Activity, int)} before using this method.
     *
     * @param context         - a Context Instance.
     * @param path            - path the path of the resource to access.
     * @param message         - the message to POST to the API.
     * @param serviceReceiver that receives the results from the service.
     * @return intent to start the service
     */

    public static Intent createPostIntent(@NonNull Context context, @NonNull String path, @NonNull String message, MeliApiReceiver serviceReceiver) {
        Intent serviceIntent = new Intent(context.getApplicationContext(), MeliApiService.class);
        serviceIntent.setAction(MeliApiService.MELI_API_ACTION);
        serviceIntent.putExtra(MeliApiService.MELI_API_VERB_KEY, MeliApiService.POST);
        serviceIntent.putExtra(MeliApiService.GET_PATH_KEY, path);
        serviceIntent.putExtra(MeliApiReceiverListener.RECEIVER_KEY, serviceReceiver);
        serviceIntent.putExtra(MeliApiService.GET_MESSAGE_KEY, message);
        return serviceIntent;
    }

    /**
     * Factory method to create an Intent that starts a PUT request. All PUT operations
     * need the user to be previously authorized, that's why you need to authorize the user by using
     * {@link Meli#startLogin(Activity, int)} before using this method.
     *
     * @param context         - a Context Instance.
     * @param path            - path the path of the resource to access.
     * @param message         - the message to POST to the API.
     * @param serviceReceiver that receives the results from the service.
     * @return intent to start the service
     */
    public static Intent createPutIntent(@NonNull Context context, @NonNull String path, String message, @NonNull MeliApiReceiver serviceReceiver) {
        Intent serviceIntent = new Intent(context.getApplicationContext(), MeliApiService.class);
        serviceIntent.setAction(MeliApiService.MELI_API_ACTION);
        serviceIntent.putExtra(MeliApiService.MELI_API_VERB_KEY, MeliApiService.PUT);
        serviceIntent.putExtra(MeliApiService.GET_PATH_KEY, path);
        serviceIntent.putExtra(MeliApiReceiverListener.RECEIVER_KEY, serviceReceiver);
        serviceIntent.putExtra(MeliApiService.GET_MESSAGE_KEY, message);
        return serviceIntent;
    }
}

