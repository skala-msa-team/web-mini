package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.VerdictEntity;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.database.repository.VerdictRepository;
import com.skala.team6.webmini.database.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrialResultServiceTest {
    private static final Long TRIAL_ID = 10L;

    @Mock TrialRepository trialRepository;
    @Mock VerdictRepository verdictRepository;
    @Mock VoteRepository voteRepository;
    @Mock ObjectMapper objectMapper;
    @Mock TrialEntity trial;
    @Mock VerdictEntity verdict;

    private TrialResultService service;

    @BeforeEach
    void setUp() {
        service = new TrialResultService(
                trialRepository, verdictRepository, voteRepository, objectMapper);
    }

    @Test
    void returnsAiVerdictAndPublicVoteCountsSeparately() throws Exception {
        when(trialRepository.findById(TRIAL_ID)).thenReturn(Optional.of(trial));
        when(trial.getStatus()).thenReturn(TrialStatus.ENDED);
        when(verdictRepository.findByTrialId(TRIAL_ID)).thenReturn(Optional.of(verdict));
        when(verdict.getWinnerSide()).thenReturn(TrialSide.B);
        when(verdict.getAFaultRatio()).thenReturn(60);
        when(verdict.getBFaultRatio()).thenReturn(40);
        when(verdict.getSummary()).thenReturn("판결 요지");
        when(verdict.getGrounds()).thenReturn("[\"근거\"]");
        when(verdict.getARecommendation()).thenReturn("A 개선");
        when(verdict.getBRecommendation()).thenReturn("B 개선");
        when(objectMapper.readValue("[\"근거\"]", String[].class))
                .thenReturn(new String[]{"근거"});
        when(voteRepository.countByTrialIdAndSelectedSide(TRIAL_ID, TrialSide.A)).thenReturn(7L);
        when(voteRepository.countByTrialIdAndSelectedSide(TRIAL_ID, TrialSide.B)).thenReturn(13L);

        TrialResultResponse result = service.getResult(TRIAL_ID);

        assertThat(result.trialId()).isEqualTo(TRIAL_ID);
        assertThat(result.verdict().winnerSide()).isEqualTo(TrialSide.B);
        assertThat(result.verdict().aFaultRatio()).isEqualTo(60);
        assertThat(result.verdict().bFaultRatio()).isEqualTo(40);
        assertThat(result.publicVote().aVotes()).isEqualTo(7);
        assertThat(result.publicVote().bVotes()).isEqualTo(13);
        assertThat(result.publicVote().totalVotes()).isEqualTo(20);
    }

    @Test
    void doesNotExposeResultBeforeTrialEnds() {
        when(trialRepository.findById(TRIAL_ID)).thenReturn(Optional.of(trial));
        when(trial.getStatus()).thenReturn(TrialStatus.VOTING);

        assertThatThrownBy(() -> service.getResult(TRIAL_ID))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESULT_NOT_FOUND);
    }
}
