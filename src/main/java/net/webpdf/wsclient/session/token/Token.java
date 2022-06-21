package net.webpdf.wsclient.session.token;

import net.webpdf.wsclient.session.Session;
import org.jetbrains.annotations.NotNull;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * An instance of {@link Token} wraps the access token of a webPDF {@link Session} in an object.
 */
@SuppressWarnings("unused")
@XmlRootElement
public class Token implements Serializable {

    private @NotNull String token = "";
    private boolean accessToken = false;

    /**
     * Creates a {@link Token} with a preexisting access token value.
     *
     * @param token token {@code String} value of the existing session
     * @return token {@link Token}
     */
    public static @NotNull Token create(@NotNull String token) {
        Token newToken = new Token();
        newToken.setToken(token);
        return newToken;
    }

    /**
     * Creates a {@link Token} that represents an access token, as provided by an OAuth authorization
     * provider, rendering a further webPDF login attempt superfluous.
     *
     * @param token token of the existing session
     * @return token {@link Token}
     */
    public static Token createAccessToken(String token) {
        return create(token).setAccessToken(true);
    }

    /**
     * Returns the access token {@code String} value.
     *
     * @return The access token {@code String} value.
     */
    @XmlElement
    public @NotNull String getToken() {
        return token;
    }

    /**
     * Sets the access token {@code String} value.
     *
     * @param token The access token {@code String} value.
     */
    public void setToken(@NotNull String token) {
        this.token = token;
    }

    /**
     * Returns {@code true}, if the token represents an access token, as provided by an OAuth authorization
     * provider, rendering a further webPDF login attempt superfluous.
     *
     * @return {@code true}, if the token has been provided by a {@link TokenProvider}.
     */
    public boolean isAccessToken() {
        return accessToken;
    }

    /**
     * Set to {@code true}, if the {@link Token} represents an access token, as provided by an OAuth authorization
     * provider, rendering a further webPDF login attempt superfluous.
     *
     * @param provided {@code true}, if the {@link Token} has been provided by a {@link TokenProvider}.
     * @return Returns the updated {@link Token} itself.
     */
    private Token setAccessToken(boolean provided) {
        this.accessToken = provided;
        return this;
    }

}
