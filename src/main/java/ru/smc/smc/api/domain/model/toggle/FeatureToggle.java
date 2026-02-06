package ru.smc.smc.api.domain.model.toggle;

import lombok.Getter;
import lombok.Setter;

@Getter
public class FeatureToggle {
    private String toggleName;
    @Setter
    private boolean active;
    private String description;

    @Override
    public String toString() {

        return "Наименование: " +
                toggleName +
                ";\n" +
                "Состояние: " +
                (active ? "Включен" : "Выключен") +
                ";\n" +
                "Описание: " +
                description +
                ";\n";
    }
}
