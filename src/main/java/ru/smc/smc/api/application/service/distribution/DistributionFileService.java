package ru.smc.smc.api.application.service.distribution;

import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.AdminDistributionType;
import ru.smc.smc.api.application.common.exceptions.NotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class DistributionFileService {

    private static final int FILES_LIMIT = 10;
    private static final Path DISTRIBUTIONS_FILES_PATH = Path.of("config", "files", "distributions");

    public List<UUID> findFileIds(AdminDistributionType distributionType) {
        Path directory = DISTRIBUTIONS_FILES_PATH.resolve(distributionType.name().toLowerCase(Locale.ROOT));

        if (!Files.exists(directory)) {
            return List.of();
        }

        try (var files = Files.list(directory)) {
            return files
                .filter(Files::isRegularFile)
                .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                .map(this::getFileId)
                .flatMap(Optional::stream)
                .limit(FILES_LIMIT)
                .toList();
        } catch (IOException e) {
            throw new NotFoundException("Error reading distribution files: " + directory);
        }
    }

    public Path findFile(UUID fileId) {
        if (!Files.exists(DISTRIBUTIONS_FILES_PATH)) {
            throw new NotFoundException("File not found: " + fileId);
        }

        try (var files = Files.walk(DISTRIBUTIONS_FILES_PATH)) {
            return files
                .filter(Files::isRegularFile)
                .filter(path -> getFileId(path).map(fileId::equals).orElse(false))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("File not found: " + fileId));
        } catch (IOException e) {
            throw new NotFoundException("Error reading distribution file: " + fileId);
        }
    }

    public String getMimeType(Path path) {
        try {
            String mimeType = Files.probeContentType(path);

            if (mimeType == null) {
                return "application/octet-stream";
            }

            return mimeType;
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }

    private Optional<UUID> getFileId(Path path) {
        String fileName = path.getFileName().toString();
        int extensionStartIndex = fileName.lastIndexOf('.');
        String fileId = extensionStartIndex > 0 ? fileName.substring(0, extensionStartIndex) : fileName;

        try {
            return Optional.of(UUID.fromString(fileId));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
