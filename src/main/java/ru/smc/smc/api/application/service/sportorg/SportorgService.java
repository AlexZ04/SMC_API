package ru.smc.smc.api.application.service.sportorg;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smc.smc.api.domain.entity.Faculty;
import ru.smc.smc.api.domain.entity.SportsOrganizer;
import ru.smc.smc.api.domain.repository.FacultyRepository;
import ru.smc.smc.api.domain.repository.SportsOrganizerRepository;

import java.time.Instant;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class SportorgService {

    private static final String ALL_SPORTORGS_MESSAGE = "Список спорторгов факультетов:";
    private static final String SPORTORG_NOT_SET_MESSAGE = "не указан";

    private final FacultyRepository facultyRepository;
    private final SportsOrganizerRepository sportsOrganizerRepository;

    public void updateSportorg(Faculty faculty, String name, String socialLink) {
        SportsOrganizer sportsOrganizer = faculty.getSportsOrganizer();

        if (sportsOrganizer == null) {
            sportsOrganizer = new SportsOrganizer();
            faculty.assignSportsOrganizer(sportsOrganizer);
        }

        sportsOrganizer.setName(name);
        sportsOrganizer.setSocialLink(socialLink);
        sportsOrganizer.setUpdateTime(Instant.now());
        sportsOrganizerRepository.save(sportsOrganizer);
    }

    @Transactional(readOnly = true)
    public String getAllSportorgsMessage() {
        StringBuilder message = new StringBuilder(ALL_SPORTORGS_MESSAGE);

        facultyRepository.findAll().stream()
                .filter(faculty -> faculty.getId() != 0)
                .filter(Faculty::isActive)
                .sorted(Comparator.comparing(Faculty::getId))
                .forEach(faculty -> message.append("\n")
                        .append(faculty.getId())
                        .append(" - ")
                        .append(faculty.getNameRu())
                        .append(": ")
                        .append(formSportorgInfo(faculty)));

        return message.toString();
    }

    private String formSportorgInfo(Faculty faculty) {
        SportsOrganizer sportsOrganizer = faculty.getSportsOrganizer();

        if (sportsOrganizer == null) {
            return SPORTORG_NOT_SET_MESSAGE;
        }

        if (sportsOrganizer.getSocialLink() == null || sportsOrganizer.getSocialLink().isBlank()) {
            return sportsOrganizer.getName();
        }

        return sportsOrganizer.getName() + " " + sportsOrganizer.getSocialLink();
    }
}
