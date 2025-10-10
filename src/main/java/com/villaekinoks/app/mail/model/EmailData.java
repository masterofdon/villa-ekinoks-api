package com.villaekinoks.app.mail.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailData {
    private String to;
    private String subject;
    private EmailTemplate template;
    private String locale;
    private Map<String, Object> variables;
    private String cc;
    private String bcc;
}