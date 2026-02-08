package ru.smc.smc.api.service.factory;

import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.constant.ErrorsMessages;
import ru.smc.smc.api.domain.enums.AvailablePlatform;
import ru.smc.smc.api.domain.exceptions.NotFoundException;
import ru.smc.smc.api.entity.BotUser;
import ru.smc.smc.api.entity.Faculty;
import ru.smc.smc.api.repository.FacultyRepository;

@Service
public class BotUserFactory {

    private FacultyRepository facultyRepository;

    public BotUser createNewUser(AvailablePlatform availablePlatform, String idOnPlatform) {
        Faculty defaultFaculty = facultyRepository.findById(0).orElseThrow(() -> new NotFoundException(ErrorsMessages.FACULTY_NOT_FOUND));

        BotUser newUser = new BotUser();
        newUser.setPlatform(availablePlatform);
        newUser.setIdOnPlatform(idOnPlatform);
        newUser.setFaculty(defaultFaculty);

        return newUser;
    }
}
