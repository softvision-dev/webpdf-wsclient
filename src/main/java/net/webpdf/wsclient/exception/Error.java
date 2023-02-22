package net.webpdf.wsclient.exception;

import net.webpdf.wsclient.session.auth.token.SessionToken;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * {@link Error} enumerates the known webPDF wsclient errors.
 * </p>
 * <p>
 * <b>Important:</b> The enumerated error codes are explicitly handling wsclient failures, and should not be confused
 * with the webPDF server´s error codes.
 * </p>
 */
public enum Error {

    /**
     * An unexpected Exception has occurred, the wsclient does not define a matching fallback behaviour for the
     * situation.
     */
    UNKNOWN_EXCEPTION(-1, "Unknown exception"),
    /**
     * The selected webservice protocol ist unknown, currently only {@link WebServiceProtocol#SOAP} or
     * {@link WebServiceProtocol#REST} are supported.
     */
    UNKNOWN_WEBSERVICE_PROTOCOL(-2, "Webservice protocol in factory not available"),
    /**
     * The selected webservice type is unknown, the wsclient ist not prepared to execute requests to that endpoint.
     * Possibly the selected wsclient version does not match that of your webPDF server?
     */
    UNKNOWN_WEBSERVICE_TYPE(-3, "Web service type not available"),
    /**
     * The selected URl does not point to a valid webPDF web service, please check the given server location and whether
     * the server is running.
     */
    INVALID_WEBSERVICE_URL(-4, "Invalid URL for web service"),
    /**
     * The selected file resource could not be read, please check whether it exists and is accessible.
     */
    INVALID_FILE_SOURCE(-5, "Invalid file source parameter"),
    /**
     * The operation data could not be translated to valid XML or JSON.
     */
    INVALID_OPERATION_DATA(-6, "Invalid XML/JSON operation data"),
    /**
     * The selected document/documentID is not known to the document manager, try uploading the document first.
     */
    INVALID_DOCUMENT(-7, "The found document is invalid"),
    /**
     * No operation data has been provided for the webservice call, try setting the required parameters first.
     */
    NO_OPERATION_DATA(-8, "No operation data available"),
    /**
     * No document has been selected to execute the operation on.
     */
    NO_DOCUMENT(-9, "No document defined"),
    /**
     * History data could not be found for the given document/documentID, try uploading the document first and check
     * whether your webPDF server is running and reachable.
     */
    INVALID_HISTORY_DATA(-10, "Invalid history parameter"),
    /**
     * Creating a SOAP or REST session failed for the given parameters and server configuration. check
     * whether your webPDF server is running and reachable.
     */
    INVALID_WEBSERVICE_SESSION(-11, "Creating the session failed"),

    /**
     * The given URL (or URI) is not well-formed and does not point to a valid resource.
     */
    INVALID_URL(-30, "Invalid URL"),
    /**
     * An error has occurred, while processing your HTTP request.
     */
    HTTP_IO_ERROR(-31, "HTTP IO error"),
    /**
     * An error has occurred, while processing your HTTPS request.
     */
    HTTPS_IO_ERROR(-32, "HTTPS IO error"),
    /**
     * The request failed unexpectedly. The server´s response is empty.
     */
    HTTP_EMPTY_ENTITY(-33, "HTTP entity is empty"),
    /**
     * The response is not as expected, it may contain an error description.
     */
    HTTP_CUSTOM_ERROR(-34, "HTTP custom error"),
    /**
     * The used HTTP method is unknown. The currently supported HTTP methods are {@link HttpMethod#GET},
     * {@link HttpMethod#PUT}, {@link HttpMethod#POST} and {@link HttpMethod#DELETE}.
     */
    UNKNOWN_HTTP_METHOD(-35, "Unknown HTTP method"),
    /**
     * Executing the operation failed, a valid session could not be created.
     */
    SESSION_CREATE(-36, "Unknown session type"),
    /**
     * A JSON structure could not be translated to valid XML.
     */
    TO_XML_JSON(-37, "Unable to convert to XML/JSON"),

    /**
     * It is only allowed to refresh webPDF server {@link SessionToken}s in this way.
     */
    FORBIDDEN_TOKEN_REFRESH(-40, "Only SessionToken instances may be refreshed in this way."),

    /**
     * The server´s WSDL could not be downloaded, please check, if the server is running and accessible.
     */
    WSDL_INVALID_FILE(-50, "Unable to access WSDL file"),
    /**
     * The WSDL URL did not point to a valid resource, please check your server configuration and whether the webPDF
     * server is running and accessible.
     */
    WSDL_INVALID_URL(-51, "Invalid WSDL URL"),
    /**
     * A client side SOAP execution error occurred.
     */
    SOAP_EXECUTION(-52, "SOAP web service execution error"),
    /**
     * A client side REST execution error occurred.
     */
    REST_EXECUTION(-53, "REST web service execution error"),
    /**
     * An {@link Exception} has occurred during the authentication/authorization step.
     * (look at the providing {@link AuthResultException}´s cause for more details.)
     */
    AUTH_ERROR(-54, "Authentication/Authorization failure.");

    private final int code;
    private final @NotNull String message;

    /**
     * Instantiates an {@link Error} representing the given error code and message.
     *
     * @param code    The error code represented by the created {@link Error}.
     * @param message The error message describing the {@link Error}.
     */
    Error(int code, @NotNull String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Returns the {@link Error} representing the given errorCode.
     *
     * @param errorCode The errorCode an {@link Error} shall be found for.
     * @return The {@link Error} representing the given errorCode.
     */
    public static @NotNull Error getName(int errorCode) {
        for (Error error : Error.values()) {
            if (error.getCode() == errorCode) {
                return error;
            }
        }
        return Error.UNKNOWN_EXCEPTION;
    }

    /**
     * Returns an error message describing the {@link Error}.
     *
     * @return ane error message describing the {@link Error}.
     */
    public @NotNull String getMessage() {
        return this.message;
    }

    /**
     * Returns the numeric wsclient error code.
     *
     * @return the numeric wsclient error code.
     */
    public int getCode() {
        return this.code;
    }

}