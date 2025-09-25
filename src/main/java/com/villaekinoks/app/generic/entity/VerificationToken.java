package com.villaekinoks.app.generic.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class VerificationToken {

    private String verificationid;

    private String code;
}
