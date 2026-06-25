package ru.smc.smc.api.application.utility;

import lombok.experimental.UtilityClass;
import ru.smc.smc.api.application.common.exceptions.NotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class FileUtility {
    public String getFileMessage(String fileName) {
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

    public void writeCustomizableFileMessage(String fileName, String message) {
        Path path = Path.of("config", "texts", "customizable", fileName + ".txt");

        try {
            Files.writeString(path, message);
        } catch (IOException e) {
            throw new NotFoundException("Ошибка записи в файл: " + path);
        }
    }

    public String getCustomizableFileMessage(String fileName) {
        Path path = Path.of("config", "texts", "customizable", fileName + ".txt");

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
