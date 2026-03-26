package ru.smc.smc.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.smc.smc.api.domain.entity.MessageHistory;

import java.util.UUID;

public interface MessageHistoryRepository extends JpaRepository<MessageHistory, UUID> {
}
