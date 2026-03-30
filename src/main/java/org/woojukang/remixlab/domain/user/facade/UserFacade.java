package org.woojukang.remixlab.domain.user.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.woojukang.remixlab.domain.user.dto.request.UserCreateRequest;
import org.woojukang.remixlab.domain.user.dto.response.UserCreateResponse;
import org.woojukang.remixlab.domain.user.dto.response.UserInfoResponse;
import org.woojukang.remixlab.query.user.service.UserQueryService;
import org.woojukang.remixlab.domain.user.service.UserService;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final UserQueryService userQueryService;

    // 유저 생성하기
    @Transactional
    public UserCreateResponse create(UserCreateRequest userCreateRequest){

        return userService.create(userCreateRequest);

    }

    public UserInfoResponse userInfo(String username){

        return userQueryService.userInfo(username);
    }


}
