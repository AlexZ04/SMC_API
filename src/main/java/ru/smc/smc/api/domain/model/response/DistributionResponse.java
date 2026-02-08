package ru.smc.smc.api.domain.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class DistributionResponse extends MessageResponse {
    private final boolean sendToHimself;
    private final List<PlatformReceivers> receivers;

    public DistributionResponse(List<String> previewMessages,
                                String responseText,
                                List<List<ElementModel>> inlineElements,
                                List<List<ElementModel>> replyElements, boolean sendToHimself,
                                List<PlatformReceivers> receivers) {
        super(previewMessages, responseText, inlineElements, replyElements);

        this.sendToHimself = sendToHimself;
        this.receivers = receivers;
    }
}
