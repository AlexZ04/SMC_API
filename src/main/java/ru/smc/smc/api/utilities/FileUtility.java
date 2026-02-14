package ru.smc.smc.api.utilities;

import lombok.experimental.UtilityClass;
import ru.smc.smc.api.domain.exceptions.NotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class FileUtility {
    public String getFileMessage(String fileName) throws Exception {
        Path path = Path.of("config", "texts", fileName + ".txt");

        if (!Files.exists(path)) {
            throw new NotFoundException("Файл не найден: " + path);
        }

        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new NotFoundException("Ошибка чтения файла: " + path);
        }
    }
}
