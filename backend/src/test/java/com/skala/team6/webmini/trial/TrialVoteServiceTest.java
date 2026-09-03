package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.UserEntity;
import com.skala.team6.webmini.database.entity.VoteEntity;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.database.repository.VoteRepository;
import com.skala.team6.webmini.demo.DemoUserPersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrialVoteServiceTest {
    private static final Long TRIAL_ID = 10L;
    private static final String DEMO_USER_ID = "demo-user-a";

    @Mock TrialRepository trialRepository;
    @Mock VoteRepository voteRepository;
    @Mock DemoUserPersistenceService demoUserPersistenceService;
    @Mock TrialEntity trial;
    @Mock UserEntity user;

    private TrialVoteService service;

    @BeforeEach
    void setUp() {
        service = new TrialVoteService(
                trialRepository, voteRepository, demoUserPersistenceService);
    }

    @Test
    void savesVoteWithDatabaseTimestampWhenVotingIsOpen() {
        OffsetDateTime phaseEndsAt = OffsetDateTime.now().plusMinutes(1);
        OffsetDateTime votedAt = OffsetDateTime.now();
        VoteEntity savedVote = org.mockito.Mockito.mock(VoteEntity.class);

        when(savedVote.getSelectedSide()).thenReturn(TrialSide.A);

        when(trialRepository.findByIdForUpdate(TRIAL_ID)).thenReturn(Optional.of(trial));
        when(trial.getStatus()).thenReturn(TrialStatus.VOTING);
        when(trial.getPhaseEndsAt()).thenReturn(phaseEndsAt);
        when(demoUserPersistenceService.getOrCreate(DEMO_USER_ID)).thenReturn(user);
        when(user.getId()).thenReturn(21L);
        when(voteRepository.existsByTrialIdAndVoterId(TRIAL_ID, 21L)).thenReturn(false);
        when(voteRepository.saveAndFlush(any(VoteEntity.class))).thenReturn(savedVote);
        when(savedVote.getCreatedAt()).thenReturn(votedAt);

        TrialVoteService.VoteResult result = service.vote(TRIAL_ID, DEMO_USER_ID, TrialSide.A);

        assertThat(result.selectedSide()).isEqualTo(TrialSide.A);
        assertThat(result.votedAt()).isEqualTo(votedAt);
    }

    @Test
    void rejectsDuplicateVoteFromSameDemoUser() {
        when(trialRepository.findByIdForUpdate(TRIAL_ID)).thenReturn(Optional.of(trial));
        when(trial.getStatus()).thenReturn(TrialStatus.VOTING);
        when(trial.getPhaseEndsAt()).thenReturn(OffsetDateTime.now().plusMinutes(1));
        when(demoUserPersistenceService.getOrCreate(DEMO_USER_ID)).thenReturn(user);
        when(user.getId()).thenReturn(21L);
        when(voteRepository.existsByTrialIdAndVoterId(TRIAL_ID, 21L)).thenReturn(true);

        assertThatThrownBy(() -> service.vote(TRIAL_ID, DEMO_USER_ID, TrialSide.B))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ALREADY_VOTED);
    }

    @Test
    void rejectsVoteOutsideVotingWindow() {
        when(trialRepository.findByIdForUpdate(TRIAL_ID)).thenReturn(Optional.of(trial));
        when(trial.getStatus()).thenReturn(TrialStatus.B_ARGUMENT);

        assertThatThrownBy(() -> service.vote(TRIAL_ID, DEMO_USER_ID, TrialSide.A))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.VOTING_NOT_OPEN);
    }
}
