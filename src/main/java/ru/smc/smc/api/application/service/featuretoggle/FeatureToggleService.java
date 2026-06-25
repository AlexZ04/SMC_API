package ru.smc.smc.api.application.service.featuretoggle;

import com.sun.tools.javac.Main;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.constant.ErrorsMessages;
import ru.smc.smc.api.application.common.exceptions.BadRequestException;
import ru.smc.smc.api.application.common.model.toggle.FeatureToggle;
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
    private static final Path TOGGLES_PATH = Path.of("config/feature-toggles.json");

    public String getSystemTogglesInfo() {
        List<FeatureToggle> toggles = getAllToggles();

        return parseToggleListToText(toggles);
    }

    public boolean isToggleActive(String toggleName) {
        return getSpecificToggle(getAllToggles(), toggleName).isActive();
    }

    public Optional<Boolean> changeToggleStatusToOppositeIfExists(String toggleName) {
        List<FeatureToggle> allToggles = getAllToggles();
        Optional<FeatureToggle> toggleOptional = findSpecificToggle(allToggles, toggleName);

        if (toggleOptional.isEmpty()) {
            return Optional.empty();
        }

        FeatureToggle toggle = toggleOptional.get();
        toggle.setActive(!toggle.isActive());

        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(TOGGLES_PATH.toFile(), allToggles);

        return Optional.of(toggle.isActive());
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

    private FeatureToggle getSpecificToggle(List<FeatureToggle> featureToggles, String toggleName) {
        return findSpecificToggle(featureToggles, toggleName)
                .orElseThrow(() -> new BadRequestException("Тоггл " + toggleName + " не найден"));
    }

    private Optional<FeatureToggle> findSpecificToggle(List<FeatureToggle> featureToggles, String toggleName) {
        return featureToggles.stream()
                .filter(toggle -> toggle.getToggleName().equalsIgnoreCase(toggleName))
                .findFirst();
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
