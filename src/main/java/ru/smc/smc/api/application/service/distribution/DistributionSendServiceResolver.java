package ru.smc.smc.api.application.service.distribution;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.application.common.enums.AdminDistributionType;
import ru.smc.smc.api.application.common.exceptions.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistributionSendServiceResolver {

    private final List<DistributionSendService> distributionSendServices;
    private Map<AdminDistributionType, DistributionSendService> distributionSendServicesMap = new HashMap<>();

    @PostConstruct
    private void init() {
        distributionSendServicesMap = distributionSendServices.stream()
                .collect(Collectors.toUnmodifiableMap(DistributionSendService::type, Function.identity()));
    }

    public DistributionSendService resolve(AdminDistributionType distributionType) {
        DistributionSendService distributionSendService = distributionSendServicesMap.get(distributionType);

        if (distributionSendService == null) {
            throw new NotFoundException("Не найден сервис рассылки для типа: " + distributionType);
        }

        return distributionSendService;
    }
}
