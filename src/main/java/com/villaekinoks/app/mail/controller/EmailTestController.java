package com.villaekinoks.app.mail.controller;

import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.mail.service.AsyncEmailService;
import com.villaekinoks.app.mail.service.CampaignEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailTestController {

    private final CampaignEmailService campaignEmailService;
    private final AsyncEmailService asyncEmailService;
    private final com.villaekinoks.app.mail.service.LoginVerificationEmailService loginVerificationEmailService;
    private final com.villaekinoks.app.user.service.AppUserService appUserService;

    @PostMapping("/test-campaign")
    public GenericApiResponse<String> testCampaignEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "Test User") String name,
            @RequestParam(defaultValue = "en") String locale) {
        
        campaignEmailService.sendNewCampaignEmail(
                email, 
                name, 
                "Summer Special - 30% Off", 
                "Book your dream villa this summer with 30% discount on all bookings made before July 31st.", 
                "https://villaekinoks.com/campaigns/summer-special", 
                locale);
        
        return new GenericApiResponse<>(
                HttpStatus.OK.value(),
                GenericApiResponseMessages.Generic.SUCCESS,
                "200#EMAIL01",
                "Campaign email sent successfully to " + email);
    }

    @PostMapping("/test-unfinished-booking")
    public GenericApiResponse<String> testUnfinishedBookingEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "Test User") String name,
            @RequestParam(defaultValue = "BOOK123") String bookingId,
            @RequestParam(defaultValue = "en") String locale) {
        
        asyncEmailService.sendBookingUnfinishedEmailAsync(email, name, bookingId, locale);
        
        return new GenericApiResponse<>(
                HttpStatus.OK.value(),
                GenericApiResponseMessages.Generic.SUCCESS,
                "200#EMAIL02",
                "Unfinished booking email sent successfully to " + email);
    }

    @PostMapping("/test-login-verification")
    public GenericApiResponse<String> testLoginVerificationEmail(
            @RequestParam String userId,
            jakarta.servlet.http.HttpServletRequest request) {
        
        com.villaekinoks.app.user.AppUser user = appUserService.getById(userId);
        
        if (user == null) {
            return new GenericApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "User not found",
                    "404#EMAIL03",
                    "User with ID " + userId + " not found");
        }
        
        loginVerificationEmailService.sendLoginVerificationEmail(user, request);
        
        String userEmail = user.getPersonalinfo() != null && user.getPersonalinfo().getEmail() != null 
                ? user.getPersonalinfo().getEmail() 
                : user.getLogin();
        
        return new GenericApiResponse<>(
                HttpStatus.OK.value(),
                GenericApiResponseMessages.Generic.SUCCESS,
                "200#EMAIL03",
                "Login verification email sent successfully to " + userEmail);
    }
}