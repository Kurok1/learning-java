package indi.kurok1.database.repository;

import indi.kurok1.database.domain.User;
import org.springframework.stereotype.Repository;

/**
 * 用户服务类型
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@Repository
public class UserRepository extends CrudEntityRepository<User, Long> {

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    protected Class<Long> getIdClass() {
        return Long.class;
    }

}
