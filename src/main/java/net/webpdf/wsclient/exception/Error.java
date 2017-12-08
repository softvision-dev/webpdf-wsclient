package net.webpdf.wsclient.exception;

public enum Error {

    /*
     * General error codes
     */
    NONE(0, "No error"),
    UNKNOWN_EXCEPTION(-1, "Unknown exception"),
    UNKNOWN_WEBSERVICE(-1, "Webservice type in factory not available"),
    UNKNOWN_WEBSERVICE_TYPE(-1, "Web service type not available"),
    INVALID_WEBSERVICE_URL(-1, "Invalid URL for web service"),
    INVALID_PARAMETER(-1, "Invalid parameter"),
    INVALID_OPERATION_DATA(-1, "Invalid XML operation data"),
    HTTP_IO_ERROR(-1, "HTTP IO error"),
    HTTP_EMPTY_ENTITY(-1, "HTTP entity is empty"),
    HTTP_CUSTOM_ERROR(-1, "HTTP custom error"),
    INVALID_URL(-1, "Invalid URL"),
    WSDL_INVALID_FILE(-1, "Unable to access WSDL file"),
    WSDL_INVALID_URL(-1, "Invalid WSDL URL"),
    NO_DOCUMENT(-1, "No document defined"),
    INVALID_DOCUMENT(-1, "The found document is invalid"),
    NO_OPERATION_DATA(-1, "No operation data available"),
    SOAP_EXECUTION(-1, "SOAP web service execution error"),
    SESSION_CREATE(-1, "Unknown session type"),
    TO_XML(-1, "Unable to convert to XML"),
    UNKNOWN_HTTP_METHOD(-1, "Unknown HTTP method");

    public static final Error OK = Error.NONE;

    private final int code;
    private final String message;

    Error(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Error getName(int errorCode) {
        for (Error error : Error.values()) {
            if (error.getCode() == errorCode) {
                return error;
            }
        }
        return Error.UNKNOWN_EXCEPTION;
    }

    protected String getMessage() {
        return this.message == null ? "" : this.message;
    }

    public int getCode() {
        return this.code;
    }

}

