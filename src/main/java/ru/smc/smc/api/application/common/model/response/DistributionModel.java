package ru.smc.smc.api.application.common.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class DistributionModel {

    private String distributionText;
    private List<UUID> distributionFiles = new ArrayList<>();
    private boolean sendToHimself;
    private List<PlatformReceiver> receivers = new ArrayList<>();
    private List<List<ElementModel>> distributionInlineElements = new ArrayList<>();
}
