package ru.smc.smc.api.application.service.faculty;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.entity.Faculty;
import ru.smc.smc.api.domain.repository.FacultyRepository;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private static final String FACULTY_CHOICE_MESSAGE = "Выберете номер факультета:";

    private final FacultyRepository facultyRepository;

    public String getFacultiesChoiceMessage() {
        StringBuilder message = new StringBuilder(FACULTY_CHOICE_MESSAGE);

        facultyRepository.findAll().stream()
                .filter(faculty -> faculty.getId() != 0)
                .filter(Faculty::isActive)
                .sorted(Comparator.comparing(Faculty::getId))
                .forEach(faculty -> message.append("\n")
                        .append(faculty.getId())
                        .append(" - ")
                        .append(faculty.getNameRu()));

        return message.toString();
    }
}
