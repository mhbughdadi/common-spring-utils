package com.apogee.spring.common.aspect;


import com.apogee.spring.common.constant.CommonConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.StringMapMessage;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;

import static com.apogee.spring.common.aspect.AOPUtilities.formatAsJsonObject;
import static com.apogee.spring.common.aspect.AOPUtilities.getHeaders;
import static com.apogee.spring.common.aspect.AOPUtilities.getPathVariables;
import static com.apogee.spring.common.aspect.AOPUtilities.getQueryParams;
import static com.apogee.spring.common.constant.CommonConstant.NULL_STRING;
import static com.apogee.spring.common.constant.CommonConstant.REQUEST_ID;

/**
 * AOP aspect for centralized HTTP request and response logging.
 * <p>
 * This aspect intercepts all method calls within the configured packages and logs:
 * <ul>
 *   <li>Request metadata: URL, HTTP method, headers, query parameters, path variables</li>
 *   <li>Request body for POST/PUT requests</li>
 *   <li>Response body and status code</li>
 *   <li>Execution duration in milliseconds</li>
 *   <li>Request ID from MDC for correlation tracking</li>
 * </ul>
 * </p>
 * <p>
 * All logging is done through Log4j2 with structured logging support for JSON output.
 * </p>
 * <p>
 * Pointcut: Matches all methods within controller packages: {@code within(com.apogee.*.controllers..*)}</p>
 *
 * @see org.aspectj.lang.annotation.Aspect
 * @see org.aspectj.lang.ProceedingJoinPoint
 * @see org.slf4j.MDC
 */
@Log4j2
@Aspect
public class LoggerAspect {

    /**
     * Intercepts HTTP requests and responses for logging.
     * <p>
     * Captures incoming request details, delegates to the target method,
     * logs the response, and handles any exceptions that occur during execution.
     * </p>
     *
     * @param joinPoint the join point representing the intercepted method call
     * @return the method's return value
     * @throws Throwable if the target method throws an exception
     */
    @Around("within(com.apogee.*.controllers...*)")
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder
                        .currentRequestAttributes())
                        .getRequest();

        String requestId = MDC.get(REQUEST_ID);
        String requestBody = getRequestBody(joinPoint);

        StringMapMessage message = new StringMapMessage()
                .with(CommonConstant.URL, request.getRequestURL().toString())
                .with(CommonConstant.HTTP_METHOD, request.getMethod())
                .with(CommonConstant.QUERY_PARAMS, formatAsJsonObject(getQueryParams(request)))
                .with(CommonConstant.REQUEST_BODY, requestBody != null ? requestBody : NULL_STRING)
                .with(CommonConstant.TIMESTAMP, Instant.now().toString())
                .with(CommonConstant.PATH_VARIABLES, getPathVariables(request))
                .with(CommonConstant.HEADERS, formatAsJsonObject(getHeaders(request)))
                .with(REQUEST_ID, requestId != null ? requestId : NULL_STRING);

        Object response = null;
        try {

            response = joinPoint.proceed();

            message
                    .with(CommonConstant.STATUS, String.valueOf(HttpStatus.OK.value()))
                    .with(CommonConstant.DURATION_MS, String.valueOf(System.currentTimeMillis() - start))
                    .with(CommonConstant.RESPONSE_BODY,
                            response != null ? formatAsJsonObject(response) : NULL_STRING);

            log.info(message);

            return response;

        } catch (Throwable ex) {

            message
                    .with(CommonConstant.STATUS, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .with(CommonConstant.DURATION_MS, String.valueOf(System.currentTimeMillis() - start))
                    .with(CommonConstant.EXCEPTION_MESSAGE,
                            ex.getMessage() != null ? ex.getMessage() : NULL_STRING);

            log.error(message, ex);
            throw ex;
        }
    }

    /**
     * Extracts the request body from method parameters if annotated with {@code @RequestBody}.
     * <p>
     * Performs null-safety checks and returns the JSON-serialized body or null if not found.
     * </p>
     *
     * @param joinPoint the join point containing method parameters and annotations
     * @return the request body as JSON string, or null if not found or if joinPoint is invalid
     */
    private String getRequestBody(JoinPoint joinPoint) {
        if (joinPoint == null) {
            return null;
        }

        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return null;
        }

        Annotation[][] annotations = getParameterizedAnnotations(joinPoint);
        for (int i = 0; i < args.length; i++) {
            if (i < annotations.length) {
                for (Annotation annotation : annotations[i]) {
                    if (annotation instanceof RequestBody) {
                        return formatAsJsonObject(args[i]);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Extracts parameter annotations from the intercepted method.
     * <p>
     * Uses reflection to obtain the method signature and its parameter annotations.
     * </p>
     *
     * @param joinPoint the join point representing the intercepted method
     * @return a 2D array of annotations where each row corresponds to a method parameter
     */
    private Annotation[][] getParameterizedAnnotations(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        return method.getParameterAnnotations();
    }
}
