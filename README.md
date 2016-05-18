#MercadoLibre's Android SDK

This is the official Android SDK for MercadoLibre's Platform.

##How can I install it?

###Android Studio

Add this line to yout app's `build.gradle` inside the `dependencies`section:

```
dependencies {
    compile 'com.mercadolibre.android.sdk:mercadolibre_android_sdk:1.0.0'
}
```


### ProGuard
If you're planning on optimizing your app with ProGuard, make sure that you exclude the MercadoLibre bindings. You can do this by adding the following to your app's `proguard.cfg` file:

     -keep class com.mercadolibre.** { *; }
     
     
## How do I start using it?

In order for the SDK to work, you need to add two String resources in your strings.xml file:

  `<string name="meli_app_id">[Application identifier]</string>`
  
  and
  
  `<string name="meli_redirect_uri">[Redirect URI]</string>`
  
Where:
 - Application Identifier: is the App ID parameter provided when you create your application in the MercadoLibre Developer's site.
 - Redirect URI: the redirect URI that you provide in the creation of your application in the MercadoLibre Developer's site. Note that this URL does not necessary belongs to an actual site.
 
 
Then you need to link this resources in the AndroidManifest.xml file of your application as follow:
 
        <meta-data
            android:name="com.mercadolibre.android.sdk.ApplicationId"
            android:value="@string/meli_app_id" />

        <meta-data
            android:name="com.mercadolibre.android.sdk.RedirectUrl"
            android:value="@string/meli_redirect_uri" />
            
            
  *NOTE: if you fail to provide any of these attributes or the proper declaration in the AndroidManifest.xml file, the library will throw an exception on startup.
  
Also, you need to declare the `<uses-permission android:name="android.permission.INTERNET" />` permission in your AndroidManifest.xml file 
and add the MercadoLibreActivity as described below.
  
##Initialize the SDK

The first thing you need to do in order to use any of the features provided by the SDK is initialize it. You can do this by calling 

      `Meli.initializeSDK(context);`
      
      
## Authorizing your application with the user

The SDK provides functionallity to authorize your users to use your application with the MercadoLibre credentials. In order to do that, you need to follow these steps:

1 - (Mandatory) declare the MercadoLibreActivity in your AndroidManifest.xml. You can do it like this (recommended):

          <activity
            android:name="com.mercadolibre.android.sdk.MercadoLibreActivity"
            android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize"
            android:label="@string/app_name"
            android:theme="@android:style/Theme.Translucent.NoTitleBar" />
            
2 - Then you need to call the Meli method for login:

                  Meli.startLogin(Activity activityClient, int requestCode);
    
  from within your Activity.
    
  - The activityClient Activity is mandatory and will receive the callback response once that the user agrees (or reject) the usage of the application.
   This will happen in the Activity's 
      
                  protected void onActivityResult(int requestCode, int resultCode, Intent data);
                  
      method. If the resultCode is `Activity.RESULT_OK`, then the granted credentials will be available via the Identity object provided by the 
            
                  Meli.getCurrentIdentity();
                  
      method.
      


## Making GET calls

You have two basic ways to do a GET call:

### Anonymous

```java
ApiResponse r = Meli.get("/users/123");
```

### Authenticated

```java
ApiResponse r = Meli.getAuth("/users/123/addresses", context);
```


## Making POST calls

```java
ApiResponse r = Meli.post("/items", bodyJsonAsString , context);
```

## Making PUT calls

```java
ApiResponse r = Meli.put("/items", bodyJsonAsString , context);
```

## Making DELETE calls

```java
ApiResponse r = Meli.delete("/items", bodyJsonAsString , context);
```

## Examples

Within the code in the Github repository, there is an example project that contains examples of how to use the SDK.
