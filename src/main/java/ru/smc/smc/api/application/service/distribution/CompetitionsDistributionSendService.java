package ru.smc.smc.api.application.service.distribution;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.AdminDistributionType;
import ru.smc.smc.api.application.common.model.response.PlatformReceiver;
import ru.smc.smc.api.application.service.user.UserService;
import ru.smc.smc.api.application.utilities.FileUtility;
import ru.smc.smc.api.domain.entity.BotUser;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionsDistributionSendService implements DistributionSendService {

    private final UserService userService;

    @Override
    public AdminDistributionType type() {
        return AdminDistributionType.COMPETITIONS;
    }

    @Override
    public String getDistributionText() {
        return FileUtility.getCustomizableFileMessage(type().getPathFile());
    }

    @Override
    public List<PlatformReceiver> getReceivers(BotUser user) {
        return userService.findCompetitionSubscribersByFacultyIds(parseFacultyIds(user.getSelectedDistributionFacultyIds()));
    }

    private List<Integer> parseFacultyIds(String facultyIds) {
        return Arrays.stream(facultyIds.split(";"))
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
    }
}
