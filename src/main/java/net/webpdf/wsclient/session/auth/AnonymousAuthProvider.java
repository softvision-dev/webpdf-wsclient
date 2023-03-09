package net.webpdf.wsclient.session.auth;

import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.material.AnonymousMaterial;

/**
 * <p>
 * An instance of {@link AnonymousAuthProvider} demands the initialization of an anonymous {@link Session}.<br>
 * <b>Be aware:</b> You should make sure, that your webPDF server allows anonymous {@link Session}s, otherwise you can
 * not use the {@link AnonymousAuthProvider}.
 * </p>
 * <p>
 * <b>Be aware:</b> Currently an {@link AnonymousAuthProvider} shall only serve one {@link Session} at a
 * time. An {@link AnonymousAuthProvider} being called by another {@link Session} than it´s current master,
 * shall assume it´s current master to have expired and shall, try to reauthorize that new {@link Session}
 * (new master).<br>
 * For that reason an {@link AnonymousAuthProvider}s shall be reusable by subsequent {@link Session}s.
 * </p>
 * <p>
 * <b>Be aware:</b> However - An implementation of {@link SessionAuthProvider} is not required to serve multiple
 * {@link Session}s at a time. It is expected to create a new {@link SessionAuthProvider} for each existing
 * {@link Session}.
 * </p>
 */
public class AnonymousAuthProvider extends AbstractAuthenticationProvider {

    /**
     * <p>
     * Instantiates a new {@link AnonymousAuthProvider} to establish an anonymous {@link Session}.<br>
     * <b>Be aware:</b> You should make sure, that your webPDF server allows anonymous {@link Session}s, otherwise you
     * can not use the {@link AnonymousAuthProvider}.
     * </p>
     * <p>
     * <b>Be aware:</b> Currently an {@link AnonymousAuthProvider} shall only serve one {@link Session} at a
     * time. An {@link AnonymousAuthProvider} being called by another {@link Session} than it´s current master,
     * shall assume it´s current master to have expired and shall, try to reauthorize that new {@link Session}
     * (new master).<br>
     * For that reason an {@link AnonymousAuthProvider}s shall be reusable by subsequent {@link Session}s.
     * </p>
     */
    public AnonymousAuthProvider() {
        super(new AnonymousMaterial());
    }

}
