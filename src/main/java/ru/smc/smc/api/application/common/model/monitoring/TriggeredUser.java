package ru.smc.smc.api.application.common.model.monitoring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriggeredUser {
    private String name;
    private String link;
}
