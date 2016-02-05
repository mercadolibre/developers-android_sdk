package com.mercadolibre.android.sdk;

/**
 * Exception that represents an specific error for the MercadoLibre Android SDK.
 */
public class MeliException extends RuntimeException {


    /**
     * Creates an instance of the Exception without any specific message.
     */
    public MeliException() {
        super();
    }


    /**
     * Creates an instance of the Exception with the given detailed message.
     *
     * @param detailMessage - the detail message of the exception.
     */
    public MeliException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Creates an instance of the Exception with the given detailed message and cause of the
     * exception
     *
     * @param detailMessage - the detail message of the exception.
     * @param throwable     - the cause of this exception.
     */
    public MeliException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }


    /**
     * Creates an instance of the Exception with the given detailed message and cause of the
     * exception
     *
     * @param throwable - the cause of this exception.
     */
    public MeliException(Throwable throwable) {
        super(throwable);
    }
}
