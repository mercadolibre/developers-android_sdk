package com.mercadolibre.android.sdk;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * This class works as a bridge for the clients of the SDK and takes care of managing
 * the lifecycle of the library.
 */
public final class Meli {

    /**
     * The key for the application ID in the Android manifest.
     */
    public static final String APPLICATION_ID_PROPERTY = "com.mercadolibre.android.sdk.ApplicationId";

    //TODO add SDK readme URL here.
    static final String MERCADO_LIBRE_ACTIVITY_NOT_FOUND = "MercadoLibreActivity is not declared in your AndroidManifest.xml"
            + " file. See TODO for more information";
    static final String INVALID_NULL_CONTEXT = "The Context that you give to this method can not be null";
    static final String NO_INTERNET_PERMISSION_REASON =
            "Please add " + "<uses-permission android:name=\"android.permission.INTERNET\" /> " +
                    "to your AndroidManifest.xml.";
    static final String APP_IDENTIFIER_AS_INTEGER = "Application identifier must be placed in the Strings resources file"
            + "not as an integer value in the AndroidManifest file";

    //TODO 2 add sdk readme URL here for app identifier.
    static final String APP_IDENTIFIER_NOT_DECLARED = "You need to place the application identifier in the AndroidManifest file"
            + " with the " + APPLICATION_ID_PROPERTY + " key. Check TODO 2";

    //TODO 2 add sdk readme URL here for app identifier.
    static final String APP_IDENTIFIER_NOT_PARSED = "Application identifier must be placed in the Strings resources file."
            + " Please, verify the example code provided in TODO 3";


    // Flag used to indicate when the SDK is initialized.
    private static boolean isSDKInitialized = false;

    // Application identifier declared in the AndroidManifest of the client application
    private static String meliApplicationId = null;


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
        isSDKInitialized = meliApplicationId != null;

    }


    /**
     * @return true if the SDK has been properly initialized, false any other case.
     */
    static boolean isSDKInitialized() {
        return isSDKInitialized;
    }


    /**
     * Retrieves the application identifier provided by the client application (if any).
     *
     * @return - the application's identifier.
     */
    static String getApplicationIdProperty() {
        return meliApplicationId;
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
        if (meliApplicationId == null) {
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


            Object appId = applicationInfo.metaData.get(APPLICATION_ID_PROPERTY);

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

}
