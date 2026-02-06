package ru.smc.smc.api.service;

import com.sun.tools.javac.Main;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.model.toggle.FeatureToggle;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class FeatureToggleService {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Path TOGGLES_PATH =
            Path.of("config/feature-toggles.json");

    public String getSystemTogglesInfo() {
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

        return parseToggleListToText(toggles);
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
