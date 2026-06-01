package ru.smc.smc.api.application.service.sportorg;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.entity.Faculty;
import ru.smc.smc.api.domain.entity.SportsOrganizer;
import ru.smc.smc.api.domain.repository.SportsOrganizerRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SportorgService {

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
}
