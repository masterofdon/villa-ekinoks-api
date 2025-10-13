package com.villaekinoks.app.mail.service;

import com.villaekinoks.app.mail.model.EmailData;
import com.villaekinoks.app.mail.model.EmailTemplate;
import com.villaekinoks.app.user.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginVerificationCodeEmailService {

    private final MailSenderService mailSenderService;

    @Async
    public void sendVerificationCodeEmail(AppUser user, String verificationCode, HttpServletRequest request) {
        try {
            String userLocale = getUserLocale(user);
            String userEmail = getUserEmail(user);
            String userName = getUserName(user);
            
            if (userEmail == null || userEmail.isEmpty()) {
                log.warn("Cannot send verification code email: user email is empty for user ID: {}", user.getId());
                return;
            }

            Map<String, Object> variables = buildEmailVariables(user, verificationCode, request, userName, userEmail);

            EmailData emailData = EmailData.builder()
                    .to(userEmail)
                    .template(EmailTemplate.LOGIN_VERIFICATION_CODE)
                    .locale(userLocale)
                    .variables(variables)
                    .build();

            mailSenderService.sendEmail(emailData);
            
            log.info("Verification code email sent successfully to user: {} ({})", userName, userEmail);
            
        } catch (Exception e) {
            log.error("Failed to send verification code email to user: {}", user.getId(), e);
        }
    }

    private Map<String, Object> buildEmailVariables(AppUser user, String verificationCode, HttpServletRequest request, String userName, String userEmail) {
        Map<String, Object> variables = new HashMap<>();
        
        // User information
        variables.put("userName", userName != null ? userName : "User");
        variables.put("userEmail", userEmail);
        
        // Verification code
        variables.put("verificationCode", verificationCode);
        
        // Login details
        variables.put("loginTime", getCurrentFormattedTime());
        variables.put("ipAddress", getClientIpAddress(request));
        variables.put("deviceInfo", getUserAgent(request));
        
        return variables;
    }

    private String getUserLocale(AppUser user) {
        if (user.getLocalesettings() != null && user.getLocalesettings().getLocale() != null) {
            return user.getLocalesettings().getLocale();
        }
        return "en"; // Default to English
    }

    private String getUserEmail(AppUser user) {
        if (user.getPersonalinfo() != null && user.getPersonalinfo().getEmail() != null) {
            return user.getPersonalinfo().getEmail();
        }
        return user.getLogin(); // Fallback to login if it's an email
    }

    private String getUserName(AppUser user) {
        if (user.getPersonalinfo() != null) {
            if (user.getPersonalinfo().getDisplayname() != null) {
                return user.getPersonalinfo().getDisplayname();
            }
            
            StringBuilder name = new StringBuilder();
            if (user.getPersonalinfo().getFirstname() != null) {
                name.append(user.getPersonalinfo().getFirstname());
            }
            if (user.getPersonalinfo().getLastname() != null) {
                if (name.length() > 0) name.append(" ");
                name.append(user.getPersonalinfo().getLastname());
            }
            
            if (name.length() > 0) {
                return name.toString();
            }
        }
        return user.getLogin(); // Fallback to login
    }

    private String getCurrentFormattedTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "Unknown";
        }
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "Unknown";
    }

    private String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "Unknown";
        }
        
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "Unknown";
    }
}