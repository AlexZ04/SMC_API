package ru.smc.smc.api.service;

import com.sun.tools.javac.Main;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.constant.ErrorsMessages;
import ru.smc.smc.api.domain.exceptions.BadRequestException;
import ru.smc.smc.api.domain.model.toggle.FeatureToggle;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class FeatureToggleService {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Path TOGGLES_PATH =
            Path.of("config/feature-toggles.json");

    public String getSystemTogglesInfo() {
        List<FeatureToggle> toggles = getAllToggles();

        return parseToggleListToText(toggles);
    }

    public boolean isToggleActive(String toggleName) {
        return getSpecificToggle(toggleName).isActive();
    }

    public void changeActiveToggleStatus(String toggleName, boolean activate) {
        FeatureToggle toggle = getSpecificToggle(toggleName);

        if (toggle.isActive() != activate) {
            toggle.setActive(activate);
        }
        else {
            throw new BadRequestException(ErrorsMessages.TOGGLE_FUNCTIONAL +
                    (activate ? "включена: " : "выключена: ") +
                    toggle.getToggleName());
        }
    }

    private List<FeatureToggle> getAllToggles() {
        List<FeatureToggle> toggles;

        if (Files.exists(TOGGLES_PATH)) {
            toggles = mapper.readValue(TOGGLES_PATH.toFile(),
                    new TypeReference<>() {
                    });
        } else {
            InputStream inputStream = Main.class
                    .getClassLoader()
                    .getResourceAsStream("feature-toggles.json");

            toggles = mapper.readValue(
                    inputStream,
                    new TypeReference<>() {
                    }
            );
        }

        return toggles;
    }

    private FeatureToggle getSpecificToggle(String toggleName) {
        return getAllToggles().stream()
                .filter(toggle -> toggle.getToggleName().equalsIgnoreCase(toggleName))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Тоггл " + toggleName + " не найден"));
    }

    private String parseToggleListToText(List<FeatureToggle> toggles) {
        StringBuilder togglesDescription = new StringBuilder("Существующие переключатели функционала: \n");

        for (int i = 0; i < toggles.size(); i++) {
            togglesDescription.append("Тоггл номер ")
                    .append(i + 1)
                    .append("\n");
            togglesDescription.append(toggles.get(i).toString()).append("\n");
            togglesDescription.append("---------\n");
        }

        return togglesDescription.toString();
    }
}
