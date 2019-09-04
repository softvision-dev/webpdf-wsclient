package net.webpdf.wsclient.schema.beans;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Stores the authentication token
 */
@SuppressWarnings({"unused"})
@XmlRootElement
public class Token implements Serializable {

    @NotNull
    private String token = "";

    /**
     * Returns the authentication token
     *
     * @return authentication token
     */
    @NotNull
    public String getToken() {
        return token;
    }

    /**
     * Sets a new authentication token
     *
     * @param token the new token
     */
    public void setToken(@NotNull String token) {
        this.token = token;
    }

}
