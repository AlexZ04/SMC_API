package ru.smc.smc.api.domain.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "messages")
public class ResponseMessagesProperties {
    private String returnToMainScreen;
    private String forbiddenAccess;
}
