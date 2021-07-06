package indi.kurok1.rpc.hmily.commons.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 账户
 *
 * @author <a href="mailto:chan@ittx.com.cn">韩超</a>
 * @version 2021.07.06
 */
@Data
public class Account {

    private Long id;
    private Long userId;
    private BigDecimal onHandQty;//已有数量
    private BigDecimal lockedQty;//锁定数量
    private BigDecimal inTransitQty;//在途数量

}
