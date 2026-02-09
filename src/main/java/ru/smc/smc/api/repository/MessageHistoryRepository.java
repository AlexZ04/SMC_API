package ru.smc.smc.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.smc.smc.api.entity.MessageHistory;

import java.util.UUID;

public interface MessageHistoryRepository extends JpaRepository<MessageHistory, UUID> {
}
