package org.woojukang.remixlab.global.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.domain.user.repository.UserRepository;
import org.woojukang.remixlab.global.config.exception.BaseExceptionEnum;
import org.woojukang.remixlab.global.config.exception.domain.BaseException;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(()
                        -> new BaseException(BaseExceptionEnum
                        .ENTITY_NOT_FOUND));

        return new UserDetailsImpl(user);

    }
}
