package ru.smc.smc.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.smc.smc.api.domain.entity.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {
}
