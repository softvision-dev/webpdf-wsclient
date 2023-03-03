package net.webpdf.wsclient.session.auth;

import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.material.AnonymousMaterial;

/**
 * <p>
 * An instance of {@link AnonymousAuthProvider} demands the initialization of an anonymous {@link Session}.<br>
 * <b>Be aware:</b> You should make sure, that your webPDF server allows anonymous {@link Session}s, otherwise you can
 * not use the {@link AnonymousAuthProvider}.
 * </p>
 */
public class AnonymousAuthProvider extends AbstractAuthenticationProvider {

    /**
     * <p>
     * Instantiates a new {@link AnonymousAuthProvider} to establish an anonymous {@link Session}.<br>
     * <b>Be aware:</b> You should make sure, that your webPDF server allows anonymous {@link Session}s, otherwise you
     * can not use the {@link AnonymousAuthProvider}.
     * </p>
     */
    public AnonymousAuthProvider() {
        super(new AnonymousMaterial());
    }

}
