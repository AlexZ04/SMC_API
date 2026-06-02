package ru.smc.smc.api.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.smc.smc.api.application.common.constant.ErrorsMessages;
import ru.smc.smc.api.application.common.exceptions.NotFoundException;
import ru.smc.smc.api.application.common.exceptions.UnauthorizedException;
import ru.smc.smc.api.application.service.distribution.DistributionFileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FileController {

    private final DistributionFileService distributionFileService;

    @Value("${api-config.key}")
    private String validApiKey;

    @GetMapping("/files/{fileId}")
    public ResponseEntity<ByteArrayResource> getFile(@PathVariable UUID fileId, @RequestHeader("api-key") String apiKey) {
        validateApiKey(apiKey);

        Path file = distributionFileService.findFile(fileId);

        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(distributionFileService.getMimeType(file)))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(new ByteArrayResource(Files.readAllBytes(file)));
        } catch (IOException e) {
            throw new NotFoundException("Error reading distribution file: " + fileId);
        }
    }

    private void validateApiKey(String apiKey) {
        if (!apiKey.equals(validApiKey)) {
            throw new UnauthorizedException(ErrorsMessages.INVALID_API_KEY);
        }
    }
}
