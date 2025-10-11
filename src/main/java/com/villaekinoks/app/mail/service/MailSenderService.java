package com.villaekinoks.app.mail.service;

import com.villaekinoks.app.mail.model.EmailData;
import com.villaekinoks.app.mail.model.EmailTemplate;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderService {

    private final JavaMailSender mailSender;

    public void sendEmail(EmailData emailData) {
        try {
            String htmlContent = loadTemplate(emailData.getTemplate(), emailData.getLocale());
            htmlContent = processTemplate(htmlContent, emailData.getVariables());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setTo(emailData.getTo());
            helper.setSubject(emailData.getSubject() != null ? emailData.getSubject() : emailData.getTemplate().getDefaultSubject());
            helper.setText(htmlContent, true);
            helper.setFrom("info@villaekinoks.com");

            if (StringUtils.hasText(emailData.getCc())) {
                helper.setCc(emailData.getCc());
            }

            if (StringUtils.hasText(emailData.getBcc())) {
                helper.setBcc(emailData.getBcc());
            }

            mailSender.send(message);
            log.info("Email sent successfully to: {} with template: {}", emailData.getTo(), emailData.getTemplate().getTemplateName());

        } catch (Exception e) {
            log.error("Failed to send email to: {} with template: {}", emailData.getTo(), emailData.getTemplate().getTemplateName(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String loadTemplate(EmailTemplate template, String locale) throws IOException {
        // Default locale if not provided
        String actualLocale = locale != null ? locale : "en";
        
        try {
            // Try to load template for requested locale from root mail-templates folder
            Path templatePath = Paths.get("mail-templates", actualLocale, template.getTemplateName() + ".html");
            if (Files.exists(templatePath)) {
                return Files.readString(templatePath, StandardCharsets.UTF_8);
            } else {
                log.warn("Template not found for locale: {} at path: {}, falling back to English", actualLocale, templatePath);
                throw new IOException("Template not found for locale: " + actualLocale);
            }
        } catch (Exception e) {
            log.warn("Template not found for locale: {}, falling back to English", actualLocale);
            // Fallback to English if the requested locale is not available
            Path englishTemplatePath = Paths.get("mail-templates", "en", template.getTemplateName() + ".html");
            if (Files.exists(englishTemplatePath)) {
                return Files.readString(englishTemplatePath, StandardCharsets.UTF_8);
            } else {
                throw new IOException("No template found for " + template.getTemplateName() + " in any locale");
            }
        }
    }

    private String processTemplate(String template, Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return template;
        }

        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }
}