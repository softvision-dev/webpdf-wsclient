package net.webpdf.wsclient.session.connection.http;

/**
 * {@link HttpMethod} enumerates all supported HTTP request methods.
 *
 * @see #GET
 * @see #POST
 * @see #PUT
 * @see #DELETE
 * @see #HEAD
 */
public enum HttpMethod {

    /**
     * Requests a representation of a specified resource.
     */
    GET,
    /**
     * Submits an entity to a specified resource.
     */
    POST,
    /**
     * Deletes the specified resource.
     */
    DELETE,
    /**
     * Replaces all current representations of a specified resource.
     */
    PUT,
    /**
     * Requests informational headers for the specified resource
     */
    HEAD
}