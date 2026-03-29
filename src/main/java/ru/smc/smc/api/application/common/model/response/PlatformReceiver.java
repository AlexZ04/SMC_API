package ru.smc.smc.api.application.common.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.smc.smc.api.application.common.enums.AvailablePlatform;

import java.util.List;

@Data
@Accessors(chain = true)
public class PlatformReceiver {
    private AvailablePlatform platform;
    private String receiverId;
    private List<List<ElementModel>> replyElements;
}
