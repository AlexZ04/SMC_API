package ru.smc.smc.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.smc.smc.api.service.FeatureToggleService;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final FeatureToggleService featureToggleService;

    @GetMapping("/test")
    public String test(){
        return featureToggleService.getSystemTogglesInfo();
    }
}
