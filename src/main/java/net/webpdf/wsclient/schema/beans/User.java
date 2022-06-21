package net.webpdf.wsclient.schema.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.webpdf.wsclient.schema.beans.limits.Limits;
import org.jetbrains.annotations.Nullable;

import jakarta.xml.bind.annotation.XmlElement;

import java.io.Serializable;

/**
 * An instance of {@link User} is intended to wrap and simplify information about a webPDF server user.
 */
@SuppressWarnings("unused")
public class User implements Serializable {

    private boolean isAdmin;
    private boolean isUser;
    private boolean isAuthenticated;
    private @Nullable String[] roles;
    private @Nullable String userName;
    private @Nullable Limits userLimits;

    /**
     * Returns {@code true}, if the user is an administrator.
     *
     * @return {@code true}, if the user is an administrator.
     */
    @XmlElement(name = "isAdmin")
    @JsonProperty("isAdmin")
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Set to {@code true}, if the user is an administrator.
     *
     * @param admin {@code true}, if the user is an administrator.
     */
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /**
     * Returns {@code true}, if the user is a standard user.
     *
     * @return {@code true}, if the user is a standard user.
     */
    @XmlElement(name = "isUser")
    @JsonProperty("isUser")
    public boolean isUser() {
        return isUser;
    }

    /**
     * Set to {@code true}, if the user is a standard user.
     *
     * @param user {@code true}, if the user is a standard user.
     */
    public void setUser(boolean user) {
        isUser = user;
    }

    /**
     * Returns {@code true}, if the user has been authenticated.
     *
     * @return {@code true}, if the user has been authenticated.
     */
    @XmlElement(name = "isAuthenticated")
    @JsonProperty("isAuthenticated")
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    /**
     * Set to {@code true}, if the user has been authenticated.
     *
     * @param authenticated {@code true}, if the user has been authenticated.
     */
    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    /**
     * Returns all active roles of the user.
     *
     * @return All active roles of the user.
     */
    @XmlElement
    public @Nullable String[] getRoles() {
        return roles;
    }

    /**
     * Set all active roles of the user.
     *
     * @param roles All active roles of the user.
     */
    public void setRoles(@Nullable String[] roles) {
        this.roles = roles;
    }

    /**
     * Returns the name of the current user.
     *
     * @return The name of the current user.
     */
    @XmlElement
    public @Nullable String getUserName() {
        return userName;
    }

    /**
     * Sets the name of the current user.
     *
     * @param userName The name of the current user.
     */
    public void setUserName(@Nullable String userName) {
        this.userName = userName;
    }

    /**
     * Returns the {@link Limits} for the current user.
     *
     * @return The {@link Limits} for the current user.
     */
    @XmlElement(name = "userLimits")
    @JsonProperty("userLimits")
    public @Nullable Limits getUserLimits() {
        return userLimits;
    }

    /**
     * Sets the user {@link Limits} for the current user.
     *
     * @param userLimits The user {@link Limits} for the current user.
     */
    public void setUserLimits(@Nullable Limits userLimits) {
        this.userLimits = userLimits;
    }

}
