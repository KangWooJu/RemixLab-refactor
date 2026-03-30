package org.woojukang.remixlab.query.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woojukang.remixlab.domain.user.dto.request.UserInfoRequest;
import org.woojukang.remixlab.domain.user.dto.response.UserInfoResponse;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.domain.user.repository.UserRepository;
import org.woojukang.remixlab.global.config.exception.BaseExceptionEnum;
import org.woojukang.remixlab.global.config.exception.domain.BaseException;
import org.woojukang.remixlab.query.user.repository.UserQueryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;
    private final UserRepository userRepository;

    // User 객체 찾기
    public User findByUsername(String username){

        return userRepository
                .findByUsername(username)
                .orElseThrow(()->
                        new BaseException(BaseExceptionEnum
                                .USER_NOT_FOUND));

    }

    // User 정보 조회
    public UserInfoResponse userInfo
            (String username){

        User user = userQueryRepository.findByUsername(username);

        return new UserInfoResponse(user.getUsername(),
                user.getExp());

    }


}
