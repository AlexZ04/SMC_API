package ru.smc.smc.api.application.service.faculty;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.entity.Faculty;
import ru.smc.smc.api.domain.repository.FacultyRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FacultyResolverService {

    private static final LevenshteinDistance LEVENSHTEIN_DISTANCE = new LevenshteinDistance(1);

    private final FacultyRepository facultyRepository;

    public Optional<Faculty> findActiveFacultyByMessage(String message) {
        return findActiveFacultyByToken(message);
    }

    public Optional<List<Faculty>> findActiveFacultiesByMessage(String message) {
        List<Faculty> faculties = parseFaculties(message);

        if (faculties.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(faculties);
    }

    public Optional<Faculty> findActiveFacultyById(int facultyId) {
        if (facultyId == 0) {
            return Optional.empty();
        }

        return facultyRepository.findById(facultyId)
                .filter(Faculty::isActive);
    }

    private List<Faculty> parseFaculties(String message) {
        List<String> facultyTokens = Stream.of(message.split(";"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();

        List<Optional<Faculty>> facultyOptions = facultyTokens.stream()
                .map(this::findActiveFacultyByToken)
                .toList();

        if (facultyOptions.stream().anyMatch(Optional::isEmpty)) {
            return List.of();
        }

        return facultyOptions.stream()
                .map(Optional::get)
                .collect(Collectors.toMap(Faculty::getId, faculty -> faculty, (first, second) -> first))
                .values()
                .stream()
                .sorted(Comparator.comparing(Faculty::getId))
                .toList();
    }

    private Optional<Faculty> findActiveFacultyByToken(String message) {
        Integer facultyId = parseFacultyId(message);

        if (facultyId != null) {
            return findActiveFacultyById(facultyId);
        }

        return findActiveFacultyByName(message);
    }

    private Integer parseFacultyId(String message) {
        try {
            return Integer.parseInt(message.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Optional<Faculty> findActiveFacultyByName(String message) {
        List<Faculty> activeFaculties = findActiveFaculties();
        String normalizedMessage = normalizeFacultyName(message);

        Optional<Faculty> exactFaculty = activeFaculties.stream()
                .filter(faculty -> normalizeFacultyName(faculty.getNameRu()).equals(normalizedMessage))
                .findFirst();

        if (exactFaculty.isPresent()) {
            return exactFaculty;
        }

        List<Faculty> facultiesWithOneMistake = activeFaculties.stream()
                .filter(faculty -> LEVENSHTEIN_DISTANCE.apply(normalizeFacultyName(faculty.getNameRu()), normalizedMessage) != -1)
                .toList();

        return facultiesWithOneMistake.size() == 1 ? Optional.of(facultiesWithOneMistake.get(0)) : Optional.empty();
    }

    private List<Faculty> findActiveFaculties() {
        return facultyRepository.findAll().stream()
                .filter(faculty -> faculty.getId() != 0)
                .filter(Faculty::isActive)
                .sorted(Comparator.comparing(Faculty::getId))
                .toList();
    }

    private String normalizeFacultyName(String facultyName) {
        return facultyName.trim()
                .toLowerCase(Locale.ROOT)
                .replace("ё", "е")
                .replace("«", "")
                .replace("»", "")
                .replace("\"", "")
                .replaceAll("\\s+", " ");
    }
}
