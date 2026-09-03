package com.skala.team6.webmini.database;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoSchemaMigrationTest {

    private static final String MIGRATION = "db/migration/V1__create_demo_schema.sql";

    @Test
    void createsApprovedDemoTablesAndConstraints() throws IOException {
        String sql = readMigration();

        List<String> tables = List.of(
                "users",
                "posts",
                "trials",
                "trial_parties",
                "trial_statements",
                "ai_guide_questions",
                "trial_events",
                "chat_messages",
                "votes",
                "verdicts"
        );

        for (String table : tables) {
            assertThat(sql).contains("CREATE TABLE " + table);
        }

        assertThat(sql)
                .contains("uk_trial_events_trial_sequence")
                .contains("uk_chat_messages_trial_sequence")
                .contains("uk_votes_trial_voter")
                .contains("chk_verdict_ratio_sum")
                .contains("a_fault_ratio + b_fault_ratio = 100")
                .contains("trial_party_id BIGINT NOT NULL UNIQUE")
                .contains("trial_id BIGINT NOT NULL UNIQUE REFERENCES trials(id) ON DELETE CASCADE")
                .contains("REFERENCES trial_parties(id) ON DELETE CASCADE")
                .contains("REFERENCES trials(id) ON DELETE CASCADE")
                .contains("payload JSONB")
                .contains("grounds JSONB NOT NULL")
                .doesNotContain("conflict_reason");
    }

    private String readMigration() throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(MIGRATION)) {
            assertThat(input).as("Demo schema migration").isNotNull();
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
