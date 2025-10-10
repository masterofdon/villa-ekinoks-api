package com.villaekinoks.app.mail.service;

import com.villaekinoks.app.mail.model.EmailData;
import com.villaekinoks.app.mail.model.EmailTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignEmailService {

    private final MailSenderService mailSenderService;

    public void sendNewCampaignEmail(List<String> recipientEmails, String campaignTitle, String campaignDescription, String campaignLink, String locale) {
        for (String email : recipientEmails) {
            try {
                Map<String, Object> variables = new HashMap<>();
                variables.put("campaignTitle", campaignTitle);
                variables.put("campaignDescription", campaignDescription);
                variables.put("campaignLink", campaignLink);
                variables.put("unsubscribeLink", "https://villaekinoks.com/unsubscribe/" + email);

                EmailData emailData = EmailData.builder()
                        .to(email)
                        .template(EmailTemplate.NEW_CAMPAIGN_RELEASE)
                        .locale(locale)
                        .variables(variables)
                        .build();

                mailSenderService.sendEmail(emailData);
            } catch (Exception e) {
                log.error("Failed to send new campaign email to: {}", email, e);
            }
        }
    }

    public void sendNewCampaignEmail(String recipientEmail, String recipientName, String campaignTitle, String campaignDescription, String campaignLink, String locale) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("recipientName", recipientName);
            variables.put("campaignTitle", campaignTitle);
            variables.put("campaignDescription", campaignDescription);
            variables.put("campaignLink", campaignLink);
            variables.put("unsubscribeLink", "https://villaekinoks.com/unsubscribe/" + recipientEmail);

            EmailData emailData = EmailData.builder()
                    .to(recipientEmail)
                    .template(EmailTemplate.NEW_CAMPAIGN_RELEASE)
                    .locale(locale)
                    .variables(variables)
                    .build();

            mailSenderService.sendEmail(emailData);
        } catch (Exception e) {
            log.error("Failed to send new campaign email to: {}", recipientEmail, e);
        }
    }
}