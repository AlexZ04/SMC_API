package ru.smc.smc.api.domain.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private List<String> previewMessages;
    private String responseText;
    private List<List<ElementModel>> inlineElements;
    private List<List<ElementModel>> replyElements;
}
