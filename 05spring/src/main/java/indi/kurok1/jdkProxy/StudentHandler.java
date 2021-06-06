package indi.kurok1.jdkProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理 {@link Student#read}
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.05
 */
public class StudentHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("read".equals(method.getName())) {
            System.out.println("before read");
            System.out.println("reading");
            System.out.println("after read");
        }
        return proxy;
    }
}
