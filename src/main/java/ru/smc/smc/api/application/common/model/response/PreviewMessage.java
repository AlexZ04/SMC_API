package ru.smc.smc.api.application.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreviewMessage {
    private String responseText;
    private List<List<ElementModel>> inlineElements;
}
