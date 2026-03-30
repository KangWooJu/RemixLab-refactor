package org.woojukang.remixlab.query.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.woojukang.remixlab.domain.user.entity.QUser;
import org.woojukang.remixlab.domain.user.entity.User;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    public User findByUsername(String username){
        return queryFactory
                .selectFrom(user)
                .where(user
                        .username
                        .eq(username))
                .fetchOne();
    }

}
