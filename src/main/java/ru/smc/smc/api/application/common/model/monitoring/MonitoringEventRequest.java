package ru.smc.smc.api.application.common.model.monitoring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringEventRequest {
    private String level;
    private String channel;
    private TriggeredUser triggeredBy;
    private String message;
    private String time;
}
