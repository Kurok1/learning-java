package indi.kurok1.rpc.user.autoconfigure;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.ArrayList;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.06
 */
public class DataSourceContainer extends AbstractRoutingDataSource {

    public static final String MASTER_KEY = "master";

    private static ThreadLocal<Long> id = new ThreadLocal<>();

    /**
     * 只要包含这个header的，视为只需要读
     */
    private final String READ_ONLY_HEADER = "X-READ-ONLY";

    private final ArrayList<String> keys = new ArrayList<>();


    public static void setId(Long id) {
        DataSourceContainer.id.set(id);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        Long choosedId = id.get();
        if (choosedId == null || choosedId < 0)
            return null;

        if (keys.size() == 0)
            return null;

        return keys.get(choosedId.intValue() % keys.size());
    }


    public void addKey(String key) {
        keys.add(key);
    }
}
