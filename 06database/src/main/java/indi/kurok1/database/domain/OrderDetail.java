package indi.kurok1.database.domain;

import indi.kurok1.database.annoation.Column;
import indi.kurok1.database.annoation.ConvertParam;
import indi.kurok1.database.annoation.Id;
import indi.kurok1.database.annoation.Table;
import indi.kurok1.database.support.convert.LocalDateTimeConverter;
import indi.kurok1.database.support.convert.NumberConverter;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@Getter
@Setter
@Table(name = "order_detail")
public class OrderDetail {

    @Id
    @Column(converter = NumberConverter.class)
    private Long id;

    @Column(converter = NumberConverter.class)
    private Long orderId;

    private String orderCode;

    @Column(converter = LocalDateTimeConverter.class, convertParams = {
            @ConvertParam(key = LocalDateTimeConverter.FORMAT_KEY, value = "yyyy-MM-dd HH:mm:ss")
    })
    private LocalDateTime created = LocalDateTime.now();

    private String createdBy;

    private String itemCode;

    private String itemName;

    @Column(converter = NumberConverter.class)
    private int requestQty;

    @Column(converter = NumberConverter.class)
    private int shipQty = 0;

    @Column(converter = NumberConverter.class)
    private BigDecimal totalPrice;



}
