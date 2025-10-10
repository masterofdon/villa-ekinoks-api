package com.villaekinoks.app.mail.service;

import com.villaekinoks.app.booking.VillaBooking;
import com.villaekinoks.app.mail.model.EmailData;
import com.villaekinoks.app.mail.model.EmailTemplate;
import com.villaekinoks.app.user.VillaGuestUser;
import com.villaekinoks.app.villa.Villa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingEmailService {

    private final MailSenderService mailSenderService;

    public void sendBookingPaymentSuccessEmail(VillaBooking booking, String locale) {
        try {
            VillaGuestUser guest = booking.getInquiror();
            Villa villa = booking.getVilla();

            Map<String, Object> variables = new HashMap<>();
            variables.put("guestName", guest.getPersonalinfo().getFirstname() + " " + guest.getPersonalinfo().getLastname());
            variables.put("villaName", villa.getPublicinfo().getName());
            variables.put("bookingId", booking.getId());
            variables.put("checkInDate", formatDate(booking.getStartdate()));
            variables.put("checkOutDate", formatDate(booking.getEnddate()));
            variables.put("numberOfGuests", booking.getNumberofguests());
            variables.put("totalAmount", booking.getBookingpayment() != null ? booking.getBookingpayment().getAmount() : "N/A");
            variables.put("currency", booking.getBookingpayment() != null ? booking.getBookingpayment().getCurrency() : "");

            EmailData emailData = EmailData.builder()
                    .to(guest.getPersonalinfo().getEmail())
                    .template(EmailTemplate.BOOKING_PAYMENT_SUCCESS)
                    .locale(locale)
                    .variables(variables)
                    .build();

            mailSenderService.sendEmail(emailData);
        } catch (Exception e) {
            log.error("Failed to send booking payment success email for booking: {}", booking.getId(), e);
        }
    }

    public void sendBookingUnfinishedEmail(String guestEmail, String guestName, String bookingId, String locale) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("guestName", guestName);
            variables.put("bookingId", bookingId);
            variables.put("completionLink", "https://villaekinoks.com/booking/complete/" + bookingId);

            EmailData emailData = EmailData.builder()
                    .to(guestEmail)
                    .template(EmailTemplate.BOOKING_UNFINISHED)
                    .locale(locale)
                    .variables(variables)
                    .build();

            mailSenderService.sendEmail(emailData);
        } catch (Exception e) {
            log.error("Failed to send booking unfinished email for booking: {}", bookingId, e);
        }
    }

    public void sendBookingConfirmedOwnerEmail(VillaBooking booking, String ownerEmail, String locale) {
        try {
            VillaGuestUser guest = booking.getInquiror();
            Villa villa = booking.getVilla();

            Map<String, Object> variables = new HashMap<>();
            variables.put("villaName", villa.getPublicinfo().getName());
            variables.put("bookingId", booking.getId());
            variables.put("guestName", guest.getPersonalinfo().getFirstname() + " " + guest.getPersonalinfo().getLastname());
            variables.put("guestEmail", guest.getPersonalinfo().getEmail());
            variables.put("guestPhone", guest.getPersonalinfo().getPhonenumber());
            variables.put("checkInDate", formatDate(booking.getStartdate()));
            variables.put("checkOutDate", formatDate(booking.getEnddate()));
            variables.put("numberOfGuests", booking.getNumberofguests());
            variables.put("totalAmount", booking.getBookingpayment() != null ? booking.getBookingpayment().getAmount() : "N/A");
            variables.put("currency", booking.getBookingpayment() != null ? booking.getBookingpayment().getCurrency() : "");

            EmailData emailData = EmailData.builder()
                    .to(ownerEmail)
                    .template(EmailTemplate.BOOKING_CONFIRMED_OWNER)
                    .locale(locale)
                    .variables(variables)
                    .build();

            mailSenderService.sendEmail(emailData);
        } catch (Exception e) {
            log.error("Failed to send booking confirmed owner email for booking: {}", booking.getId(), e);
        }
    }

    private String formatDate(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMdd"));
            return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            log.warn("Failed to format date: {}", dateString);
            return dateString;
        }
    }
}