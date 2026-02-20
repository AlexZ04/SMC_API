package ru.smc.smc.api.service.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.common.constant.ErrorsMessages;
import ru.smc.smc.api.common.enums.AvailablePlatform;
import ru.smc.smc.api.common.exceptions.NotFoundException;
import ru.smc.smc.api.entity.BotUser;
import ru.smc.smc.api.entity.Faculty;
import ru.smc.smc.api.repository.FacultyRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotUserFactory {

    private final FacultyRepository facultyRepository;

    public BotUser createNewUser(AvailablePlatform availablePlatform, String idOnPlatform) {
        Faculty defaultFaculty = facultyRepository.findById(0).orElseThrow(() -> new NotFoundException(ErrorsMessages.FACULTY_NOT_FOUND));

        BotUser newUser = new BotUser();
        newUser.setPlatform(availablePlatform);
        newUser.setIdOnPlatform(idOnPlatform);
        newUser.setFaculty(defaultFaculty);

        log.info("Создан новый пользователь бота с {}. Id: {}", newUser.getPlatform(), newUser.getIdOnPlatform());

        return newUser;
    }
}
