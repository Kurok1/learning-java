package indi.kurok1.database.domain;

import indi.kurok1.database.annoation.Column;
import indi.kurok1.database.annoation.Id;
import indi.kurok1.database.annoation.Table;
import indi.kurok1.database.support.convert.NumberConverter;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户实体
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@Table(name = "user")
@Getter
@Setter
public class User {

    @Id
    @Column(converter = NumberConverter.class)
    private Long id;

    private String code;

    private String name;

    private String phone;

    private String email;

}
