package net.webpdf.wsclient.session.rest.user;

import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.RestWebServiceDocument;
import org.jetbrains.annotations.NotNull;

/**
 * Concrete {@link UserManager} for {@link RestWebServiceDocument}-based sessions.
 */
public class RestUserManager extends AbstractUserManager<RestWebServiceDocument>
        implements UserManager<RestWebServiceDocument> {

    /**
     * Initializes a {@link RestUserManager} for the given {@link RestSession}.
     *
     * @param session The {@link RestSession} a {@link UserManager} shall be created for.
     */
    public RestUserManager(@NotNull RestSession<RestWebServiceDocument> session) {
        super(session);
    }

}
