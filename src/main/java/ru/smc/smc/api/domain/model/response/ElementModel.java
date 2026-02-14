package ru.smc.smc.api.domain.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.smc.smc.api.domain.enums.ElementType;

@Data
@AllArgsConstructor
public class ElementModel {
    private ElementType type;
    private String link; // не null, если ElementType == LINK
    private String text;
    private String color;
    private String textColor;
}
