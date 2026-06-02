package ru.smc.smc.api.application.service.faculty;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.entity.Faculty;
import ru.smc.smc.api.domain.repository.FacultyRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Optional<List<Faculty>> findActiveFacultiesByMessage(String message) {
        List<Integer> facultyIds = parseFacultyIds(message);

        if (facultyIds.isEmpty()) {
            return Optional.empty();
        }

        List<Faculty> faculties = facultyIds.stream()
                .map(this::findActiveFacultyById)
                .flatMap(Optional::stream)
                .toList();

        if (faculties.size() != facultyIds.size()) {
            return Optional.empty();
        }

        return Optional.of(faculties);
    }

    public String formFacultyIds(List<Faculty> faculties) {
        return faculties.stream()
                .map(faculty -> String.valueOf(faculty.getId()))
                .collect(Collectors.joining(";"));
    }

    public String formFacultyNames(List<Faculty> faculties) {
        return faculties.stream()
                .map(Faculty::getNameRu)
                .collect(Collectors.joining(", "));
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

    private List<Integer> parseFacultyIds(String message) {
        try {
            return Stream.of(message.split(";"))
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .map(Integer::parseInt)
                    .distinct()
                    .toList();
        } catch (NumberFormatException e) {
            return List.of();
        }
    }
}
