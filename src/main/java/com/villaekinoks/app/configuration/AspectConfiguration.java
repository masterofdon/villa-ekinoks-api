package com.villaekinoks.app.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration class to enable AspectJ support for the application.
 * This enables the use of custom aspects like @VillaEkinoksAuthorized annotation.
 */
@Configuration
@EnableAspectJAutoProxy
public class AspectConfiguration {
    // AspectJ configuration - no additional beans needed for basic aspect support
}