package ru.smc.smc.api.domain.model;

import lombok.Getter;

import java.util.List;

@Getter
public class DistributionResponse extends MessageResponse {
    private final boolean sendToHimself;
    private final List<PlatformReceivers> receivers;

    public DistributionResponse(String responseText,
                                List<List<ElementModel>> inlineElements,
                                List<List<ElementModel>> replyElements,
                                boolean sendToHimself,
                                List<PlatformReceivers> receivers) {
        super(responseText, inlineElements, replyElements);
        this.sendToHimself = sendToHimself;
        this.receivers = receivers;
    }
}
