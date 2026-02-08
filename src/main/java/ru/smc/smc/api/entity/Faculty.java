package ru.smc.smc.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor()
public class Faculty {
    @Id
    private int id;
    private String nameRu;
    private boolean isActive = true;
    private Instant createTime =  Instant.now();

    @OneToOne(mappedBy = "faculty")
    private SportsOrganizer sportsOrganizer;

    public Faculty(int id, String nameRu) {
        this.id = id;
        this.nameRu = nameRu;
    }

    public void assignSportsOrganizer(SportsOrganizer sportsOrg) {
        sportsOrganizer = sportsOrg;
        sportsOrganizer.setFaculty(this);
    }
}
