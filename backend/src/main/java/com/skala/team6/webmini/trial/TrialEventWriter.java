package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.model.TrialSpeaker;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.TrialEventEntity;
import com.skala.team6.webmini.database.repository.TrialEventRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class TrialEventWriter {
    private final TrialEventRepository repository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher publisher;

    public TrialEventWriter(TrialEventRepository repository, ObjectMapper objectMapper,
                            ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.publisher = publisher;
    }

    public TrialEventEntity save(TrialEntity trial, String type, TrialSpeaker speaker,
                                 String content, Map<String, Object> payload) {
        long sequence = repository.findLatestSequenceByTrialId(trial.getId()) + 1;
        TrialEventEntity saved = repository.saveAndFlush(new TrialEventEntity(
                trial, sequence, type, speaker, content, objectMapper.writeValueAsString(payload)));
        publisher.publishEvent(new TrialEventSavedEvent(new TrialEventMessage(
                saved.getId(), trial.getId(), sequence, type, speaker, content,
                saved.getCreatedAt(), payload)));
        return saved;
    }
}
