package net.webpdf.wsclient.session.rest.administration;

import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.RestWebServiceDocument;
import org.jetbrains.annotations.NotNull;

public class RestAdministrationManager extends AbstractAdministrationManager<RestWebServiceDocument>
        implements AdministrationManager<RestWebServiceDocument> {
    /**
     * Initializes a {@link RestAdministrationManager} for the given {@link RestSession}.
     *
     * @param session The {@link RestSession} a {@link AdministrationManager} shall be created for.
     */
    public RestAdministrationManager(@NotNull RestSession<RestWebServiceDocument> session) {
        super(session);
    }
}
