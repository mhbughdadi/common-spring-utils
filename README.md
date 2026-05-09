# common-spring-utils

Common utilities library for Spring Boot microservices: global exception handler, AOP logging, exceptions, responses, and error responses.

## Features

- **Global Exception Handler**: Centralized exception handling for REST APIs
- **AOP Logging**: Automatic logging of HTTP requests and responses
- **Custom Exceptions**: Business-specific exception types
- **Response DTOs**: Standardized response formats for API consistency
- **Auto-Configuration**: Automatic setup when added as a dependency to Spring Boot applications

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.apogee.spring.common</groupId>
    <artifactId>spring-common</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Auto-Configuration

This package includes Spring Boot auto-configuration that automatically registers the following components:

### 1. **LoggerAspect** - AOP Logging
- Automatically logs all HTTP requests and responses
- Captures request details: URL, method, headers, query parameters, request body
- Captures response details: status, duration, response body
- Supports MDC (Mapped Diagnostic Context) for request tracking
- Uses Log4j2 for structured logging

**Pointcut**: `within(com.apogee.*.controllers..*)`

### 2. **GlobalExceptionHandler** - Exception Handling
- Centralized exception handling for `RecordNotFoundException`
- Centralized exception handling for `DBException`
- Centralized exception handling for `MapperException`
- Returns standardized error responses with internationalization (English & Arabic support)
- Logs all exceptions with context information

## Usage

### Default Auto-Configuration

Simply add this library as a dependency to your Spring Boot application. The auto-configuration will automatically register:

1. `LoggerAspect` - for AOP logging
2. `GlobalExceptionHandler` - for centralized exception handling

```xml
<dependency>
    <groupId>com.apogee.spring.common</groupId>
    <artifactId>spring-common</artifactId>
    <version>0.0.1</version>
</dependency>
```

Both components will be automatically registered as Spring beans and ready to use.

### Custom Configuration (Optional)

If you want to override the default configuration, you can define your own beans in your application:

```java
@Configuration
public class MyCustomConfig {
    
    @Bean
    public LoggerAspect customLoggerAspect() {
        // Custom LoggerAspect implementation
        return new LoggerAspect();
    }
    
    @Bean
    public GlobalExceptionHandler customExceptionHandler() {
        // Custom exception handler
        return new GlobalExceptionHandler();
    }
}
```

The auto-configuration uses `@ConditionalOnMissingBean`, so your custom beans will take precedence.

## Using Custom Exceptions

### RecordNotFoundException

Thrown when a database record is not found:

```java
throw new RecordNotFoundException("user.not.found", userId);
// or for multiple IDs:
throw new RecordNotFoundException("users.not.found", id1, id2, id3);
```

### DBException

Thrown for database-related errors:

```java
throw new DBException("Database operation failed", ids);
```

### BusinessException

Thrown for business logic violations:

```java
throw new BusinessException(
    "message", 
    "errorCode", 
    "details", 
    "debugInfo", 
    cause
);
```

## Response Format

All responses follow a standardized format:

### Success Response
```json
{
    "success": true,
    "data": { /* response data */ },
    "message": "Operation successful"
}
```

### Error Response
```json
{
    "success": false,
    "errors": [
        {
            "message": "Resource not found",
            "arabicMessage": "لم يتم العثور على المورد"
        }
    ]
}
```

## Dependencies

### Required (Provided)
- Spring Framework 6.1.21
- Spring Boot Autoconfigure 3.2.5
- AspectJ 1.9.22.1
- Log4j 2.25.4
- SLF4J 2.0.9
- Jackson 2.17.2
- Jakarta Servlet API 6.0.0
- Lombok 1.18.30

### Test Dependencies
- JUnit Jupiter 5.10.0
- Mockito 5.7.0

### Note
All dependencies are marked as `provided` scope, allowing client applications to specify their own versions.

## Configuration Properties

The library respects the following Spring Boot properties for MDC:

```properties
# Used for request tracking
logging.mdc.request-id=correlation-id
```

## Logging Configuration

The package uses Log4j 2 with JSON template layout for structured logging. Configure Log4j in your application's `log4j2.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <JsonTemplateLayout eventTemplateUri="classpath:LogstashJsonEventLayoutTemplate.json"/>
        </Console>
    </Appenders>
    <Loggers>
        <Logger name="com.apogee.spring.common" level="info"/>
        <Root level="info">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

## Security Considerations

- All dependencies are kept up-to-date with the latest security patches
- Spring Framework 6.1.21 with critical CVE fixes
- Log4j 2.25.4 with TLS hostname verification fixes
- No known CVEs in current versions

## Version Notes

- **Java**: Compiled for Java 21
- **Spring Framework**: 6.1.21 (compatible with Spring Boot 3.2.x)
- **Spring Boot Autoconfigure**: 3.2.5

## Contributing

When extending this library:

1. Ensure all new dependencies use the `provided` scope
2. Add version properties for all new dependencies
3. Update the `spring.factories` file if adding new auto-configuration classes
4. Include comprehensive JavaDoc comments
5. Test with Spring Boot 3.2.5+

## License

All rights reserved.
