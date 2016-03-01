package com.mercadolibre.android.sdk;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class MeliTests {

    private static final String MOCK_APP_ID = "99181992988";
    private static final String VALID_REDIRECT_URL = "http://someUrl.com";
    private static final String VALID_REDIRECT_URL_HTTPS = "https://someUrl.com";
    private static final String INVALID_REDIRECT_URL = "file://someUrl.com";


    private Context mContextMock;
    private PackageManager mPackageManagerMock;
    private ApplicationInfo mAppInfoMock;
    private Bundle mMetadataMock;

    @Before
    public void setup() {
        mContextMock = mock(Context.class);
        mPackageManagerMock = mock(PackageManager.class);
        mAppInfoMock = mock(ApplicationInfo.class);
        mMetadataMock = mock(Bundle.class);
        when(mContextMock.getPackageName()).thenReturn("com.mercadolibre.android.sdk");
        when(mContextMock.getPackageManager()).thenReturn(mPackageManagerMock);
        Meli.shutDown();
    }


    @Test
    public void validateMercadoLibreActivityPresent() {
        try {
            ComponentName componentName = new ComponentName(mContextMock, MercadoLibreActivity.class);
            when(mPackageManagerMock.getActivityInfo(componentName, PackageManager.GET_ACTIVITIES))
                    .thenAnswer(new Answer<ActivityInfo>() {
                        @Override
                        public ActivityInfo answer(InvocationOnMock invocation) throws Throwable {
                            return new ActivityInfo();
                        }
                    });
            Meli.validateMercadoLibreActivityPresent(mContextMock);
        } catch (Exception e) {
            fail();
            System.out.print(e.getMessage());
        }

    }


    @Test
    public void validateMercadoLibreActivityNotProperException() {
        try {
            ComponentName componentName = new ComponentName(mContextMock, MercadoLibreActivity.class);
            when(mPackageManagerMock.getActivityInfo(componentName, PackageManager.GET_ACTIVITIES))
                    .thenAnswer(new Answer<ActivityInfo>() {
                        @Override
                        public ActivityInfo answer(InvocationOnMock invocation) throws Throwable {
                            return null;
                        }
                    });
            Meli.validateMercadoLibreActivityPresent(mContextMock);
        } catch (IllegalStateException e) {
            assertEquals(Meli.MERCADO_LIBRE_ACTIVITY_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            fail();
            System.out.print(e.getMessage());
        }

    }


    @Test
    public void validateMercadoLibreActivityNullContextProperException() {
        try {
            Meli.validateMercadoLibreActivityPresent(null);
        } catch (NullPointerException e) {
            assertEquals(Meli.INVALID_NULL_CONTEXT, e.getMessage());
        } catch (Exception e) {
            fail();
            System.out.print(e.getMessage());
        }
    }


    @Test
    public void verifyInternetPermissionNotDeclared() {
        try {
            when(mContextMock.checkCallingOrSelfPermission(Manifest.permission.INTERNET))
                    .thenReturn(PackageManager.PERMISSION_DENIED);
        } catch (IllegalStateException e) {
            assertEquals(Meli.NO_INTERNET_PERMISSION_REASON, e.getMessage());
        } catch (Exception e) {
            fail();
            System.out.print(e.getMessage());
        }
    }


    @Test
    public void verifyInternetPermissionDeclared() {
        try {
            when(mContextMock.checkCallingOrSelfPermission(Manifest.permission.INTERNET))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
        } catch (Exception e) {
            fail();
            System.out.print(e.getMessage());
        }
    }


    @Test
    public void loadMetadataFromManifest() {
        try {
            when(mPackageManagerMock.getApplicationInfo(mContextMock.getPackageName(), PackageManager.GET_META_DATA))
                    .thenReturn(mAppInfoMock);
            when(mMetadataMock.get(Meli.APPLICATION_ID_PROPERTY)).thenReturn(MOCK_APP_ID);
            when(mMetadataMock.get(Meli.LOGIN_REDIRECT_URL_PROPERTY)).thenReturn(VALID_REDIRECT_URL);
            mAppInfoMock.metaData = mMetadataMock;

            Meli.loadMetaDataFromManifest(mContextMock);

            assertEquals(MOCK_APP_ID, Meli.getApplicationIdProperty());
            assertEquals(VALID_REDIRECT_URL, Meli.getLoginRedirectUrlProperty());
        } catch (Exception e) {
            fail();
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void loadMetadataFromManifestNoAppId() {
        try {
            when(mPackageManagerMock.getApplicationInfo(mContextMock.getPackageName(), PackageManager.GET_META_DATA))
                    .thenReturn(mAppInfoMock);
            when(mMetadataMock.get(Meli.APPLICATION_ID_PROPERTY)).thenReturn(null);
            when(mMetadataMock.get(Meli.LOGIN_REDIRECT_URL_PROPERTY)).thenReturn(VALID_REDIRECT_URL);
            mAppInfoMock.metaData = mMetadataMock;

            Meli.loadMetaDataFromManifest(mContextMock);
        } catch (MeliException ex) {
            assertEquals(Meli.APP_IDENTIFIER_NOT_DECLARED, ex.getMessage());
        } catch (Exception e) {
            fail();
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void loadMetadataFromManifestEmptyAppId() {
        try {
            when(mPackageManagerMock.getApplicationInfo(mContextMock.getPackageName(), PackageManager.GET_META_DATA))
                    .thenReturn(mAppInfoMock);
            when(mMetadataMock.get(Meli.APPLICATION_ID_PROPERTY)).thenReturn("");
            when(mMetadataMock.get(Meli.LOGIN_REDIRECT_URL_PROPERTY)).thenReturn(VALID_REDIRECT_URL);
            mAppInfoMock.metaData = mMetadataMock;

            Meli.loadMetaDataFromManifest(mContextMock);
        } catch (MeliException ex) {
            assertEquals(Meli.APP_IDENTIFIER_NOT_DECLARED, ex.getMessage());
        } catch (Exception e) {
            fail();
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void loadMetadataFromManifestNumberAsStringAppId() {
        try {
            when(mPackageManagerMock.getApplicationInfo(mContextMock.getPackageName(), PackageManager.GET_META_DATA))
                    .thenReturn(mAppInfoMock);
            when(mMetadataMock.get(Meli.APPLICATION_ID_PROPERTY)).thenReturn("7.895674E15");
            when(mMetadataMock.get(Meli.LOGIN_REDIRECT_URL_PROPERTY)).thenReturn(VALID_REDIRECT_URL);
            mAppInfoMock.metaData = mMetadataMock;

            Meli.loadMetaDataFromManifest(mContextMock);
        } catch (MeliException ex) {
            assertEquals(Meli.APP_IDENTIFIER_AS_INTEGER, ex.getMessage());
        } catch (Exception e) {
            fail();
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void loadMetadataFromManifestAppIdAsInteger() {
        try {
            when(mPackageManagerMock.getApplicationInfo(mContextMock.getPackageName(), PackageManager.GET_META_DATA))
                    .thenReturn(mAppInfoMock);
            when(mMetadataMock.get(Meli.APPLICATION_ID_PROPERTY)).thenReturn(245616);
            when(mMetadataMock.get(Meli.LOGIN_REDIRECT_URL_PROPERTY)).thenReturn(VALID_REDIRECT_URL);
            mAppInfoMock.metaData = mMetadataMock;

            Meli.loadMetaDataFromManifest(mContextMock);
        } catch (MeliException ex) {
            assertEquals(Meli.APP_IDENTIFIER_AS_INTEGER, ex.getMessage());
        } catch (Exception e) {
            fail();
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void loadMetadataFromManifestInvalidUrl() {
        try {
            when(mPackageManagerMock.getApplicationInfo(mContextMock.getPackageName(), PackageManager.GET_META_DATA))
                    .thenReturn(mAppInfoMock);
            when(mMetadataMock.get(Meli.APPLICATION_ID_PROPERTY)).thenReturn(MOCK_APP_ID);
            when(mMetadataMock.get(Meli.LOGIN_REDIRECT_URL_PROPERTY)).thenReturn(INVALID_REDIRECT_URL);
            mAppInfoMock.metaData = mMetadataMock;

            Meli.loadMetaDataFromManifest(mContextMock);
        } catch (MeliException ex) {
            assertEquals(Meli.INVALID_URL_FORMAT, ex.getMessage());
        } catch (Exception e) {
            fail();
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void loadMetadataFromManifestValidUrlAsHttps() {
        try {
            when(mPackageManagerMock.getApplicationInfo(mContextMock.getPackageName(), PackageManager.GET_META_DATA))
                    .thenReturn(mAppInfoMock);
            when(mMetadataMock.get(Meli.APPLICATION_ID_PROPERTY)).thenReturn(MOCK_APP_ID);
            when(mMetadataMock.get(Meli.LOGIN_REDIRECT_URL_PROPERTY)).thenReturn(VALID_REDIRECT_URL_HTTPS);
            mAppInfoMock.metaData = mMetadataMock;

            Meli.loadMetaDataFromManifest(mContextMock);

            assertEquals(MOCK_APP_ID, Meli.getApplicationIdProperty());
            assertEquals(VALID_REDIRECT_URL_HTTPS, Meli.getLoginRedirectUrlProperty());
        } catch (Exception e) {
            fail();
            System.out.println(e.getMessage());
        }
    }


}
