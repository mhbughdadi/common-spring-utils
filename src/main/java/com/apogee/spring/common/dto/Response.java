package com.apogee.spring.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.MDC;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import static com.apogee.spring.common.constant.CommonConstant.REQUEST_ID;

@Getter
@Setter
public class Response implements Serializable {

    @Serial
    private static final long serialVersionUID = 100L;

    private int code;
    private ErrorMessage error;
    private String timeStamp ;
    private Status status;
    private String requestId = MDC.get(REQUEST_ID);

    public Response(int code, Status status) {
        this.code = code;
        this.status = status;
        this.timeStamp = Instant.now().toString();
    }
}
