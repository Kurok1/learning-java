package indi.kurok1.redis.domain;

import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.18
 */
public class OrderWrapper {

    public static final String CHANNEL = "order_pub_sub";

    private long orderId;

    private String orderCode;

    private OrderHeader header;

    private List<OrderDetail> details;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public OrderHeader getHeader() {
        return header;
    }

    public void setHeader(OrderHeader header) {
        this.header = header;
    }

    public List<OrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }
}
