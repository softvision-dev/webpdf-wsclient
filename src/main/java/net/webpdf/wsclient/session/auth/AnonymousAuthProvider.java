package net.webpdf.wsclient.session.auth;

import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.material.AnonymousMaterial;
import net.webpdf.wsclient.session.auth.material.token.SessionToken;

/**
 * <p>
 * An instance of {@link AnonymousAuthProvider} demands the initialization of an anonymous {@link Session}.<br>
 * <b>Be aware:</b> You should make sure, that your webPDF server allows anonymous {@link Session}s, otherwise you can
 * not use the {@link AnonymousAuthProvider}.
 * </p>
 * <p>
 * <b>Be aware:</b> An {@link AnonymousAuthProvider} serves one {@link Session}. After the login it keeps the
 * {@link SessionToken} of that server session and provides it to every {@link Session} it is used for;
 * closing a {@link Session} ends that server session. A provider is therefore not reusable for a
 * subsequent {@link Session}: create a new {@link AuthProvider} for each {@link Session}.
 * </p>
 * <p>
 * <b>Be aware:</b> However - An implementation of {@link AuthProvider} is not required to serve multiple
 * {@link Session}s at a time. It is expected to create a new {@link AuthProvider} for each existing
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
     * <b>Be aware:</b> An {@link AnonymousAuthProvider} serves one {@link Session}. After the login it keeps the
     * {@link SessionToken} of that server session and provides it to every {@link Session} it is used for;
     * closing a {@link Session} ends that server session. A provider is therefore not reusable for a
     * subsequent {@link Session}: create a new {@link AuthProvider} for each {@link Session}.
     * </p>
     */
    public AnonymousAuthProvider() {
        super(new AnonymousMaterial());
    }

}
