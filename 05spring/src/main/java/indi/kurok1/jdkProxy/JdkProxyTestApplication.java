package indi.kurok1.jdkProxy;

import java.lang.reflect.Proxy;

/**
 * 测试
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.05
 */
public class JdkProxyTestApplication {

    public static void main(String[] args) {
        Object o = Proxy.newProxyInstance(JdkProxyTestApplication.class.getClassLoader(), new Class[]{Student.class}, new StudentHandler());
        if (o instanceof Student)
            ((Student) o).read();
    }

}
