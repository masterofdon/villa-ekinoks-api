package com.villaekinoks.app.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.configuration.annotation.VillaEkinoksAuthorized;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;

/**
 * Example controller demonstrating the usage of @VillaEkinoksAuthorized annotation.
 * This controller shows how to protect endpoints with authentication requirements.
 */
@RestController
@RequestMapping("/api/v1/examples")
public class ExampleAuthorizationController {

    /**
     * Example of a method-level @VillaEkinoksAuthorized annotation.
     * This endpoint requires authentication to access.
     */
    @VillaEkinoksAuthorized
    @GetMapping("/protected-method")
    public GenericApiResponse<String> protectedMethod() {
        return new GenericApiResponse<>(
            HttpStatus.OK.value(),
            GenericApiResponseMessages.Generic.SUCCESS,
            "200#0000",
            "This is a protected endpoint. You must be authenticated to see this message."
        );
    }

    /**
     * Example of a method with custom authorization message.
     */
    @VillaEkinoksAuthorized(message = "Custom message: You need to be logged in to access this resource.")
    @GetMapping("/protected-with-message")
    public GenericApiResponse<String> protectedMethodWithCustomMessage() {
        return new GenericApiResponse<>(
            HttpStatus.OK.value(),
            GenericApiResponseMessages.Generic.SUCCESS,
            "200#0000",
            "This endpoint has a custom authorization message."
        );
    }

    /**
     * Example of a method that doesn't require authentication.
     */
    @VillaEkinoksAuthorized(requireAuthentication = false)
    @GetMapping("/public-endpoint")
    public GenericApiResponse<String> publicEndpoint() {
        return new GenericApiResponse<>(
            HttpStatus.OK.value(),
            GenericApiResponseMessages.Generic.SUCCESS,
            "200#0000",
            "This endpoint is public and doesn't require authentication."
        );
    }

    /**
     * Example of an endpoint without any annotation (will follow global security rules).
     */
    @GetMapping("/default-security")
    public GenericApiResponse<String> defaultSecurity() {
        return new GenericApiResponse<>(
            HttpStatus.OK.value(),
            GenericApiResponseMessages.Generic.SUCCESS,
            "200#0000",
            "This endpoint follows default Spring Security rules."
        );
    }
}

/**
 * Example of a class-level @VillaEkinoksAuthorized annotation.
 * All methods in this class will require authentication unless overridden.
 */
@VillaEkinoksAuthorized
@RestController
@RequestMapping("/api/v1/examples/class-level")
class ExampleClassLevelAuthorizationController {

    @GetMapping("/protected-by-class")
    public GenericApiResponse<String> protectedByClass() {
        return new GenericApiResponse<>(
            HttpStatus.OK.value(),
            GenericApiResponseMessages.Generic.SUCCESS,
            "200#0000",
            "This method is protected by class-level @VillaEkinoksAuthorized."
        );
    }

    /**
     * This method overrides the class-level requirement.
     */
    @VillaEkinoksAuthorized(requireAuthentication = false)
    @GetMapping("/public-override")
    public GenericApiResponse<String> publicOverride() {
        return new GenericApiResponse<>(
            HttpStatus.OK.value(),
            GenericApiResponseMessages.Generic.SUCCESS,
            "200#0000",
            "This method overrides the class-level authentication requirement."
        );
    }
}