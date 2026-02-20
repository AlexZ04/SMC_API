package ru.smc.smc.api.server.initialization;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.smc.smc.api.common.model.initialization.FacultyInfo;
import ru.smc.smc.api.entity.Faculty;
import ru.smc.smc.api.entity.SportsOrganizer;
import ru.smc.smc.api.repository.FacultyRepository;
import ru.smc.smc.api.repository.SportsOrganizerRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class FacultiesSeeder implements ApplicationRunner {

    private final FacultyRepository facultyRepository;
    private final SportsOrganizerRepository sportsOrganizerRepository;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Path FACULTIES_INFO_PATH =
            Path.of("config/faculties.json");

    @Override
    public void run(@NonNull ApplicationArguments args) {
        log.info("Начало обработки данных о факультетах");

        List<FacultyInfo> facultyInfoList;

        if (!Files.exists(FACULTIES_INFO_PATH)) {
            log.warn("Файл конфигурации факультетов не найден, обновлений в таблице не будет!");
            return;
        }

        facultyInfoList = mapper.readValue(FACULTIES_INFO_PATH.toFile(),
                new TypeReference<>() {
                });

        for (FacultyInfo facultyInfo : facultyInfoList) {
            Optional<Faculty> existingFacultyOptional = facultyRepository.findById(facultyInfo.getId());

            if (existingFacultyOptional.isPresent()) {
                Faculty existingFaculty = existingFacultyOptional.get();

                updateFacultyInfoIfNecessary(facultyInfo, existingFaculty);
            } else {
                log.info("Факультет id={}, ({}) не найден в базе данных. Происходит обогащение таблицы данными",
                        facultyInfo.getId(), facultyInfo.getNameRu());

                addFaculty(facultyInfo);
            }
        }

        log.info("Данные о факультетах обработаны");
    }

    private void updateFacultyInfoIfNecessary(FacultyInfo facultyInfo, Faculty existingFaculty) {
        boolean facultyChanged = !existingFaculty.isActive() ||
                !existingFaculty.getNameRu().equals(facultyInfo.getNameRu());

        SportsOrganizer organizer = existingFaculty.getSportsOrganizer();

        boolean organizerChanged = false;

        if (organizer == null) {
            organizer = new SportsOrganizer();
            organizer.setFaculty(existingFaculty);
            existingFaculty.setSportsOrganizer(organizer);
            organizerChanged = true;
        }

        if (!organizer.getName().equals(facultyInfo.getSportsOrgName())) {
            organizer.setName(facultyInfo.getSportsOrgName());
            organizerChanged = true;
        }

        if (!organizer.getSocialLink().equals(facultyInfo.getSportsOrgLink())) {
            organizer.setSocialLink(facultyInfo.getSportsOrgLink());
            organizerChanged = true;
        }

        if (facultyChanged) {
            existingFaculty.setActive(true);
            existingFaculty.setNameRu(facultyInfo.getNameRu());
        }

        if (facultyChanged || organizerChanged) {
            organizer.setUpdateTime(Instant.now());
            sportsOrganizerRepository.save(organizer);

            log.info("Информация о факультете id={}, ({}) обновлена",
                    facultyInfo.getId(), facultyInfo.getNameRu());
        }
    }

    private void addFaculty(FacultyInfo facultyInfo) {
        Faculty newFaculty = new Faculty(facultyInfo.getId(), facultyInfo.getNameRu());
        SportsOrganizer newSportOrganizerInfo = new SportsOrganizer(facultyInfo.getSportsOrgName(), facultyInfo.getSportsOrgLink());

        newFaculty.assignSportsOrganizer(newSportOrganizerInfo);

        sportsOrganizerRepository.save(newSportOrganizerInfo);

        log.info("Добавлена информация о факультете id={}, ({})", facultyInfo.getId(), facultyInfo.getNameRu());
    }
}
