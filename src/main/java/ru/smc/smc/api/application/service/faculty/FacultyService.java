package ru.smc.smc.api.application.service.faculty;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.entity.Faculty;
import ru.smc.smc.api.domain.repository.FacultyRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private static final String FACULTY_CHOICE_MESSAGE = "Выберете номер факультета:";
    private static final String ALL_FACULTIES_MESSAGE = "Список факультетов:";

    private final FacultyRepository facultyRepository;
    private final FacultyResolverService facultyResolverService;

    public String getFacultiesChoiceMessage() {
        return formFacultiesListMessage(FACULTY_CHOICE_MESSAGE);
    }

    public String getAllFacultiesMessage() {
        return formFacultiesListMessage(ALL_FACULTIES_MESSAGE);
    }

    public Optional<Faculty> findActiveFacultyById(int facultyId) {
        return facultyResolverService.findActiveFacultyById(facultyId);
    }

    public Optional<Faculty> findActiveFacultyByMessage(String message) {
        return facultyResolverService.findActiveFacultyByMessage(message);
    }

    public Optional<List<Faculty>> findActiveFacultiesByMessage(String message) {
        return facultyResolverService.findActiveFacultiesByMessage(message);
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

}
