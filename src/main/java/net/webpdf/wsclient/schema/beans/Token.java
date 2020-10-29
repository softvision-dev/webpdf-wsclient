package net.webpdf.wsclient.schema.beans;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Wraps the authentication token value into an onject
 */
@XmlRootElement
public class Token implements Serializable {

    @NotNull
    private String token = "";

    /**
     * Creates a {@link Token} with an existing authentication token value (from an existing session)
     *
     * @param token token of the existing session
     * @return token {@link Token}
     */
    public static Token create(String token) {
        Token newToken = new Token();
        newToken.setToken(token);
        return newToken;
    }

    /**
     * Returns the authentication token value
     *
     * @return authentication token value
     */
    @XmlElement
    @NotNull
    public String getToken() {
        return token;
    }

    /**
     * Sets a new authentication token value
     *
     * @param token the new token value
     */
    public void setToken(@NotNull String token) {
        this.token = token;
    }
}
