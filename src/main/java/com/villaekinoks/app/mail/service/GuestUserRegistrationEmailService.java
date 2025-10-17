package com.villaekinoks.app.mail.service;

import com.villaekinoks.app.mail.model.EmailData;
import com.villaekinoks.app.mail.model.EmailTemplate;
import com.villaekinoks.app.user.VillaGuestUser;
import com.villaekinoks.app.verification.VerificationPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestUserRegistrationEmailService {

    private final MailSenderService mailSenderService;

    @Async
    public void sendRegistrationVerificationEmail(VillaGuestUser guestUser, VerificationPair verificationPair, String locale) {
        try {
            if (guestUser.getPersonalinfo() == null || 
                guestUser.getPersonalinfo().getEmail() == null || 
                guestUser.getPersonalinfo().getEmail().isEmpty()) {
                log.warn("Cannot send registration verification email: guest user email is empty for user ID: {}", guestUser.getId());
                return;
            }

            String userEmail = guestUser.getPersonalinfo().getEmail();
            String firstName = guestUser.getPersonalinfo().getFirstname() != null ? 
                              guestUser.getPersonalinfo().getFirstname() : "";
            String lastName = guestUser.getPersonalinfo().getLastname() != null ? 
                             guestUser.getPersonalinfo().getLastname() : "";
            
            Map<String, Object> variables = buildEmailVariables(guestUser, verificationPair, firstName, lastName, userEmail);

            EmailData emailData = EmailData.builder()
                    .to(userEmail)
                    .template(EmailTemplate.GUEST_USER_REGISTRATION_VERIFICATION)
                    .locale(locale != null ? locale : "en")
                    .variables(variables)
                    .build();

            mailSenderService.sendEmail(emailData);
            
            log.info("Guest user registration verification email sent successfully to: {} ({} {})", 
                    userEmail, firstName, lastName);
            
        } catch (Exception e) {
            log.error("Failed to send guest user registration verification email to user ID: {}", 
                     guestUser.getId(), e);
        }
    }

    private Map<String, Object> buildEmailVariables(VillaGuestUser guestUser, VerificationPair verificationPair, 
                                                   String firstName, String lastName, String userEmail) {
        Map<String, Object> variables = new HashMap<>();
        
        // User information
        variables.put("firstName", firstName);
        variables.put("lastName", lastName);
        variables.put("email", userEmail);
        
        // Verification code
        variables.put("verificationCode", verificationPair.getVerificationcode());
        
        // Registration details
        variables.put("registrationTime", getCurrentFormattedTime());
        
        return variables;
    }

    private String getCurrentFormattedTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}