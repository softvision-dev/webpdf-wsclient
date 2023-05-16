package net.webpdf.wsclient.schema.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "JWTToken")
public class JWTToken implements Serializable {
    private @Nullable String aud;
    private @Nullable String sub;
    private int nbf;
    private @Nullable String azp;
    private @Nullable List<String> roles;
    private @Nullable String iss;
    private @Nullable String preferred_username;
    private int exp;
    private int iat;
    private @Nullable String jti;

    @XmlElement
    @SuppressWarnings("unused")
    public @Nullable String getAud() {
        return aud;
    }

    @SuppressWarnings("unused")
    public void setAud(@Nullable String aud) {
        this.aud = aud;
    }

    @XmlElement
    public @Nullable String getSub() {
        return sub;
    }

    public void setSub(@Nullable String sub) {
        this.sub = sub;
    }

    @XmlElement
    @SuppressWarnings("unused")
    public int getNbf() {
        return nbf;
    }

    @SuppressWarnings("unused")
    public void setNbf(int nbf) {
        this.nbf = nbf;
    }

    @XmlElement
    public @Nullable String getAzp() {
        return azp;
    }

    public void setAzp(@Nullable String azp) {
        this.azp = azp;
    }

    @XmlElement
    public @Nullable List<String> getRoles() {
        return roles;
    }

    public void setRoles(@Nullable List<String> roles) {
        this.roles = roles;
    }

    @XmlElement
    @SuppressWarnings("unused")
    public @Nullable String getIss() {
        return iss;
    }

    @SuppressWarnings("unused")
    public void setIss(@Nullable String iss) {
        this.iss = iss;
    }

    @XmlElement
    @SuppressWarnings("unused")
    public @Nullable String getPreferred_username() {
        return preferred_username;
    }

    @SuppressWarnings("unused")
    public void setPreferred_username(@Nullable String preferred_username) {
        this.preferred_username = preferred_username;
    }

    @XmlElement
    @SuppressWarnings("unused")
    public int getExp() {
        return exp;
    }

    @SuppressWarnings("unused")
    public void setExp(int exp) {
        this.exp = exp;
    }

    @XmlElement
    @SuppressWarnings("unused")
    public int getIat() {
        return iat;
    }

    @SuppressWarnings("unused")
    public void setIat(int iat) {
        this.iat = iat;
    }

    @XmlElement
    @SuppressWarnings("unused")
    public @Nullable String getJti() {
        return jti;
    }

    @SuppressWarnings("unused")
    public void setJti(@Nullable String jti) {
        this.jti = jti;
    }
}
