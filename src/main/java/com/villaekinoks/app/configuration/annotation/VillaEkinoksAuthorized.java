package com.villaekinoks.app.configuration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to challenge controller methods against all authenticated users.
 * When applied to a controller method, it ensures that only authenticated users
 * (any user with valid authentication) can access the method.
 * 
 * Usage:
 * @VillaEkinoksAuthorized
 * @GetMapping("/protected-endpoint")
 * public ResponseEntity<?> protectedMethod() {
 *     // Method implementation
 * }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VillaEkinoksAuthorized {
    
    /**
     * Optional message to include in the response when authorization fails.
     * Default message will be used if not specified.
     */
    String message() default "Access denied. Authentication required.";
    
    /**
     * Whether to require authentication for this endpoint.
     * Default is true.
     */
    boolean requireAuthentication() default true;
}