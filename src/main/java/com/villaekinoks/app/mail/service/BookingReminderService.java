package com.villaekinoks.app.mail.service;

import com.villaekinoks.app.booking.VillaBooking;
import com.villaekinoks.app.booking.VillaBookingStatus;
import com.villaekinoks.app.booking.service.VillaBookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingReminderService {

    private final VillaBookingService villaBookingService;
    private final AsyncEmailService asyncEmailService;

    // Run every 6 hours
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000)
    public void sendUnfinishedBookingReminders() {
        log.info("Starting unfinished booking reminder process");
        
        try {
            // Get all pending bookings created more than 1 hour ago
            long oneDayAgo = Instant.now().minus(1, ChronoUnit.HOURS).toEpochMilli();
            
            // Note: You might need to add a method to VillaBookingService to find pending bookings older than X time
            // For now, we'll get all pending bookings and filter them
            Page<VillaBooking> pendingBookings = villaBookingService.getAll(
                new String[]{}, "", "", "", PageRequest.of(0, 100));
            
            pendingBookings.getContent().stream()
                .filter(booking -> booking.getStatus() == VillaBookingStatus.PENDING)
                .filter(booking -> booking.getTimestamps() != null && 
                                 booking.getTimestamps().getCreationdate() < oneDayAgo)
                .forEach(this::sendReminderEmail);
                
            log.info("Completed unfinished booking reminder process");
        } catch (Exception e) {
            log.error("Error in unfinished booking reminder process", e);
        }
    }

    private void sendReminderEmail(VillaBooking booking) {
        try {
            if (booking.getInquiror() != null && 
                booking.getInquiror().getPersonalinfo() != null &&
                booking.getInquiror().getPersonalinfo().getEmail() != null) {
                
                String guestName = booking.getInquiror().getPersonalinfo().getFirstname() + 
                                 " " + booking.getInquiror().getPersonalinfo().getLastname();
                
                asyncEmailService.sendBookingUnfinishedEmailAsync(
                    booking.getInquiror().getPersonalinfo().getEmail(),
                    guestName,
                    booking.getId(),
                    "en" // You could determine locale based on user preferences
                );
                
                log.info("Sent unfinished booking reminder for booking: {}", booking.getId());
            }
        } catch (Exception e) {
            log.error("Failed to send unfinished booking reminder for booking: {}", booking.getId(), e);
        }
    }
}