package ru.smc.smc.api.application.common.model.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class DistributionResponse extends MessageResponse {

    private String messageText;
    private boolean sendToHimself;
    private List<PlatformReceiver> receivers;
    private List<List<ElementModel>> inlineElements;
}
