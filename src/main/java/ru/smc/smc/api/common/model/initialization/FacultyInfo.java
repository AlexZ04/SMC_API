package ru.smc.smc.api.common.model.initialization;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FacultyInfo {
    private int id;
    private String nameRu;
    private String sportsOrgName;
    private String sportsOrgLink;
}
