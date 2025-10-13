package com.villaekinoks.app.mail.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTemplate {
    BOOKING_PAYMENT_SUCCESS("booking-payment-success", "Booking Confirmation - Villa Ekinoks"),
    BOOKING_UNFINISHED("booking-unfinished", "Complete Your Booking - Villa Ekinoks"),
    NEW_CAMPAIGN_RELEASE("new-campaign-release", "New Campaign Available - Villa Ekinoks"),
    BOOKING_CONFIRMED_OWNER("booking-confirmed-owner", "New Booking Confirmed - Villa Ekinoks"),
    LOGIN_VERIFICATION("login-verification", "Login Verification - Villa Ekinoks");

    private final String templateName;
    private final String defaultSubject;

    public String getTemplatePath(String locale) {
        return String.format("mail-templates/%s/%s.html", locale, templateName);
    }
}