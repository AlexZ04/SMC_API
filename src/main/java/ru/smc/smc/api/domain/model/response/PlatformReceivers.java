package ru.smc.smc.api.domain.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.smc.smc.api.domain.enums.AvailablePlatform;

import java.util.List;

@Data
@AllArgsConstructor
public class PlatformReceivers {
    private AvailablePlatform platform;
    private List<String> receiversId;
}
