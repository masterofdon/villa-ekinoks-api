package com.villaekinoks.app.configuration.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.villaekinoks.app.configuration.annotation.VillaEkinoksAuthorized;
import com.villaekinoks.app.exception.NotAuthorizedException;
import com.villaekinoks.app.generic.api.GenericApiResponseCodes;

/**
 * Aspect that handles the @VillaEkinoksAuthorized annotation.
 * Intercepts method calls annotated with @VillaEkinoksAuthorized and
 * verifies that the user is authenticated before allowing method execution.
 */
@Aspect
@Component
public class VillaEkinoksAuthorizationAspect {

    /**
     * Around advice that intercepts methods annotated with @VillaEkinoksAuthorized
     * and performs authentication validation.
     */
    @Around("@annotation(villaEkinoksAuthorized)")
    public Object checkAuthorization(ProceedingJoinPoint joinPoint, VillaEkinoksAuthorized villaEkinoksAuthorized) throws Throwable {
        
        // Check if authentication is required for this annotation
        if (!villaEkinoksAuthorized.requireAuthentication()) {
            return joinPoint.proceed();
        }
        
        // Get the current authentication from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            
            // User is not authenticated, throw authorization exception
            throw new NotAuthorizedException(
                villaEkinoksAuthorized.message(),
                GenericApiResponseCodes.AuthenticationResponseCodes.BAD_CREDENTIALS
            );
        }
        
        // User is authenticated, proceed with method execution
        return joinPoint.proceed();
    }
    
    /**
     * Around advice for class-level @VillaEkinoksAuthorized annotation.
     * Applies to all methods in classes annotated with @VillaEkinoksAuthorized.
     */
    @Around("@within(villaEkinoksAuthorized) && execution(public * *(..))")
    public Object checkClassLevelAuthorization(ProceedingJoinPoint joinPoint, VillaEkinoksAuthorized villaEkinoksAuthorized) throws Throwable {
        return checkAuthorization(joinPoint, villaEkinoksAuthorized);
    }
}