package ru.smc.smc.api.application.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smc.smc.api.domain.repository.MessageHistoryRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHistoryScheduler {

    private static final int MESSAGE_HISTORY_STORAGE_DAYS = 7;

    private final MessageHistoryRepository messageHistoryRepository;

    @Scheduled(cron = "0 0 5 * * *")
    @Transactional
    public void clearOldMessageHistory() {
        Instant borderMessageTime = Instant.now().minus(MESSAGE_HISTORY_STORAGE_DAYS, ChronoUnit.DAYS);

        int deletedMessagesAmount = messageHistoryRepository.deleteByMessageTimeBefore(borderMessageTime);

        log.info("Удалено записей истории сообщений старше {}: {}", borderMessageTime, deletedMessagesAmount);
    }
}
