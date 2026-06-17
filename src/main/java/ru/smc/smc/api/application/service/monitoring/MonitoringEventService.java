package ru.smc.smc.api.application.service.monitoring;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.client.RestClient;
import ru.smc.smc.api.application.common.model.monitoring.MonitoringEventRequest;
import ru.smc.smc.api.application.common.model.monitoring.TriggeredUser;
import ru.smc.smc.api.domain.entity.BotUser;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MonitoringEventService {

    private static final String EVENTS_PATH = "/api/v1/events";
    private static final String API_KEY_HEADER = "api-key";
    private static final String INFO_LEVEL = "INFO";
    private static final String WARN_LEVEL = "WARN";
    private static final String ERROR_LEVEL = "ERROR";
    private static final int MONITORING_QUEUE_SIZE = 100;
    private static final DateTimeFormatter HUMAN_READABLE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(MONITORING_QUEUE_SIZE), new ThreadPoolExecutor.DiscardPolicy());

    @Value("${monitoring.enabled:false}")
    private boolean enabled;

    @Value("${monitoring.base-url:http://localhost:8080/smc-monitoring}")
    private String baseUrl;

    @Value("${monitoring.api-key:test-api-key}")
    private String apiKey;

    @Value("${monitoring.channel:smc-api}")
    private String channel;

    @Value("${monitoring.time-zone:Europe/Samara}")
    private String timeZone;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(1));
        requestFactory.setReadTimeout(Duration.ofSeconds(2));

        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdownNow();
    }

    public void sendInfo(BotUser triggeredBy, String message) {
        sendEvent(INFO_LEVEL, triggeredBy, message);
    }

    public void sendDistributionSentInfo(BotUser triggeredBy, String message) {
        sendEvent(INFO_LEVEL, triggeredBy, message);
    }

    public void sendWarn(BotUser triggeredBy, String message) {
        sendEvent(WARN_LEVEL, triggeredBy, message);
    }

    public void sendError(Exception exception) {
        sendEvent(ERROR_LEVEL, null, formExceptionMessage(exception));
    }

    private void sendEvent(String level, BotUser triggeredBy, String message) {
        if (!enabled) {
            return;
        }

        MonitoringEventRequest request = new MonitoringEventRequest(level, formChannel(triggeredBy), formTriggeredUser(triggeredBy),
                message, formCurrentTime());

        executorService.execute(() -> sendEvent(request));
    }

    private void sendEvent(MonitoringEventRequest request) {
        try {
            restClient.post()
                    .uri(EVENTS_PATH)
                    .header(API_KEY_HEADER, apiKey)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception exception) {
            log.warn("Не удалось отправить событие в сервис мониторинга: {}", exception.getMessage());
        }
    }

    private TriggeredUser formTriggeredUser(BotUser user) {
        if (user == null) {
            return null;
        }

        String userId = user.getIdOnPlatform();
        return new TriggeredUser("{getName(" + userId + ")}", "{getLink(" + userId + ")}");
    }

    private String formExceptionMessage(Exception exception) {
        return "Получено исключение " + exception.getClass().getSimpleName() + ": " + exception.getMessage();
    }

    private String formCurrentTime() {
        return HUMAN_READABLE_TIME_FORMATTER.withZone(ZoneId.of(timeZone)).format(Instant.now());
    }

    private String formChannel(BotUser triggeredBy) {
        return channel + " | endpoint: " + getCurrentEndpoint() + " | platform: " + getPlatform(triggeredBy);
    }

    private String getCurrentEndpoint() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes requestAttributes)) {
            return "unknown";
        }

        HttpServletRequest request = requestAttributes.getRequest();
        return request.getMethod() + " " + request.getRequestURI();
    }

    private String getPlatform(BotUser triggeredBy) {
        if (triggeredBy == null || triggeredBy.getPlatform() == null) {
            return "unknown";
        }

        return triggeredBy.getPlatform().name();
    }
}
