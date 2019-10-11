package net.webpdf.wsclient.exception;

import org.jetbrains.annotations.NotNull;

public enum Error {

    /*
     * General error codes
     */
    NONE(0, "No error"),

    UNKNOWN_EXCEPTION(-1, "Unknown exception"),
    UNKNOWN_WEBSERVICE_PROTOCOL(-2, "Webservice type in factory not available"),
    UNKNOWN_WEBSERVICE_TYPE(-3, "Web service type not available"),
    INVALID_WEBSERVICE_URL(-4, "Invalid URL for web service"),
    INVALID_FILE_SOURCE(-5, "Invalid file source parameter"),
    INVALID_OPERATION_DATA(-6, "Invalid XML/JSON operation data"),
    INVALID_DOCUMENT(-7, "The found document is invalid"),
    NO_OPERATION_DATA(-8, "No operation data available"),
    NO_DOCUMENT(-9, "No document defined"),

    INVALID_URL(-30, "Invalid URL"),
    HTTP_IO_ERROR(-31, "HTTP IO error"),
    HTTPS_IO_ERROR(-32, "HTTPS IO error"),
    HTTP_EMPTY_ENTITY(-33, "HTTP entity is empty"),
    HTTP_CUSTOM_ERROR(-34, "HTTP custom error"),
    UNKNOWN_HTTP_METHOD(-35, "Unknown HTTP method"),
    SESSION_CREATE(-36, "Unknown session type"),
    TO_XML_JSON(-37, "Unable to convert to XML/JSON"),

    WSDL_INVALID_FILE(-50, "Unable to access WSDL file"),
    WSDL_INVALID_URL(-51, "Invalid WSDL URL"),
    SOAP_EXECUTION(-52, "SOAP web service execution error");

    private final int code;
    @NotNull
    private final String message;

    /**
     * Instantiates an Enum object representing the given error code and message.
     *
     * @param code    The error code represented by the created enum instance
     * @param message The error message describing the error represented by the enum instance.
     */
    Error(int code, @NotNull String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Returns the Enum object representing the given errorCode.
     *
     * @param errorCode The errorCode an enum representation shall be found for.
     * @return The Enum object representing the given errorCode.
     */
    @NotNull
    public static Error getName(int errorCode) {
        for (Error error : Error.values()) {
            if (error.getCode() == errorCode) {
                return error;
            }
        }
        return Error.UNKNOWN_EXCEPTION;
    }

    /**
     * Returns the error message describing the error represented by the enum instance.
     *
     * @return The error message describing the error represented by the enum instance.
     */
    @NotNull
    protected String getMessage() {
        return this.message;
    }

    /**
     * Returns the error code represented by the created enum instance
     *
     * @return The error code represented by the created enum instance
     */
    public int getCode() {
        return this.code;
    }

}

