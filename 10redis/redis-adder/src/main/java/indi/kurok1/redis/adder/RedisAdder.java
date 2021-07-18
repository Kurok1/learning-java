package indi.kurok1.redis.adder;

import indi.kurok1.redis.common.connect.ConnectionProvider;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.18
 */
public class RedisAdder {

    private final String prefix = "add-";

    private final ConnectionProvider connectionProvider = ConnectionProvider.getInstance();

    public void add(String userId) {
        String key = String.format("%s%s", prefix, userId);
        Jedis jedis = connectionProvider.getConnection();
        if (!jedis.exists(key))
            jedis.set(key, "0");

        jedis.incr(key);
    }

    public int commit() {
        Jedis jedis = connectionProvider.getConnection();
        String cursor = ScanParams.SCAN_POINTER_START;
        ScanParams scanParams = new ScanParams();
        scanParams.match(String.format("%s*", prefix));// 匹配以 PLFX-ZZSFP-* 为前缀的 key
        Integer count = 0;
        while (true){
            //使用scan命令获取数据，使用cursor游标记录位置，下次循环使用
            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
            cursor = scanResult.getCursor();// 返回0 说明遍历完成
            if ("0".equals(cursor)){
                break;
            }
            List<String> list = scanResult.getResult();
            for (String key : list) {
                String value = jedis.get(key);
                count = count + (Integer.decode(value));
            }
        }
        return count;
    }

}
