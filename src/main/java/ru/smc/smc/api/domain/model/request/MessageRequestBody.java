package ru.smc.smc.api.domain.model.request;

import lombok.Data;
import ru.smc.smc.api.domain.enums.AvailablePlatform;

@Data
public class MessageRequestBody {
    private String message;
    private AvailablePlatform platform;
    private String userIdOnPlatform;

    // todo: сделать поле для картинок и стикеров
}
