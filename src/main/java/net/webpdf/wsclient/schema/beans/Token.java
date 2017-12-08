package net.webpdf.wsclient.schema.beans;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Stores the authentication token
 */
@XmlRootElement
public class Token implements Serializable {

    private String token = "";

    /**
     * Returns the authentication token
     *
     * @return authentication token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets a new authentication token
     *
     * @param token the new token
     */
    public void setToken(String token) {
        this.token = token;
    }

}
