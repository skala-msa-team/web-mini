package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.model.RelationshipType;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.database.entity.PostEntity;
import com.skala.team6.webmini.database.entity.AiGuideQuestionEntity;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.TrialPartyEntity;
import com.skala.team6.webmini.database.entity.UserEntity;
import com.skala.team6.webmini.database.repository.PostRepository;
import com.skala.team6.webmini.database.repository.AiGuideQuestionRepository;
import com.skala.team6.webmini.database.repository.TrialPartyRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.database.repository.TrialStatementRepository;
import com.skala.team6.webmini.database.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@EnabledIfEnvironmentVariable(named = "DB_INTEGRATION_TEST", matches = "true")
class TrialPreparationAcceptanceTest {
    private static final String DEMO_USER_ID = "11111111-1111-4111-8111-111111111111";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TrialRepository trialRepository;
    @Autowired
    private TrialPartyRepository trialPartyRepository;
    @Autowired
    private TrialStatementRepository trialStatementRepository;
    @Autowired
    private AiGuideQuestionRepository questionRepository;
    @Autowired
    private EntityManager entityManager;

    private TrialEntity trial;
    private TrialPartyEntity aParty;
    private TrialPartyEntity bParty;

    @BeforeEach
    void setUp() {
        UserEntity user = userRepository.save(new UserEntity(UUID.randomUUID().toString(), "작성자"));
        PostEntity post = postRepository.save(new PostEntity(
                user, "게시글", "내용", RelationshipType.COUPLE, true));
        trial = trialRepository.save(new TrialEntity(post, user));
        aParty = trialPartyRepository.save(new TrialPartyEntity(trial, TrialSide.A, "A측"));
        bParty = trialPartyRepository.save(new TrialPartyEntity(trial, TrialSide.B, "B측"));
    }

    @Test
    void savesAndUpdatesStatementsWithoutOverwritingOtherSide() throws Exception {
        saveStatement(TrialSide.A, "A측 최초 상황")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.side").value("A"))
                .andExpect(jsonPath("$.data.statement.situation").value("A측 최초 상황"));
        saveStatement(TrialSide.B, "B측 상황").andExpect(status().isOk());
        saveStatement(TrialSide.A, "A측 수정 상황").andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        assertThat(trialStatementRepository.findByTrialPartyId(aParty.getId()).orElseThrow().getSituation())
                .isEqualTo("A측 수정 상황");
        assertThat(trialStatementRepository.findByTrialPartyId(bParty.getId()).orElseThrow().getSituation())
                .isEqualTo("B측 상황");
    }

    @Test
    void rejectsMissingTrialInvalidSideAndMissingDemoUser() throws Exception {
        mockMvc.perform(put("/api/v1/trials/{trialId}/parties/A/statement", Long.MAX_VALUE)
                        .header("X-Demo-User-Id", DEMO_USER_ID)
                        .contentType("application/json")
                        .content(statementRequest("상황")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TRIAL_NOT_FOUND"));

        mockMvc.perform(put("/api/v1/trials/{trialId}/parties/C/statement", trial.getId())
                        .header("X-Demo-User-Id", DEMO_USER_ID)
                        .contentType("application/json")
                        .content(statementRequest("상황")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/v1/trials/{trialId}/parties/A/statement", trial.getId())
                        .contentType("application/json")
                        .content(statementRequest("상황")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("DEMO_USER_REQUIRED"));
    }

    @Test
    void savesOnlyOwnSideAnswersAndCalculatesCompletion() throws Exception {
        AiGuideQuestionEntity aQuestion1 = questionRepository.save(
                new AiGuideQuestionEntity(aParty, 1, "A측 질문 1"));
        AiGuideQuestionEntity aQuestion2 = questionRepository.save(
                new AiGuideQuestionEntity(aParty, 2, "A측 질문 2"));
        AiGuideQuestionEntity bQuestion = questionRepository.save(
                new AiGuideQuestionEntity(bParty, 1, "B측 질문"));

        saveAnswers(TrialSide.A, """
                [{"questionId":%d,"answer":"첫 답변"}]
                """.formatted(aQuestion1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.allAnswered").value(false));

        saveAnswers(TrialSide.A, """
                [{"questionId":%d,"answer":"둘째 답변"}]
                """.formatted(aQuestion2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.allAnswered").value(true));

        entityManager.flush();
        entityManager.clear();
        assertThat(questionRepository.findById(aQuestion1.getId()).orElseThrow().getAnswer())
                .isEqualTo("첫 답변");
        assertThat(questionRepository.findById(aQuestion1.getId()).orElseThrow().getAnsweredAt())
                .isNotNull();
        assertThat(questionRepository.findById(bQuestion.getId()).orElseThrow().getAnswer())
                .isNull();

        saveAnswers(TrialSide.B, """
                [{"questionId":%d,"answer":"잘못된 측 답변"}]
                """.formatted(aQuestion1.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("GUIDE_QUESTION_NOT_FOUND"));
    }

    @Test
    void savesArgumentDraftOnlyAfterAllGuideAnswers() throws Exception {
        saveStatement(TrialSide.A, "A측 상황").andExpect(status().isOk());
        AiGuideQuestionEntity question = questionRepository.save(
                new AiGuideQuestionEntity(aParty, 1, "A측 질문"));

        updateArgument(TrialSide.A, "사실 요약", "변론 내용")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("GUIDE_ANSWERS_INCOMPLETE"));

        question.answer("완료된 답변", OffsetDateTime.now());
        questionRepository.save(question);

        updateArgument(TrialSide.A, " 사실 요약 ", " 변론 내용 ")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.side").value("A"))
                .andExpect(jsonPath("$.data.factSummary").value("사실 요약"))
                .andExpect(jsonPath("$.data.argumentText").value("변론 내용"));

        entityManager.flush();
        entityManager.clear();
        var saved = trialStatementRepository.findByTrialPartyId(aParty.getId()).orElseThrow();
        assertThat(saved.getFactSummary()).isEqualTo("사실 요약");
        assertThat(saved.getArgumentText()).isEqualTo("변론 내용");
        assertThat(trialStatementRepository.findByTrialPartyId(bParty.getId())).isEmpty();
    }

    @Test
    void confirmsEachPartyIndependentlyAndReportsBothConfirmed() throws Exception {
        prepareArgument(aParty, TrialSide.A);
        prepareArgument(bParty, TrialSide.B);

        startTrial()
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PARTIES_NOT_READY"));

        confirmArgument(TrialSide.A)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.side").value("A"))
                .andExpect(jsonPath("$.data.confirmedAt").isNotEmpty())
                .andExpect(jsonPath("$.data.bothConfirmed").value(false));
        mockMvc.perform(get("/api/v1/trials/{trialId}", trial.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.aParty.ready").value(true))
                .andExpect(jsonPath("$.data.bParty.ready").value(false));
        startTrial()
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PARTIES_NOT_READY"));
        OffsetDateTime firstConfirmedAt = trialStatementRepository
                .findByTrialPartyId(aParty.getId()).orElseThrow().getConfirmedAt();

        confirmArgument(TrialSide.A)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bothConfirmed").value(false));
        assertThat(trialStatementRepository.findByTrialPartyId(aParty.getId())
                .orElseThrow().getConfirmedAt()).isEqualTo(firstConfirmedAt);

        confirmArgument(TrialSide.B)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bothConfirmed").value(true));
        mockMvc.perform(get("/api/v1/trials/{trialId}", trial.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.aParty.ready").value(true))
                .andExpect(jsonPath("$.data.bParty.ready").value(true));
        startTrial()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("INTRODUCTION"));

        entityManager.flush();
        entityManager.clear();
        assertThat(trialPartyRepository.findByTrialIdAndSide(trial.getId(), TrialSide.A)
                .orElseThrow().isReady()).isTrue();
        assertThat(trialPartyRepository.findByTrialIdAndSide(trial.getId(), TrialSide.B)
                .orElseThrow().isReady()).isTrue();
        assertThat(trialStatementRepository.findByTrialPartyId(bParty.getId())
                .orElseThrow().getConfirmedAt()).isNotNull();
    }

    private org.springframework.test.web.servlet.ResultActions saveStatement(
            TrialSide side,
            String situation
    ) throws Exception {
        return mockMvc.perform(put("/api/v1/trials/{trialId}/parties/{side}/statement", trial.getId(), side)
                .header("X-Demo-User-Id", DEMO_USER_ID)
                .contentType("application/json")
                .content(statementRequest(situation)));
    }

    private String statementRequest(String situation) {
        return """
                {
                  "incidentTime": "어제 저녁",
                  "situation": "%s",
                  "counterpartAction": "상대 행동",
                  "ownAction": "내 행동",
                  "afterConversation": "이후 대화",
                  "desiredResolution": "원하는 해결"
                }
                """.formatted(situation);
    }

    private org.springframework.test.web.servlet.ResultActions saveAnswers(
            TrialSide side,
            String answers
    ) throws Exception {
        return mockMvc.perform(put("/api/v1/trials/{trialId}/parties/{side}/guide-answers", trial.getId(), side)
                .header("X-Demo-User-Id", DEMO_USER_ID)
                .contentType("application/json")
                .content("{\"answers\":" + answers + "}"));
    }

    private org.springframework.test.web.servlet.ResultActions updateArgument(
            TrialSide side,
            String factSummary,
            String argumentText
    ) throws Exception {
        return mockMvc.perform(put("/api/v1/trials/{trialId}/parties/{side}/argument-draft", trial.getId(), side)
                .header("X-Demo-User-Id", DEMO_USER_ID)
                .contentType("application/json")
                .content("""
                        {"factSummary":"%s","argumentText":"%s"}
                        """.formatted(factSummary, argumentText)));
    }

    private void prepareArgument(TrialPartyEntity party, TrialSide side) throws Exception {
        saveStatement(side, side + "측 상황").andExpect(status().isOk());
        AiGuideQuestionEntity question = questionRepository.save(
                new AiGuideQuestionEntity(party, 1, side + "측 질문"));
        question.answer("완료된 답변", OffsetDateTime.now());
        questionRepository.save(question);
        updateArgument(side, side + "측 사실", side + "측 변론").andExpect(status().isOk());
    }

    private org.springframework.test.web.servlet.ResultActions confirmArgument(TrialSide side)
            throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/api/v1/trials/{trialId}/parties/{side}/confirm", trial.getId(), side)
                .header("X-Demo-User-Id", DEMO_USER_ID));
    }

    private org.springframework.test.web.servlet.ResultActions startTrial() throws Exception {
        return mockMvc.perform(post("/api/v1/trials/{trialId}/start", trial.getId())
                .header("X-Demo-User-Id", DEMO_USER_ID));
    }
}
