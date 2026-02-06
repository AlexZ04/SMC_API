package ru.smc.smc.api.domain.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.smc.smc.api.domain.enums.AvailablePlatform;

import java.util.List;

@Data
@RequiredArgsConstructor
public class PlatformReceivers {
    private AvailablePlatform platform;
    private List<String> receiversId;
}
