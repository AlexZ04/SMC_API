package ru.smc.smc.api.domain.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.smc.smc.api.domain.enums.ElementType;

@Data
@AllArgsConstructor
public class ElementModel {
    private ElementType type;
    private String text;
    private String color; // todo: заменить строковое значение на таблицу цветов
    private String textColor; // todo: заменить строковое значение на таблицу цветов
}
