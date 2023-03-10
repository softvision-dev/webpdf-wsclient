package net.webpdf.wsclient.exception;

import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.webservice.rest.RestWebService;
import net.webpdf.wsclient.webservice.soap.SoapWebService;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

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
    UNKNOWN_EXCEPTION(-1, "Unknown exception."),
    /**
     * The selected webservice protocol ist unknown, currently only {@link WebServiceProtocol#SOAP} or
     * {@link WebServiceProtocol#REST} are supported.
     */
    UNKNOWN_WEBSERVICE_PROTOCOL(-2, "The selected webservice protocol is unknown."),
    /**
     * The selected webservice type is unknown, the wsclient is not prepared to execute requests to that endpoint.
     * Possibly the selected wsclient version does not match that of your webPDF server?
     */
    UNKNOWN_WEBSERVICE_TYPE(-3, "The selected webservice type is not available."),
    /**
     * The selected URL is not valid - more specific: {@code null}
     *
     * @deprecated This error code is no longer in use.
     */
    @Deprecated
    INVALID_WEBSERVICE_URL(-4, "Invalid URL for webservice."),
    /**
     * The selected source document could not be read, please check whether it exists and is accessible.
     */
    INVALID_SOURCE_DOCUMENT(-5, "Invalid file source."),
    /**
     * An HTTP request´s/response´s content could not be translated to valid XML or JSON.
     */
    INVALID_HTTP_MESSAGE_CONTENT(-6, "Failed to deserialize XML/JSON HTTP message content."),
    /**
     * The selected document/documentID is not known to the document manager, try uploading the document first.
     */
    INVALID_DOCUMENT(-7, "The found document is invalid."),
    /**
     * No operation data has been provided for the webservice call, try setting the required parameters first.
     *
     * @deprecated This error code is no longer in use.
     */
    @Deprecated
    NO_OPERATION_DATA(-8, "No operation data available."),
    /**
     * No document has been selected to execute the operation on.
     *
     * @deprecated This error code is no longer in use.
     */
    @Deprecated
    NO_DOCUMENT(-9, "No document defined."),
    /**
     * History data could not be found for the given document/documentID, try uploading the document first and check
     * whether your webPDF server is running and reachable and whether collecting history data is enabled.
     */
    INVALID_HISTORY_DATA(-10, "Invalid history parameter."),
    /**
     * <p>
     * Creating a SOAP or REST Webservice call failed for the given session. Please check whether the selected
     * {@link WebServiceProtocol} matches that of your session.<br>
     * To produce {@link RestWebService} instances you require the protocol type {@link WebServiceProtocol#REST} and
     * must also provide a {@link RestSession}.<br>
     * To produce {@link SoapWebService} instances you require the protocol type {@link WebServiceProtocol#SOAP} and
     * must also provide a {@link SoapSession}.
     * </p>
     */
    INVALID_WEBSERVICE_SESSION(-11, "Creating a webservice instance failed for the selected session."),
    /**
     * <p>
     * This error should never occur, the webPDF server should either provide a valid result document, or should
     * provide a proper exception, that should have been parsed and thrown prior to this, but the server did neither.<br>
     * </p>
     * <p>
     * At the moment of writing this, no scenario is known where this might ever be the case.<br>
     * <b>However:</b> future webservices might not necessarily return a result document.<br>
     * Please check whether the used wsclient version matches your webPDF server.
     * </p>
     */
    INVALID_RESULT_DOCUMENT(-12, "The resulting document is invalid"),
    /**
     * The data handlers of a {@link SoapDocument} failed to close.
     */
    FAILED_TO_CLOSE_DATA_SOURCE(-13, "The data handlers of a {@link SoapDocument} failed to close."),

    /**
     * <p>
     * The given URL (or URI) is not well-formed and does not point to a valid resource.<br>
     * </p>
     * <p>
     * A {@link Session} was unable to create a proper baseURL or subPath from the {@link URL} provided to the
     * {@link SessionContext} - please check if that URL is correct.
     * </p>
     */
    INVALID_URL(-30, "Invalid URL."),
    /**
     * An error has occurred, while processing your HTTP/HTTPS request. Please check whether the webPDF server is
     * running and reachable, also you might want to check your configured {@link SessionContext}.
     */
    HTTP_IO_ERROR(-31, "HTTP/HTTPS IO error."),
    /**
     * Initializing the {@link TLSContext} failed, please check your TLS settings.
     */
    TLS_INITIALIZATION_FAILURE(-32, "TLS initialization failed."),
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
     * The used webservice protocol is unknown. The currently supported protocols are {@link WebServiceProtocol#REST}
     * and {@link WebServiceProtocol#SOAP}.
     */
    UNKNOWN_SESSION_TYPE(-36, "Unknown session type"),

    /**
     * <p>
     * Failed to parse a given JSON or XML source.<br>
     * Should this fail to parse a given raw OperationData stream: Please check, whether the operation data is well
     * formed.
     * </p>
     */
    XML_OR_JSON_CONVERSION_FAILURE(-37, "Unable to convert to XML/JSON"),
    /**
     * The provided authentication/authorization material is invalid and a session may not be established.
     */
    INVALID_AUTH_MATERIAL(-40, "Authentication/authorization material is invalid"),
    /**
     * Authentication of the session failed for the provided material - please check whether the webPDF server is
     * running and reachable and whether the provided authentication material is correct.
     */
    AUTHENTICATION_FAILURE(-41, "The session authentication failed"),
    /**
     * <p>
     * Refreshing the session token failed - please check whether the webPDF server is running and reachable.<br>
     * Alternatively your access and refresh token expired in the meantime und you need to reauthorize.
     * </p>
     */
    SESSION_REFRESH_FAILURE(-42, "Refreshing the session token failed"),

    /**
     * The server´s WSDL could not be downloaded, please check, if the webPDF server is running and accessible.
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