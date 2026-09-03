package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.database.entity.PostEntity;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.TrialPartyEntity;
import com.skala.team6.webmini.database.entity.UserEntity;
import com.skala.team6.webmini.database.repository.PostRepository;
import com.skala.team6.webmini.database.repository.TrialPartyRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.demo.DemoUserPersistenceService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrialCreationService {
    private final DemoUserPersistenceService demoUserPersistenceService;
    private final PostRepository postRepository;
    private final TrialRepository trialRepository;
    private final TrialPartyRepository trialPartyRepository;

    public TrialCreationService(DemoUserPersistenceService demoUserPersistenceService,
                                PostRepository postRepository,
                                TrialRepository trialRepository,
                                TrialPartyRepository trialPartyRepository) {
        this.demoUserPersistenceService = demoUserPersistenceService;
        this.postRepository = postRepository;
        this.trialRepository = trialRepository;
        this.trialPartyRepository = trialPartyRepository;
    }

    @Transactional
    public CreatedTrial create(String demoUserId, Long postId, String aName, String bName) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));
        if (trialRepository.existsByPostId(postId)) {
            throw new ApiException(ErrorCode.TRIAL_ALREADY_EXISTS);
        }
        UserEntity creator = demoUserPersistenceService.getOrCreate(demoUserId);
        try {
            TrialEntity trial = trialRepository.saveAndFlush(new TrialEntity(post, creator));
            List<TrialPartyEntity> parties = trialPartyRepository.saveAll(List.of(
                    new TrialPartyEntity(trial, TrialSide.A, aName.trim()),
                    new TrialPartyEntity(trial, TrialSide.B, bName.trim())
            ));
            return new CreatedTrial(trial, parties);
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(ErrorCode.TRIAL_ALREADY_EXISTS);
        }
    }

    public record CreatedTrial(TrialEntity trial, List<TrialPartyEntity> parties) {
    }
}
