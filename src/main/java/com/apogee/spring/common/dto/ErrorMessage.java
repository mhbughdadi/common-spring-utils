package com.apogee.spring.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
public class ErrorMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String message;
    private String messageAr;

    public ErrorMessage(String message, String messageAr) {
        this.message = message;
        this.messageAr = messageAr;
    }

    public ErrorMessage() {
        this.message = "An error occurred";
        this.messageAr = "\\u062d\\u062f\\u062b\\u0020\\u062e\\u0637\\u0623\\u0020\\u0645\\u0627";
    }
}
