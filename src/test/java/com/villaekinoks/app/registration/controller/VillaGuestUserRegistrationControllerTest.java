package com.villaekinoks.app.registration.controller;

import com.villaekinoks.app.generic.entity.Currency;
import com.villaekinoks.app.mail.service.AsyncEmailService;
import com.villaekinoks.app.registration.xaction.Create_VillaGuestUser_WC_MLS_XAction;
import com.villaekinoks.app.user.VillaGuestUser;
import com.villaekinoks.app.user.service.VillaGuestUserRegistrationService;
import com.villaekinoks.app.user.service.VillaGuestUserService;
import com.villaekinoks.app.verification.VerificationPair;
import com.villaekinoks.app.verification.service.VerificationPairService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VillaGuestUserRegistrationController.class)
class VillaGuestUserRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VillaGuestUserRegistrationService villaGuestUserRegistrationService;

    @MockitoBean
    private VillaGuestUserService villaGuestUserService;

    @MockitoBean
    private VerificationPairService verificationPairService;

    @MockitoBean
    private AsyncEmailService asyncEmailService;

    private VillaGuestUser mockGuestUser;
    private VerificationPair mockVerificationPair;
    private Create_VillaGuestUser_WC_MLS_XAction testRequest;

    @BeforeEach
    void setUp() {
        // Setup mock guest user
        mockGuestUser = new VillaGuestUser();
        mockGuestUser.setId("test-user-id");

        // Setup mock verification pair
        mockVerificationPair = new VerificationPair();
        mockVerificationPair.setId("test-verification-id");
        mockVerificationPair.setVerificationcode("123456");
        mockVerificationPair.setUserid("test-user-id");

        // Setup test request
        testRequest = new Create_VillaGuestUser_WC_MLS_XAction();
        testRequest.setEmail("test@example.com");
        testRequest.setFirstname("John");
        testRequest.setLastname("Doe");
        testRequest.setLocale("en");
        testRequest.setCurrency(Currency.USD);
    }

    @Test
    void createVillaGuestUser_ShouldCreateUserAndSendVerificationEmail() throws Exception {
        // Given
        when(villaGuestUserService.getByLogin(testRequest.getEmail())).thenReturn(null);
        when(villaGuestUserRegistrationService.registerNewUser(
                anyString(), anyString(), anyString(), anyString(), anyString(), 
                anyString(), anyString(), anyString(), anyString(), any(Currency.class)))
                .thenReturn(mockGuestUser);
        when(verificationPairService.create(any(VerificationPair.class)))
                .thenReturn(mockVerificationPair);

        // When & Then
        mockMvc.perform(post("/api/v1/user-registrations/villa-guest-users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statuscode").value(201))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.registrationverificationid").value("test-verification-id"));

        // Verify that the email service was called
        verify(asyncEmailService, times(1)).sendGuestUserRegistrationVerificationEmailAsync(
                eq(mockGuestUser), 
                eq(mockVerificationPair), 
                eq(testRequest.getLocale()));
    }

    @Test
    void createVillaGuestUser_ExistingUser_ShouldStillSendVerificationEmail() throws Exception {
        // Given - user already exists
        when(villaGuestUserService.getByLogin(testRequest.getEmail())).thenReturn(mockGuestUser);
        when(verificationPairService.create(any(VerificationPair.class)))
                .thenReturn(mockVerificationPair);

        // When & Then
        mockMvc.perform(post("/api/v1/user-registrations/villa-guest-users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated());

        // Verify that registration service is NOT called for existing user
        verify(villaGuestUserRegistrationService, never()).registerNewUser(
                anyString(), anyString(), anyString(), anyString(), anyString(), 
                anyString(), anyString(), anyString(), anyString(), any(Currency.class));

        // Verify that the email service was still called
        verify(asyncEmailService, times(1)).sendGuestUserRegistrationVerificationEmailAsync(
                eq(mockGuestUser), 
                eq(mockVerificationPair), 
                eq(testRequest.getLocale()));
    }
}