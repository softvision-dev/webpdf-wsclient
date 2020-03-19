package net.webpdf.wsclient.schema.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.webpdf.wsclient.schema.beans.limits.LimitsBean;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@SuppressWarnings("unused")
public class UserCredentialsBean implements Serializable {

    private boolean isAdmin;
    private boolean isUser;
    private boolean isAuthenticated;
    @Nullable
    private String[] roles;
    @Nullable
    private String userName;
    @Nullable
    private LimitsBean userLimits;

    /**
     * Returns true, if the user is an administrator.
     *
     * @return True, if the user is an administrator.
     */
    @XmlElement(name = "isAdmin")
    @JsonProperty("isAdmin")
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Set to true, if the user is an administrator.
     *
     * @param admin True, if the user is an administrator.
     */
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /**
     * Returns true, if the user is a standard user.
     *
     * @return True, if the user is a standard user.
     */
    @XmlElement(name = "isUser")
    @JsonProperty("isUser")
    public boolean isUser() {
        return isUser;
    }

    /**
     * Set to true, if the user is a standard user.
     *
     * @param user True, if the user is a standard user.
     */
    public void setUser(boolean user) {
        isUser = user;
    }

    /**
     * Returns true, if the user has been authenticated.
     *
     * @return True, if the user has been authenticated.
     */
    @XmlElement(name = "isAuthenticated")
    @JsonProperty("isAuthenticated")
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    /**
     * Set to true, if the user has been authenticated.
     *
     * @param authenticated True, if the user has been authenticated.
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
    @Nullable
    public String[] getRoles() {
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
    @Nullable
    public String getUserName() {
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
     * Returns the user limits for the current user.
     *
     * @return The user limits for the current user.
     */
    @XmlElement(name = "userLimits")
    @JsonProperty("userLimits")
    @Nullable
    public LimitsBean getUserLimits() {
        return userLimits;
    }

    /**
     * Sets the user limits for the current user.
     *
     * @param userLimits The user limits for the current user.
     */
    public void setUserLimits(@Nullable LimitsBean userLimits) {
        this.userLimits = userLimits;
    }
}
