package indi.kurok1.database.repository;

import indi.kurok1.database.domain.Item;
import org.springframework.stereotype.Repository;

/**
 * 货品服务
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@Repository
public class ItemRepository extends CrudEntityRepository<Item, Long> {

    @Override
    protected Class<Item> getEntityClass() {
        return Item.class;
    }

    @Override
    protected Class<Long> getIdClass() {
        return Long.class;
    }
}
