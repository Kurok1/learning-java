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
 * 订单头
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@Getter
@Setter
@Table(name = "order_header")
public class OrderHeader {

    @Id
    @Column(converter = NumberConverter.class)
    private Long id;

    private String code;

    private String shipTo;

    private String shipToAddress;

    private String shipToPhone;

    @Column(converter = LocalDateTimeConverter.class, convertParams = {
            @ConvertParam(key = LocalDateTimeConverter.FORMAT_KEY, value = "yyyy-MM-dd HH:mm:ss")
    })
    private LocalDateTime created = LocalDateTime.now();

    private String createdBy;

    @Column(converter = NumberConverter.class)
    private Long totalQty;

    @Column(converter = NumberConverter.class)
    private Long totalLines;

    @Column(converter = NumberConverter.class)
    private BigDecimal totalPrice;



}
