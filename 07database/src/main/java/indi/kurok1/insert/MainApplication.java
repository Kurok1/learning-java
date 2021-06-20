package indi.kurok1.insert;

/**
 * 测试插入效率使用的
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.20
 */
public class MainApplication {

    public static void main(String[] args) {
        JdbcService jdbcService = new JdbcService();
        long current = jdbcService.doAddBatchByStep(500);
        long spent = System.currentTimeMillis() - current;
        System.out.printf("use %d ms\n", spent);
        while (true) {
            //todo ..
        }
    }

}
