package org.woojukang.remixlab.query.creation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woojukang.remixlab.domain.creation.entity.Creation;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.global.utils.creation.CreationUtils;
import org.woojukang.remixlab.query.creation.dto.response.ShowMyCreationResponse;
import org.woojukang.remixlab.query.creation.repository.CreationQueryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreationQueryService {

    private final CreationQueryRepository creationQueryRepository;

    public ShowMyCreationResponse findCreationByUserId(Long userId){

        return CreationUtils
                .toShowResponse(creationQueryRepository
                        .findByUserId(userId));

    }

    public Creation findCreationEntityById(Long creationId){

        return creationQueryRepository
                .findCreationEntityById(creationId);

    }

    public Long countCompletedProjects(User user){

        return creationQueryRepository
                .countCompletedProjects(user);
    }


}
