package net.webpdf.wsclient.schema.beans;

import net.webpdf.wsclient.session.Session;
import org.jetbrains.annotations.NotNull;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * An instance of {@link Token} wraps the access token of a webPDF {@link Session} in an object.
 */
@XmlRootElement
public class Token implements Serializable {

    private @NotNull String token = "";

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

}
