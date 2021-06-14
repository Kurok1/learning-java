package indi.kurok1.database.repository;

import indi.kurok1.database.domain.OrderHeader;
import org.springframework.stereotype.Repository;

/**
 * 订单头仓库
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@Repository
public class OrderHeaderRepository extends CrudEntityRepository<OrderHeader, Long> {

    @Override
    protected Class<OrderHeader> getEntityClass() {
        return OrderHeader.class;
    }

    @Override
    protected Class<Long> getIdClass() {
        return Long.class;
    }
}
