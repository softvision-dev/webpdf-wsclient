package net.webpdf.wsclient.testsuite.integration.annotations;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(OAuthTestConditionExt.class)
public @interface OAuthTest {

    @NotNull OAuthProviderSelection provider();

}
