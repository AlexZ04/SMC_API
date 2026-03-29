package ru.smc.smc.api.application.common.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DistributionModel {

    private String distributionText;
    private boolean sendToHimself;
    private List<PlatformReceiver> receivers = new ArrayList<>();
    private List<List<ElementModel>> distributionInlineElements = new ArrayList<>();
}
