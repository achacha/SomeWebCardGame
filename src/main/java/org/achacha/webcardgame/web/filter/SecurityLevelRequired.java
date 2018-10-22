package org.achacha.webcardgame.web.filter;

import org.achacha.base.security.SecurityLevel;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for REST calls to require certain authentication level
 */
@NameBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface SecurityLevelRequired {
    SecurityLevel value() default SecurityLevel.AUTHENTICATED;
    boolean requiresSuperuser() default false;
}