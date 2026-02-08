package ru.smc.smc.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.smc.smc.api.entity.SportsOrganizer;

public interface SportsOrganizerRepository extends JpaRepository<SportsOrganizer, Long> {
}
