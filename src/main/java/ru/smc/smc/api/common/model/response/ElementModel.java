package ru.smc.smc.api.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.smc.smc.api.common.enums.ElementType;

@Data
@AllArgsConstructor
public class ElementModel {
    private ElementType type;
    private String link; // не null, если ElementType == LINK
    private String text;
    private String color;
    private String textColor;
}
