package com.mercadolibre.android.sdk;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;

/**
 * Class that wraps information about an API response.
 */
public class ApiResponse implements Parcelable {


    // The HTTP response code for this response
    @ApiResponseCode
    private int mResponseCode;


    // The content of the response as a String value
    private String mContentAsString;


    // The content of the Exception
    private Throwable mErrorException;


    /**
     * Creates a new instance of {@link ApiResponse} with the content provided.
     *
     * @param httpCodeResponse - the response code from the Http connection.
     * @param content          - the response content (if any).
     * @return - the newly created instance of {@link ApiResponse}
     */
    @NonNull
    public static ApiResponse newResponse(int httpCodeResponse, @Nullable String content) {
        ApiResponse response;
        switch (httpCodeResponse) {
            case HttpURLConnection.HTTP_OK:
            case HttpURLConnection.HTTP_CREATED:
                response = new ApiResponse(ApiResponseCode.RESPONSE_CODE_SUCCESS, content);
                break;
            // TODO add case for invalid access token
            default:
                response = new ApiResponse(ApiResponseCode.RESPONSE_CODE_ERROR, content);
                break;
        }

        return response;
    }


    /**
     * Creates a new instance with the proper error declaration.
     *
     * @param throwable - the exception that wraps the error.
     * @return - the newly created instance (with error)
     */
    @NonNull
    public static ApiResponse newErrorResponseWithException(@NonNull Throwable throwable) {
        return new ApiResponse(ApiResponseCode.RESPONSE_CODE_ERROR, null, throwable);
    }


    /**
     * Class constructor to build a new ApiResponse with the given parameters.
     *
     * @param responseCode - an integer that matches a {@link com.mercadolibre.android.sdk.ApiResponse.ApiResponseCode} to identify the response.
     * @param content      - the content of the response as a String value.
     */
    private ApiResponse(@ApiResponseCode int responseCode, @Nullable String content) {
        mResponseCode = responseCode;
        mContentAsString = content;
    }


    private ApiResponse(Parcel parcel) {
        @ApiResponseCode int responseCode = parcel.readInt();
        mResponseCode = responseCode;
        mContentAsString = parcel.readString();
        mErrorException = (Throwable) parcel.readSerializable();

    }


    /**
     * Class constructor to build a new ApiResponse with the given parameters.
     *
     * @param responseCode - an integer that matches a {@link com.mercadolibre.android.sdk.ApiResponse.ApiResponseCode} to identify the response.
     * @param content      - the content of the response as a String value.
     * @param throwable    - the exception wrapped.
     */
    private ApiResponse(@ApiResponseCode int responseCode, @Nullable String content, @NonNull Throwable throwable) {
        this(responseCode, content);
        mErrorException = throwable;
    }


    /**
     * Retrieve the response code of the ApiResponse as an integer value.
     *
     * @return - an integer value that can be matched with a {@link com.mercadolibre.android.sdk.ApiResponse.ApiResponseCode}
     */
    @NonNull
    @ApiResponseCode
    public int getResponseCode() {
        return mResponseCode;
    }


    /**
     * Retrieve the response content as a String value (usually a JSON value wrapped in a String)
     *
     * @return - the content of the response as a String value.
     */
    @Nullable
    public String getContent() {
        return mContentAsString;
    }


    /**
     * Retrieve the {@link Throwable} wrapped in this error.
     *
     * @return - the {@link Throwable} wrapped in this error.
     */
    @Nullable
    public Throwable getErrorException() {
        return mErrorException;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mResponseCode);
        dest.writeString(mContentAsString);
        dest.writeSerializable(mErrorException);
    }


    public static final Parcelable.Creator<ApiResponse> CREATOR = new Creator<ApiResponse>() {
        @Override
        public ApiResponse createFromParcel(Parcel source) {
            return new ApiResponse(source);
        }

        @Override
        public ApiResponse[] newArray(int size) {
            return new ApiResponse[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ApiResponseCode.RESPONSE_CODE_SUCCESS, ApiResponseCode.RESPONSE_CODE_ERROR, ApiResponseCode.RESPONSE_CODE_REFRESH_TOKEN})
    public @interface ApiResponseCode {

        /**
         * The operation has been successfully executed
         */
        int RESPONSE_CODE_SUCCESS = 0;


        /**
         * The operation has been executed with some error
         */
        int RESPONSE_CODE_ERROR = 1;


        /**
         * A refresh of the access token is required before executing this operation
         */
        int RESPONSE_CODE_REFRESH_TOKEN = 2;

    }
}
