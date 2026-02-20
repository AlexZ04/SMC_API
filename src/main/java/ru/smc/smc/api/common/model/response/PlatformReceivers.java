package ru.smc.smc.api.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.smc.smc.api.common.enums.AvailablePlatform;

import java.util.List;

@Data
@AllArgsConstructor
public class PlatformReceivers {
    private AvailablePlatform platform;
    private List<String> receiversId;
}
