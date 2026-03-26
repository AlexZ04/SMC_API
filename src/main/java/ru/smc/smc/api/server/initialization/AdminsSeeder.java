package ru.smc.smc.api.server.initialization;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.smc.smc.api.application.common.enums.UserRole;
import ru.smc.smc.api.application.common.model.initialization.AdminInfo;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.repository.BotUserRepository;
import ru.smc.smc.api.application.service.factory.BotUserFactory;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class AdminsSeeder implements ApplicationRunner {

    private final BotUserRepository botUserRepository;
    private final BotUserFactory botUserFactory;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Path ADMINS_INFO_PATH = Path.of("config/admins-list.json");

    @Override
    public void run(@NonNull ApplicationArguments args) {
        log.info("Начало обработки списка администраторов");

        if (!Files.exists(ADMINS_INFO_PATH)) {
            log.warn("Файл admins-list.json не найден, обновлений в таблице пользователей не будет");
            return;
        }

        List<AdminInfo> admins = mapper.readValue(
                ADMINS_INFO_PATH.toFile(),
                new TypeReference<>() {
                }
        );

        for (AdminInfo admin : admins) {
            updateOrCreateUserRole(admin);
        }

        log.info("Список администраторов обработан");
    }

    private void updateOrCreateUserRole(AdminInfo admin) {
        UserRole targetRole = admin.getRole() == null ? UserRole.ADMIN : admin.getRole();

        Optional<BotUser> existingUserOptional =
                botUserRepository.findBotUserByPlatformAndIdOnPlatform(admin.getPlatform(), admin.getIdOnPlatform());

        if (existingUserOptional.isPresent()) {
            BotUser existing = existingUserOptional.get();
            boolean changed = false;

            if (existing.getRole() != targetRole) {
                existing.setRole(targetRole);
                changed = true;
            }

            if (changed) {
                botUserRepository.save(existing);
                log.info("Пользователь ({}, {}) обновлён до роли {}",
                        admin.getPlatform(), admin.getIdOnPlatform(), targetRole);
            } else {
                log.debug("Пользователь ({}, {}) уже имеет роль {} — пропускаем обновление",
                        admin.getPlatform(), admin.getIdOnPlatform(), targetRole);
            }
            return;
        }

        BotUser newUser = botUserFactory.createNewUser(admin.getPlatform(), admin.getIdOnPlatform());
        newUser.setRole(targetRole);

        botUserRepository.save(newUser);

        log.info("Создан пользователь ({}, {}) с ролью {}",
                admin.getPlatform(), admin.getIdOnPlatform(), targetRole);
    }
}
