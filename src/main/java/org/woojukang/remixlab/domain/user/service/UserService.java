package org.woojukang.remixlab.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woojukang.remixlab.domain.user.dto.request.UserCreateRequest;
import org.woojukang.remixlab.domain.user.dto.response.UserCreateResponse;
import org.woojukang.remixlab.domain.user.entity.Role;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 유저 생성하기
    public UserCreateResponse create(UserCreateRequest request){

        User user = User.builder()
                .username(request.username())
                .password(bCryptPasswordEncoder.encode(request.password()))
                .role(Role.USER)
                .exp(0)
                .build();

        userRepository.save(user);

        return new UserCreateResponse(user.getUsername(),
                user.getCreatedAt());
    }

}
