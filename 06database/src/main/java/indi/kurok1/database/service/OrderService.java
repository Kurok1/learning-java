package indi.kurok1.database.service;

import indi.kurok1.database.domain.OrderDetail;
import indi.kurok1.database.domain.OrderHeader;
import indi.kurok1.database.repository.AbstractEntityRepository;
import indi.kurok1.database.repository.OrderDetailRepository;
import indi.kurok1.database.repository.OrderHeaderRepository;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;

/**
 * 订单服务
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@Service
public class OrderService {

    private final OrderHeaderRepository orderHeaderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderService(OrderHeaderRepository orderHeaderRepository, OrderDetailRepository orderDetailRepository) {
        this.orderHeaderRepository = orderHeaderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public OrderWrapper getOrder(Long id) {
        OrderHeader header = orderHeaderRepository.getById(id);
        if (header == null)
            return null;
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(id);
        OrderWrapper wrapper = new OrderWrapper();
        wrapper.setOrderId(id);
        wrapper.setOrderCode(header.getCode());
        wrapper.setHeader(header);
        wrapper.setDetails(orderDetails);
        return wrapper;
    }


    @Data
    public static class OrderWrapper {
        private Long orderId;
        private String orderCode;

        private OrderHeader header;
        private List<OrderDetail> details;
    }


    /**
     * 创建订单
     * 1.检查数据库同是否存在相同单号的订单
     *      1.1 存在，删除原订单
     * 2.创建订单明细（orderId=0）
     * 3.创建订单头
     * 4.查找orderId=0，单号=orderCode的订单，订单明细写入orderId
     * @param wrapper 订单包装
     * @return 创建信息
     */
    public Map<String, Object> save(OrderWrapper wrapper) {
        try {
            //校验数据
            if (!StringUtils.hasLength(wrapper.header.getCode()))
                throw new NullPointerException();

            String code = wrapper.header.getCode();
            wrapper.setOrderCode(code);
            OrderHeader headerInDb = orderHeaderRepository.getByCondition(Collections.singletonMap("code", code));
            if (headerInDb != null)
                orderDetailRepository.delete(headerInDb.getId());

            //统计信息更新
            final AtomicLong totalLines = new AtomicLong(0);
            final AtomicLong totalQty = new AtomicLong(0);
            final AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.ZERO);
            wrapper.header.setTotalLines(0L);
            wrapper.header.setTotalQty(0L);
            wrapper.header.setTotalPrice(BigDecimal.ZERO);
            wrapper.details.forEach(
                    (detail)-> {
                        detail.setOrderId(0L);
                        detail.setOrderCode(wrapper.getOrderCode());
                        totalLines.incrementAndGet();
                        totalQty.addAndGet(detail.getRequestQty());
                        totalPrice.accumulateAndGet(detail.getTotalPrice(), new BinaryOperator<BigDecimal>() {
                            @Override
                            public BigDecimal apply(BigDecimal bigDecimal, BigDecimal bigDecimal2) {
                                return bigDecimal.add(bigDecimal2);
                            }
                        });
                    }
            );
            wrapper.header.setTotalLines(totalLines.get());
            wrapper.header.setTotalQty(totalQty.get());
            wrapper.header.setTotalPrice(totalPrice.get());
            //先创建订单明细
            for (OrderDetail detail : wrapper.getDetails()) {
                orderDetailRepository.insert(detail);
            }

            //创建订单头
            OrderHeader orderHeader = orderHeaderRepository.insert(wrapper.getHeader());
            if (orderHeader != null) {//创建成功
                List<OrderDetail> details = orderDetailRepository.findByConditions(Collections.singletonMap("orderCode", orderHeader.getCode()));
                for (OrderDetail orderDetail : details) {
                    //更新订单id
                    orderDetail.setOrderId(orderHeader.getId());
                    orderDetailRepository.update(orderDetail);
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("code", 0);
            data.put("error", false);
            data.put("orderCode", wrapper.orderCode);
            return data;
        } catch (Throwable t) {
            t.printStackTrace();
            Map<String, Object> data = new HashMap<>();
            data.put("code", 1);
            data.put("error", true);
            data.put("message", t.getMessage());
            return data;
        } finally {
            AbstractEntityRepository.release();
        }
    }

}
