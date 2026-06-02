package ru.smc.smc.api.application.service.distribution;

import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.AdminDistributionType;
import ru.smc.smc.api.application.common.exceptions.BadRequestException;
import ru.smc.smc.api.application.common.exceptions.NotFoundException;
import ru.smc.smc.api.application.common.model.request.MessageFileRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class DistributionFileService {

    private static final int FILES_LIMIT = 10;
    private static final Path DISTRIBUTIONS_FILES_PATH = Path.of("config", "files", "distributions");

    public void replaceFiles(AdminDistributionType distributionType, List<MessageFileRequest> files) {
        Path directory = getDistributionDirectory(distributionType);

        try {
            Files.createDirectories(directory);
            deleteOldFiles(directory);
            saveFiles(directory, files);
        } catch (IOException e) {
            throw new NotFoundException("Error updating distribution files: " + distributionType);
        }
    }

    public List<UUID> findFileIds(AdminDistributionType distributionType) {
        Path directory = getDistributionDirectory(distributionType);

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

            return Objects.requireNonNullElse(mimeType, "application/octet-stream");

        } catch (IOException e) {
            return "application/octet-stream";
        }
    }

    private void saveFiles(Path directory, List<MessageFileRequest> files) throws IOException {
        if (files == null) {
            return;
        }

        for (MessageFileRequest file : files.stream().limit(FILES_LIMIT).toList()) {
            saveFile(directory, file);
        }
    }

    private void saveFile(Path directory, MessageFileRequest file) throws IOException {
        if (file == null) {
            return;
        }

        if (file.getContentBase64() == null || file.getContentBase64().isBlank()) {
            return;
        }

        Path filePath = directory.resolve(UUID.randomUUID() + getFileExtension(file));

        try {
            Files.write(filePath, Base64.getDecoder().decode(file.getContentBase64()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Incorrect file content");
        }
    }

    private void deleteOldFiles(Path directory) throws IOException {
        try (var files = Files.list(directory)) {
            for (Path file : files.filter(Files::isRegularFile).toList()) {
                if (!file.getFileName().toString().equals(".gitkeep")) {
                    Files.delete(file);
                }
            }
        }
    }

    private Path getDistributionDirectory(AdminDistributionType distributionType) {
        return DISTRIBUTIONS_FILES_PATH.resolve(distributionType.name().toLowerCase(Locale.ROOT));
    }

    private String getFileExtension(MessageFileRequest file) {
        String fileName = file.getFileName();

        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf('.'));
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
