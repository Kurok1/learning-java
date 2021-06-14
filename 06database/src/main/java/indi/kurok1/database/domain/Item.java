package indi.kurok1.database.domain;

import indi.kurok1.database.annoation.Column;
import indi.kurok1.database.annoation.Id;
import indi.kurok1.database.annoation.Table;
import indi.kurok1.database.support.convert.NumberConverter;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 货品
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@Getter
@Setter
@Table(name = "item")
public class Item {

    @Id
    @Column(converter = NumberConverter.class)
    private Long id;

    private String code;

    private String name;

    @Column(converter = NumberConverter.class)
    private BigDecimal price;

}
