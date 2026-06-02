package ru.smc.smc.api.application.service.faculty;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.entity.Faculty;
import ru.smc.smc.api.domain.repository.FacultyRepository;

import java.util.Comparator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private static final String FACULTY_CHOICE_MESSAGE = "Выберете номер факультета:";
    private static final String ALL_FACULTIES_MESSAGE = "Список факультетов:";

    private final FacultyRepository facultyRepository;

    public String getFacultiesChoiceMessage() {
        return formFacultiesListMessage(FACULTY_CHOICE_MESSAGE);
    }

    public String getAllFacultiesMessage() {
        return formFacultiesListMessage(ALL_FACULTIES_MESSAGE);
    }

    public Optional<Faculty> findActiveFacultyById(int facultyId) {
        if (facultyId == 0) {
            return Optional.empty();
        }

        return facultyRepository.findById(facultyId)
                .filter(Faculty::isActive);
    }

    public Optional<Faculty> findActiveFacultyByMessage(String message) {
        Integer facultyId = parseFacultyId(message);

        if (facultyId == null) {
            return Optional.empty();
        }

        return findActiveFacultyById(facultyId);
    }

    private String formFacultiesListMessage(String header) {
        StringBuilder message = new StringBuilder(header);

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

    private Integer parseFacultyId(String message) {
        try {
            return Integer.parseInt(message.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
