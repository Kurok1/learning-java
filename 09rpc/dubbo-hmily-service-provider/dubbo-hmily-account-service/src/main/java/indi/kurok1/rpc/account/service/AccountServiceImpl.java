package indi.kurok1.rpc.account.service;

import indi.kurok1.rpc.account.autoconfiugre.DataSourceContainer;
import indi.kurok1.rpc.commons.api.AccountService;
import indi.kurok1.rpc.commons.api.UserService;
import indi.kurok1.rpc.commons.domain.Account;
import indi.kurok1.rpc.commons.domain.User;
import org.dromara.hmily.annotation.Hmily;
import org.dromara.hmily.common.exception.HmilyRuntimeException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户服务实现
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.07
 */
@Service("accountService")
public class AccountServiceImpl implements AccountService {

    private final UserService userService;

    private final JdbcTemplate jdbcTemplate;

    public AccountServiceImpl(UserService userService, JdbcTemplate jdbcTemplate) {
        this.userService = userService;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Account getByUserId(Long userId) {
        DataSourceContainer.setId(userId);
        final String sql = "select * from account where userId = ?";
        List<Account> list =  this.jdbcTemplate.query(sql, new BeanPropertyRowMapper<Account>(Account.class), userId);
        if (CollectionUtils.isEmpty(list))
            return null;

        return list.get(0);
    }

    /**
     * 转账逻辑：
     * T:
     * 1.检查fromUser和toUser是否存在账户，任意一个不存在则失败
     * 2.检查fromUser的账户是否有剩余，并且剩余数量大于等于转账金额
     * 3.fromUser的账户增加锁定数量，金额为转账金额
     * 4.toUser的账户增加在途数量，金额为转账金额
     * COMMIT：
     * 1. fromUser的账户的已有金额和锁定金额均扣减掉转账金额
     * 2. toUser的账户的已有金额增加转账金额，在途金额扣减掉转账金额
     *
     * @param fromUserId 原用户id
     * @param toUserId   目标用户id
     * @param totalQty   金额
     * @return
     */
    @Override
    @Hmily(confirmMethod = "confirmTransfer", cancelMethod = "cancelTransfer")
    public boolean transfer(Long fromUserId, Long toUserId, BigDecimal totalQty) {

        if (totalQty.compareTo(BigDecimal.ZERO) < 0)
            throw new HmilyRuntimeException("illegal account");

        DataSourceContainer.setId(fromUserId);
        User fromUser = this.userService.getById(fromUserId);
        if (fromUser == null)
            throw new HmilyRuntimeException("from user not found!!");

        DataSourceContainer.setId(toUserId);
        User toUser = this.userService.getById(toUserId);
        if (toUser == null)
            throw new HmilyRuntimeException("to user not found!!");

        DataSourceContainer.setId(fromUserId);
        Account fromAccount = getByUserId(fromUserId);
        if (fromAccount == null)
            throw new HmilyRuntimeException("from account not found!!");

        DataSourceContainer.setId(toUserId);
        Account toAccount = getByUserId(toUserId);
        if (toAccount == null)
            throw new HmilyRuntimeException("to account not found!!");

        //检查数量
        if (fromAccount.getOnHandQty().compareTo(totalQty) < 0)
            throw new HmilyRuntimeException("from account is not enough!!");

        DataSourceContainer.setId(fromAccount.getId());
        int updateFrom = this.jdbcTemplate.update("update account set lockedQty = lockedQty + ? where id = ?", totalQty, fromAccount.getId());
        DataSourceContainer.setId(toAccount.getId());
        int updateTo = this.jdbcTemplate.update("update account set inTransitQty = inTransitQty + ? where id = ?", totalQty, toAccount.getId());

        return updateFrom > 0 && updateTo > 0;
    }

    /**
     * 1. fromUser的账户的已有金额和锁定金额均扣减掉转账金额
     * 2. toUser的账户的已有金额增加转账金额，在途金额扣减掉转账金额
     * @param fromUserId
     * @param toUserId
     * @param totalQty
     * @return
     */
    public boolean confirmTransfer(Long fromUserId, Long toUserId, BigDecimal totalQty) {
        DataSourceContainer.setId(fromUserId);//决定数据源
        int updateFrom = this.jdbcTemplate.update("update account set onHandQty = onHandQty - ?, lockedQty = lockedQty - ? where userId = ?", totalQty, totalQty, fromUserId);
        DataSourceContainer.setId(toUserId);//决定数据源
        int updateTo = this.jdbcTemplate.update("update account set onHandQty = onHandQty + ?, inTransitQty = inTransitQty - ? where userId = ?", totalQty, totalQty, toUserId);

        return updateFrom > 0 && updateTo > 0;
    }

    /**
     * @param fromUserId
     * @param toUserId
     * @param totalQty
     * @return
     */
    public boolean cancelTransfer(Long fromUserId, Long toUserId, BigDecimal totalQty) {
        System.out.println("转账失败0.0");
        return true;
    }
}
