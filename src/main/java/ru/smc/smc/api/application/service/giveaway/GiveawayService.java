package ru.smc.smc.api.application.service.giveaway;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smc.smc.api.domain.entity.BotUser;
import ru.smc.smc.api.domain.entity.GiveawayParticipant;
import ru.smc.smc.api.domain.repository.GiveawayParticipantRepository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GiveawayService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String EMPTY_PARTICIPANTS_MESSAGE = "В текущем розыгрыше пока нет участников";
    private static final String PARTICIPANTS_MESSAGE_FORMAT = "Всего участников розыгрыша: %s\n%s";
    private static final String PARTICIPANT_FORMAT = "%s - {getName(%s:%s)} - {getId()} - %s - {getLink()}";

    private final GiveawayParticipantRepository giveawayParticipantRepository;

    public GiveawayParticipant addParticipant(BotUser user) {
        Optional<GiveawayParticipant> existingParticipant = giveawayParticipantRepository.findByUser(user);

        if (existingParticipant.isPresent()) {
            return existingParticipant.get();
        }

        GiveawayParticipant giveawayParticipant = new GiveawayParticipant();
        giveawayParticipant.setUser(user);
        giveawayParticipant.setPlatform(user.getPlatform());
        giveawayParticipant.setIdOnPlatform(user.getIdOnPlatform());
        giveawayParticipant.setParticipantNumber(findNextParticipantNumber());

        return giveawayParticipantRepository.save(giveawayParticipant);
    }

    public String getParticipantsInfo() {
        List<GiveawayParticipant> participants = findAllParticipants();

        if (participants.isEmpty()) {
            return EMPTY_PARTICIPANTS_MESSAGE;
        }

        return String.format(PARTICIPANTS_MESSAGE_FORMAT, participants.size(), formParticipantsInfo(participants));
    }

    public void clearParticipants() {
        giveawayParticipantRepository.deleteAll();
    }

    public int countParticipants() {
        return (int) giveawayParticipantRepository.count();
    }

    public List<BotUser> getParticipantUsers() {
        return findAllParticipants().stream()
                .map(GiveawayParticipant::getUser)
                .toList();
    }

    public Optional<String> getRandomParticipantsInfo(int participantsAmount) {
        List<GiveawayParticipant> allParticipants = findAllParticipants();

        if (participantsAmount <= 0 || participantsAmount > allParticipants.size()) {
            return Optional.empty();
        }

        List<GiveawayParticipant> randomParticipants = new ArrayList<>(allParticipants);
        Collections.shuffle(randomParticipants, RANDOM);

        return Optional.of(formParticipantsInfo(randomParticipants.subList(0, participantsAmount)));
    }

    private String findNextParticipantNumber() {
        int maxParticipantNumber = giveawayParticipantRepository.findAll().stream()
                .map(GiveawayParticipant::getParticipantNumber)
                .map(this::parseParticipantNumber)
                .max(Integer::compareTo)
                .orElse(0);

        return String.valueOf(maxParticipantNumber + 1);
    }

    private List<GiveawayParticipant> findAllParticipants() {
        return giveawayParticipantRepository.findAll().stream()
                .sorted(Comparator.comparing(participant -> parseParticipantNumber(participant.getParticipantNumber())))
                .toList();
    }

    private String formParticipantsInfo(List<GiveawayParticipant> participants) {
        return participants.stream()
                .map(this::formParticipantInfo)
                .collect(Collectors.joining("\n"));
    }

    private String formParticipantInfo(GiveawayParticipant participant) {
        return String.format(PARTICIPANT_FORMAT,
                participant.getParticipantNumber(),
                participant.getPlatform(),
                participant.getIdOnPlatform(),
                participant.getPlatform());
    }

    private int parseParticipantNumber(String participantNumber) {
        try {
            return Integer.parseInt(participantNumber);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
