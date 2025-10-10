package com.villaekinoks.app.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncEmailService {

    private final BookingEmailService bookingEmailService;
    private final CampaignEmailService campaignEmailService;

    @Async
    public CompletableFuture<Void> sendBookingPaymentSuccessEmailAsync(
            com.villaekinoks.app.booking.VillaBooking booking, String locale) {
        try {
            bookingEmailService.sendBookingPaymentSuccessEmail(booking, locale);
            log.info("Booking payment success email sent asynchronously for booking: {}", booking.getId());
        } catch (Exception e) {
            log.error("Failed to send booking payment success email asynchronously for booking: {}", booking.getId(), e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> sendBookingConfirmedOwnerEmailAsync(
            com.villaekinoks.app.booking.VillaBooking booking, String ownerEmail, String locale) {
        try {
            bookingEmailService.sendBookingConfirmedOwnerEmail(booking, ownerEmail, locale);
            log.info("Booking confirmed owner email sent asynchronously for booking: {}", booking.getId());
        } catch (Exception e) {
            log.error("Failed to send booking confirmed owner email asynchronously for booking: {}", booking.getId(), e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> sendBookingUnfinishedEmailAsync(
            String guestEmail, String guestName, String bookingId, String locale) {
        try {
            bookingEmailService.sendBookingUnfinishedEmail(guestEmail, guestName, bookingId, locale);
            log.info("Booking unfinished email sent asynchronously for booking: {}", bookingId);
        } catch (Exception e) {
            log.error("Failed to send booking unfinished email asynchronously for booking: {}", bookingId, e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> sendNewCampaignEmailAsync(
            String recipientEmail, String recipientName, String campaignTitle, 
            String campaignDescription, String campaignLink, String locale) {
        try {
            campaignEmailService.sendNewCampaignEmail(recipientEmail, recipientName, campaignTitle, 
                campaignDescription, campaignLink, locale);
            log.info("New campaign email sent asynchronously to: {}", recipientEmail);
        } catch (Exception e) {
            log.error("Failed to send new campaign email asynchronously to: {}", recipientEmail, e);
        }
        return CompletableFuture.completedFuture(null);
    }
}