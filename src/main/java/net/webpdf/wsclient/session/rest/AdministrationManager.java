package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.session.rest.documents.RestDocument;
import org.jetbrains.annotations.NotNull;

/**
 * An instance of {@link AdministrationManager} allows to administrate the webPDF server.
 *
 * @param <T_REST_DOCUMENT> The {@link RestDocument} used by the currently activs {@link RestSession}.
 */
public class AdministrationManager<T_REST_DOCUMENT extends RestDocument> {

    private final @NotNull RestSession<T_REST_DOCUMENT> session;

    /**
     * Initializes a {@link AdministrationManager} for the given {@link RestSession}.
     *
     * @param session The {@link RestSession} a {@link AdministrationManager} shall be created for.
     */
    public AdministrationManager(@NotNull RestSession<T_REST_DOCUMENT> session) {
        this.session = session;
    }

    /**
     * Returns the {@link RestSession} the {@link AdministrationManager} is managing {@link RestDocument}s for.
     *
     * @return The {@link RestSession} the {@link AdministrationManager} is managing {@link RestDocument}s for.
     */
    public @NotNull RestSession<T_REST_DOCUMENT> getSession() {
        return this.session;
    }

}
