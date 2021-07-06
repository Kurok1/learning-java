package indi.kurok1.rpc.user.service;


import indi.kurok1.rpc.user.autoconfigure.DataSourceContainer;
import indi.kurok1.rpc.commons.api.UserService;
import indi.kurok1.rpc.commons.domain.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:chan@ittx.com.cn">韩超</a>
 * @version 2021.07.06
 */
@Service(value = "userService")
public class UserServiceImpl implements UserService {

    private final JdbcTemplate jdbcTemplate;

    public UserServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getById(Long id) {
        final String sql = "select * from user where id = ?";
        DataSourceContainer.setId(id);
        List<User> userList =  this.jdbcTemplate.query(sql, new BeanPropertyRowMapper<User>(User.class), id);
        if (CollectionUtils.isEmpty(userList))
            return null;

        return userList.get(0);
    }
}
