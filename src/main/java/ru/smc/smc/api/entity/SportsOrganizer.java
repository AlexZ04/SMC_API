package ru.smc.smc.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SportsOrganizer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "faculty_id", nullable = false, unique = true)
    private Faculty faculty;

    @Column(nullable = false)
    private String name;

    @Column(name = "social_link")
    private String socialLink;

    private Instant updateTime = Instant.now();

    public SportsOrganizer(String name, String socialLink) {
        this.name = name;
        this.socialLink = socialLink;
    }
}

