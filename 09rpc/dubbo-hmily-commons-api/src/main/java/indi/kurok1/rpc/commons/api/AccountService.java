package indi.kurok1.rpc.commons.api;

import indi.kurok1.rpc.commons.domain.Account;

import java.math.BigDecimal;

/**
 * TODO
 *
 * @author <a href="mailto:chan@ittx.com.cn">韩超</a>
 * @version 2021.07.06
 */
public interface AccountService {

    /**
     * 转账逻辑：
     *  T:
     *      1.检查fromUser和toUser是否存在账户，任意一个不存在则失败
     *      2.检查fromUser的账户是否有剩余，并且剩余数量大于等于转账金额
     *      3.fromUser的账户增加锁定数量，金额为转账金额
     *      4.toUser的账户增加在途数量，金额为转账金额
     *  COMMIT：
     *      1. fromUser的账户的已有金额和锁定金额均扣减掉转账金额
     *      2. toUser的账户的已有金额增加转账金额，在途金额扣减掉转账金额
     *  CANCEL:
     *      1.fromUser的账户的锁定金额均扣减掉转账金额
     *      2.toUser的账户的在途金额均扣减掉转账金额
     * @param fromUserId 原用户id
     * @param toUserId 目标用户id
     * @param totalQty 金额
     * @return
     */
    boolean transfer(Long fromUserId, Long toUserId, BigDecimal totalQty);

    Account getByUserId(Long userId);

}
