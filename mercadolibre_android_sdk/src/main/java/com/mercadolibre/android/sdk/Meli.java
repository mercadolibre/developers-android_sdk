package com.mercadolibre.android.sdk;


import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.mercadolibre.android.sdk.internal.ApiPoolManager;
import com.mercadolibre.android.sdk.internal.HttpDelete;
import com.mercadolibre.android.sdk.internal.HttpGet;
import com.mercadolibre.android.sdk.internal.HttpPost;
import com.mercadolibre.android.sdk.internal.HttpPut;
import com.mercadolibre.android.sdk.internal.HttpRequestParameters;
import com.mercadolibre.android.sdk.internal.LoginWebDialogFragment;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class works as a bridge for the clients of the SDK and takes care of managing
 * the lifecycle of the library.
 */
public final class Meli {

    // The key for the application ID in the Android manifest.
    public static final String APPLICATION_ID_PROPERTY = "com.mercadolibre.android.sdk.ApplicationId";

    // The key for the redirection URL in the Android manifest.
    public static final String LOGIN_REDIRECT_URL_PROPERTY = "com.mercadolibre.android.sdk.RedirectUrl";

    static final String MERCADO_LIBRE_ACTIVITY_NOT_FOUND = "MercadoLibreActivity is not declared in your AndroidManifest.xml"
            + " file. See https://github.com/mercadolibre/developers-android_sdk/blob/master/README.md#authorizing-your-application-with-the-user for more information";
    static final String INVALID_NULL_CONTEXT = "The Context that you give to this method can not be null";
    static final String NO_INTERNET_PERMISSION_REASON =
            "Please add " + "<uses-permission android:name=\"android.permission.INTERNET\" /> " +
                    "to your AndroidManifest.xml.";
    static final String APP_IDENTIFIER_AS_INTEGER = "Application identifier must be placed in the Strings resources file"
            + "not as an integer value in the AndroidManifest file";

    static final String APP_IDENTIFIER_NOT_DECLARED = "You need to place the application identifier in the AndroidManifest file"
            + " with the " + APPLICATION_ID_PROPERTY + " key. Check https://github.com/mercadolibre/developers-android_sdk/blob/master/README.md#how-do-i-start-using-it";

    static final String APP_IDENTIFIER_NOT_PARSED = "Application identifier must be placed in the Strings resources file."
            + " Please, verify the example code provided in https://github.com/mercadolibre/developers-android_sdk/blob/master/README.md#how-do-i-start-using-it";


    static final String REDIRECT_URL_NOT_DECLARED = "You need to place the redirection URL in the AndroidManifest file"
            + " with the " + LOGIN_REDIRECT_URL_PROPERTY + " key. Check https://github.com/mercadolibre/developers-android_sdk/blob/master/README.md#how-do-i-start-using-it";

    static final String INVALID_URL_FORMAT = "The redirect URI provided with the key " + LOGIN_REDIRECT_URL_PROPERTY +
            " has an invalid format. Please, refer to https://github.com/mercadolibre/developers-android_sdk/blob/master/README.md#how-do-i-start-using-it";

    static final String SDK_NOT_INITIALIZED = "You need to call Meli.initializeSDK() in order to perform this action. Please, refer to https://github.com/mercadolibre/developers-android_sdk/blob/master/README.md#how-do-i-start-using-it";


    // Flag used to indicate when the SDK is initialized.
    private static boolean isSDKInitialized = false;

    // Application identifier declared in the AndroidManifest of the client application
    private static String meliApplicationId = null;

    private static String meliRedirectLoginUrl = null;

    private static Identity meliIdentity = null;


    /**
     * Sets the library in logging mode or not. If it's set to true,
     * the library will log messages to Logcat with the {@link MeliLogger#TAG} tag.
     *
     * @param enabled - true if the SDK is set to log events, false any other case.
     */
    public static void setLoggingEnabled(boolean enabled) {
        MeliLogger.DEBUG = enabled;
    }

    /**
     * Performs the initialization of the library by verifying that all the
     * required data in the AndroidManifest file is present and loading all
     * the needed information for the library to work.
     *
     * @param applicationContext - a {@link Context} that represents the application's context.
     */
    public static void initializeSDK(Context applicationContext) {

        if (isSDKInitialized) {
            return;
        }

        // Validate that the given context is not null before continue
        validateContextNull(applicationContext);

        // Verify that internet permission has been declared
        verifyInternetPermission(applicationContext);

        // Verify that the MercadoLibreActivity has been declared properly
        validateMercadoLibreActivityPresent(applicationContext);

        // Load the data required by the application
        loadMetaDataFromManifest(applicationContext);

        // OK, if the application identifier is present, the init is done
        isSDKInitialized = meliApplicationId != null && meliRedirectLoginUrl != null;

        loadIdentity(applicationContext);
    }


    /**
     * @return true if the SDK has been properly initialized, false any other case.
     */
    static boolean isSDKInitialized() {
        MeliLogger.log("MeliSDK initialized: [application id is null? " + (meliApplicationId == null) + "; redirectUrl is null? " + (meliRedirectLoginUrl == null));
        return isSDKInitialized;
    }


    /**
     * Retrieves the application identifier provided by the client application (if any).
     *
     * @return - the application's identifier.
     */
    static
    @Nullable
    String getApplicationIdProperty() {
        return meliApplicationId;
    }


    /**
     * Retrieves the application's redirect URL provided by the client application (if any).
     *
     * @return - the login redirect URL.
     */
    static
    @Nullable
    String getLoginRedirectUrlProperty() {
        return meliRedirectLoginUrl;
    }

    /**
     * Verifies if the {@link MercadoLibreActivity} has been declared in the AndroidManifest
     * file of the client application.
     *
     * @param context - the {@link Context} of the application.
     */
    static void validateMercadoLibreActivityPresent(Context context) {
        validateContextNull(context);
        PackageManager pm = context.getPackageManager();
        ActivityInfo activityInfo = null;
        if (pm != null) {
            ComponentName componentName = new ComponentName(context, MercadoLibreActivity.class);
            try {
                activityInfo = pm.getActivityInfo(componentName, PackageManager.GET_ACTIVITIES);
            } catch (PackageManager.NameNotFoundException e) {
                activityInfo = null;
            }
        }
        if (activityInfo == null) {
            throw new IllegalStateException(MERCADO_LIBRE_ACTIVITY_NOT_FOUND);
        }
    }


    /**
     * Verifies if the client application has declared the internet permission in it's
     * AndroidManifest.xml file.
     *
     * @param context - the {@link Context} of the application.
     */
    static void verifyInternetPermission(Context context) {
        validateContextNull(context);
        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
            throw new IllegalStateException(NO_INTERNET_PERMISSION_REASON);
        }
    }


    /**
     * Loads data needed by the library from the AndroidManifest file declared in the client application.
     *
     * @param context - the {@link Context} of the application.
     */
    static void loadMetaDataFromManifest(Context context) {
        if (!isSDKInitialized()) {
            validateContextNull(context);

            PackageManager pm = context.getPackageManager();
            ApplicationInfo applicationInfo;

            try {
                applicationInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                return;
            }

            if (applicationInfo == null || applicationInfo.metaData == null) {
                throw new MeliException(APP_IDENTIFIER_NOT_DECLARED);
            }

            // Load the application identifier
            if (meliApplicationId == null) {
                loadApplicationID(applicationInfo.metaData);

            }

            // Load the redirect URL for Oauth
            if (meliRedirectLoginUrl == null) {
                loadRedirectionUrl(applicationInfo.metaData);
            }
        }
    }


    /**
     * Loads the application identifier from the metadata provided by the client application.
     *
     * @param metadata - a {@link Bundle} that represents the metadata provided by the client application.
     */
    private static void loadApplicationID(Bundle metadata) {
        Object appId = metadata.get(APPLICATION_ID_PROPERTY);

        if (appId == null) {
            throw new MeliException(APP_IDENTIFIER_NOT_DECLARED);
        }

        if (appId instanceof String) {
            meliApplicationId = (String) appId;

            if (TextUtils.isEmpty(meliApplicationId)) {
                meliApplicationId = null;
                throw new MeliException(APP_IDENTIFIER_NOT_DECLARED);
            }

            // Verify that the application id only contains numbers
            if (!Pattern.matches("[0-9]+", meliApplicationId)) {
                meliApplicationId = null;
                throw new MeliException(APP_IDENTIFIER_AS_INTEGER);
            }

        } else if (appId instanceof Integer) {
            throw new MeliException(APP_IDENTIFIER_AS_INTEGER);
        } else {
            throw new MeliException(APP_IDENTIFIER_NOT_PARSED);
        }
    }


    /**
     * Loads the application's redirect URL for Oauth, provided by the client application.
     *
     * @param metadata - a {@link Bundle} that represents the metadata provided by the client application.
     */
    private static void loadRedirectionUrl(Bundle metadata) {
        Object redirectUrl = metadata.get(LOGIN_REDIRECT_URL_PROPERTY);

        if (redirectUrl == null) {
            throw new MeliException(REDIRECT_URL_NOT_DECLARED);
        }

        if (redirectUrl instanceof String) {
            meliRedirectLoginUrl = (String) redirectUrl;

            if (TextUtils.isEmpty(meliRedirectLoginUrl)) {
                meliRedirectLoginUrl = null;
                throw new MeliException(REDIRECT_URL_NOT_DECLARED);
            }

            // Verify that the redirect url looks like a real URL
            if (!URLUtil.isHttpsUrl(meliRedirectLoginUrl) && !URLUtil.isHttpUrl(meliRedirectLoginUrl)) {
                meliRedirectLoginUrl = null;
                throw new MeliException(INVALID_URL_FORMAT);
            }
        } else {
            throw new MeliException(INVALID_URL_FORMAT);
        }
    }


    /**
     * Validates if the given {@link Context} is null or not. If it is, a NullPointerException is thrown.
     *
     * @param context - the {@link Context} to validate.
     */
    private static void validateContextNull(Context context) {
        if (context == null) {
            throw new NullPointerException(INVALID_NULL_CONTEXT);
        }
    }


    /**
     * Creates a new instance of a {@link LoginWebDialogFragment} that can be used to login the
     * user using OAuth.
     *
     * @return - thew created instance.
     */
    static LoginWebDialogFragment getLoginDialogNewInstance() {
        String loginUrl = Config.getLoginUrlForApplicationIdentifier(meliApplicationId);
        return LoginWebDialogFragment.newInstance(loginUrl, meliRedirectLoginUrl);
    }


    /**
     * Shuts down the SDK by setting all states to default
     */
    static void shutDown() {
        meliApplicationId = null;
        meliRedirectLoginUrl = null;
        isSDKInitialized = false;
    }


    /**
     * Sets the {@link Identity} for the current session.
     *
     * @param loginInfo - the information related to the session.
     * @param context   - a context instance
     */
    static void setIdentity(@Nullable Map<String, String> loginInfo, @NonNull Context context) {
        if (loginInfo != null) {
            meliIdentity = Identity.newInstance(loginInfo);
            meliIdentity.store(context);
        } else {
            meliIdentity = null;
        }
    }


    /**
     * Loads the instance of {@link Identity} from the storage (if any)
     *
     * @param context - a Context instance
     * @return - true if it has been possible load the identity, false any other case.
     */
    private static boolean loadIdentity(@NonNull Context context) {
        meliIdentity = Identity.restore(context);
        return meliIdentity != null;
    }

    /**
     * @return - the instance of {@link Identity} for the current session. Null
     * if no identity has been created yet (might indicate that the user has not been
     * authenticated yet).
     */
    public static
    @Nullable
    Identity getCurrentIdentity(@NonNull Context context) {
        validateContextNull(context);
        if (meliIdentity == null) {
            loadIdentity(context);
        }
        return meliIdentity;
    }

    /**
     * Starts the Login process by calling the proper SDK behavior. The {@link Activity} provided
     * in this method will be used to start the {@link MercadoLibreActivity} with the proper settings
     * for the login process and the {@link Activity#onActivityResult(int, int, Intent)} method will
     * be called with the result code in {@link Activity#RESULT_OK} if the process is completed properly
     * or the {@link Activity#RESULT_CANCELED} if an error is detected.
     * If the process is completed properly, the SDK will provide the user's data in an {@link Identity}
     * object that can be reached by the {@link Meli#getCurrentIdentity(Context context)} method.
     * Note that if the login process has been executed successfully at least once on the device, then
     * a false return value occurs and the process is skipped. At this point, you can retrieve the Identity
     * of the logged user from the method {@link Meli#getCurrentIdentity(Context context)}.
     *
     * @param activityClient - an {@link Activity} that will be used as callback receiver. When the process
     *                       is finished (whether is success or not) the {@link Activity#onActivityResult(int, int, Intent)}
     *                       callback will be called with the proper result code.
     * @param requestCode    - the request code used in the {@link Activity#startActivityForResult(Intent, int)} method.
     * @return - true if the login process has started. If the login process has ben executed successfully at least once
     * on the device, it skipped and the return value is false.
     */
    public static boolean startLogin(@NonNull Activity activityClient, int requestCode) {
        boolean loginStarted = true;
        MeliLogger.log("Meli#startLogin(" + activityClient.getClass().getName() + ", " + requestCode + ")");
        if (isSDKInitialized()) {
            //first, attempt to load data from storage
            if (loadIdentity(activityClient)) {
                loginStarted = false;
            } else {
                MercadoLibreActivity.login(activityClient, requestCode);
            }
        } else {
            throw new MeliException(SDK_NOT_INITIALIZED);
        }
        return loginStarted;
    }


    /**
     * Performs a get access to a remote resource exposed by the MercadoLibre API. This method should
     * not be executed in the UI thread since it performs a network operation.
     *
     * @param path - the path of the resource to access.
     * @return - the {@link ApiResponse} retrieved from the API.
     */
    @WorkerThread
    @NonNull
    public static ApiResponse get(@NonNull String path) {
        return new HttpGet().execute(path);
    }

    /**
     * Proxy method that performs a get access to a remote resource exposed by the MercadoLibre API,
     * on a worker thread.
     *
     * @param path     - the path of the resource to access.
     * @param listener - listener that will receive the result of the request.
     */
    public static void asyncGet(@NonNull String path, @NonNull ApiRequestListener listener) {
        ApiPoolManager.requestApi(HttpRequestParameters.createGetParameters(path, null), listener);
    }


    /**
     * Performs an authorized get access to a remote resource exposed by the MercadoLibre API. This method needs
     * to be used when the remote resource being access uses the access token to authorize the user. Should
     * not be executed in the UI thread since it performs a network operation.
     *
     * @param path     - the path of the resource to access.
     * @param identity - user information of the current session.
     * @return - the {@link ApiResponse} retrieved from the API.
     */
    @WorkerThread
    @Nullable
    public static ApiResponse getAuth(@NonNull String path, @Nullable Identity identity) {
        if (meliIdentity == null) {
            meliIdentity = identity;
        }

        if (meliIdentity == null) {
            throw new IllegalStateException("You need to perform a login process before using this method.");
        }

        String accessToken = meliIdentity.getAccessToken().getAccessTokenValue();
        path += "?access_token=" + accessToken;

        return new HttpGet().execute(path);
    }

    /**
     * Proxy method that performs an authorized get access to a remote resource exposed by the MercadoLibre API,
     * on a worker thread. This method needs to be used when the remote resource being access uses the access
     * token to authorize the user.
     *
     * @param path     - the path of the resource to access.
     * @param identity - user information of the current session.
     * @param listener - listener that will receive the result of the request.
     */
    public static void asyncGetAuth(@NonNull String path, @Nullable Identity identity, @NonNull ApiRequestListener listener) {
        ApiPoolManager.requestApi(HttpRequestParameters.createAuthenticatedGetParameters(path, identity), listener);
    }


    /**
     * Performs a POST operation to the remote resource exposed by the MercadoLibre API. All POST operations
     * need the user to be previously authorized, that's why you need to authorize the user by using
     * {@link Meli#startLogin(Activity, int)} before using this method.  This method should
     * not be executed in the UI thread since it performs a network operation.
     *
     * @param path     - the path of the resource to access.
     * @param message  - the message to POST to the API.
     * @param identity - user information of the current session.
     * @return - the {@link ApiResponse} retrieved from the API.
     */
    @WorkerThread
    @NonNull
    public static ApiResponse post(@NonNull String path, @NonNull String message, @Nullable Identity identity) {
        if (meliIdentity == null) {
            meliIdentity = identity;
        }

        if (meliIdentity == null) {
            throw new IllegalStateException("You need to perform a login process before using this method.");
        }

        String accessToken = meliIdentity.getAccessToken().getAccessTokenValue();
        path += "?access_token=" + accessToken;
        return new HttpPost(message).execute(path);
    }

    /**
     * Proxy method that performs a POST operation to the remote resource exposed by the MercadoLibre API,
     * on a worker thread. All POST operations need the user to be previously authorized, that's why you
     * need to authorize the user by using {@link Meli#startLogin(Activity, int)} before using this method.
     *
     * @param path     - the path of the resource to access.
     * @param message  - the message to POST to the API.
     * @param identity - user information of the current session.
     * @param listener - listener that will receive the result of the request.
     */
    public static void asyncPost(@NonNull String path, @NonNull String message, @Nullable Identity identity, @NonNull ApiRequestListener listener) {
        ApiPoolManager.requestApi(HttpRequestParameters.createPostParameters(path, message, identity), listener);
    }


    /**
     * Performs a PUT operation to the remote resource exposed by the MercadoLibre API. All PUT operations
     * need the user to be previously authorized, that's why you need to authorize the user by using
     * {@link Meli#startLogin(Activity, int)} before using this method.  This method should
     * not be executed in the UI thread since it performs a network operation.
     *
     * @param path     - the path of the resource to access.
     * @param message  - the message to POST to the API.
     * @param identity - user information of the current session.
     * @return - the {@link ApiResponse} retrieved from the API.
     */
    @WorkerThread
    @NonNull
    public static ApiResponse put(@NonNull String path, @NonNull String message, @Nullable Identity identity) {
        if (meliIdentity == null) {
            meliIdentity = identity;
        }

        if (meliIdentity == null) {
            throw new IllegalStateException("You need to perform a login process before using this method.");
        }

        String accessToken = meliIdentity.getAccessToken().getAccessTokenValue();
        path += "?access_token=" + accessToken;
        return new HttpPut(message).execute(path);
    }

    /**
     * Proxy method that performs a PUT operation to the remote resource exposed by the MercadoLibre API,
     * on a worker thread. All PUT operations need the user to be previously authorized, that's why you
     * need to authorize the user by using {@link Meli#startLogin(Activity, int)} before using this method.
     *
     * @param path     - the path of the resource to access.
     * @param message  - the message to POST to the API.
     * @param identity - user information of the current session.
     * @param listener - listener that will receive the result of the request.
     */
    public static void asyncPut(@NonNull String path, @NonNull String message, @Nullable Identity identity, @NonNull ApiRequestListener listener) {
        ApiPoolManager.requestApi(HttpRequestParameters.createPutParameters(path, message, identity), listener);
    }


    /**
     * Proxy method that performs a DELETE operation to the remote resource exposed by the MercadoLibre API, on a worker thread
     * All DELETE operations need the user to be previously authorized, that's why you need to authorize the user by using
     * {@link Meli#startLogin(Activity, int)} before using this method.
     *
     * @param path     - the path of the resource to access.
     * @param message  - the message to POST to the API.
     * @param identity - user information of the current session.
     * @return - the {@link ApiResponse} retrieved from the API.
     */
    public static ApiResponse delete(@NonNull String path, @NonNull String message, @Nullable Identity identity) {
        if (meliIdentity == null) {
            meliIdentity = identity;
        }

        if (meliIdentity == null) {
            throw new IllegalStateException("You need to perform a login process before using this method.");
        }

        String accessToken = meliIdentity.getAccessToken().getAccessTokenValue();
        path += "?access_token=" + accessToken;
        return new HttpDelete(message).execute(path);
    }

    /**
     * Proxy method that performs a DELETE operation to the remote resource exposed by the MercadoLibre API, on a worker thread.
     * All DELETE operations need the user to be previously authorized, that's why you need to authorize the user by using
     * {@link Meli#startLogin(Activity, int)} before using this method.
     *
     * @param path     - the path of the resource to access.
     * @param message  - the message to POST to the API.
     * @param identity - user information of the current session.
     * @param listener - listener that will receive the result of the request.
     */
    public static void asyncDelete(@NonNull String path, @NonNull String message, @Nullable Identity identity, @NonNull ApiRequestListener listener) {
        ApiPoolManager.requestApi(HttpRequestParameters.createPutParameters(path, message, identity), listener);
    }


}
