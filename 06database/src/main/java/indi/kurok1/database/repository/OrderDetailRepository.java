package indi.kurok1.database.repository;

import indi.kurok1.database.domain.OrderDetail;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * 订单明细仓库
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@Repository
public class OrderDetailRepository extends CrudEntityRepository<OrderDetail, Long> {

    @Override
    protected Class<OrderDetail> getEntityClass() {
        return OrderDetail.class;
    }

    @Override
    protected Class<Long> getIdClass() {
        return Long.class;
    }


    public List<OrderDetail> findByOrderId(Long orderId) {
        return findByConditions(Collections.singletonMap("orderId", orderId));
    }
}
