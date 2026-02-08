package ru.smc.smc.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.smc.smc.api.entity.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {
}
