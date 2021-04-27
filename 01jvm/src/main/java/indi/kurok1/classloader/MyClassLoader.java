package indi.kurok1.classloader;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 自定义ClassLoader,支持从 {@code META-INF/xlasses/}中加载对应的xlass文件到jvm中
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.04.26
 * @see ClassLoader
 * @see ClassLoader#defineClass(String, byte[], int, int)
 */
public class MyClassLoader extends ClassLoader {

    private static final String RESOURCE_PATH = "META-INF/xlasses/";

    public static void main(String[] args) {
        String className = "Hello";
        String fileName = "Hello.xlass";
        MyClassLoader classLoader = new MyClassLoader();
        Object instance = classLoader.loadAndInstance(className, fileName);
        if (instance == null) {
            System.err.printf("Failed to load or create class [%s] with fileName:[%s]\n", className, fileName);
            return;
        }

        Class<?> clazz = instance.getClass();
        //打印所有方法
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            System.out.printf("find method : [%s]\n", method.getName());
        }
        //输出结果
        /**
         * find method : [hello]
         */
        //利用反射执行hello方法
        //Method helloMethod = declaredMethods[0];
        try {
            Method helloMethod = clazz.getMethod("hello");
            helloMethod.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            System.err.println("Failed to invoke method!");
        }

    }


    /**
     * 加载一个xlass文件到jvm中，并且初始化一个实例返回
     * @param className 类名称,不允许为空
     * @param fileName xlass文件路径,不允许为空
     * @return 如果加载并且实例化成功则返回实例，否则返回null
     */
    private Object loadAndInstance(String className, String fileName) {
        if (className == null || className.length() == 0)
            throw new NullPointerException();

        if (fileName == null || fileName.length() == 0)
            throw new NullPointerException();
        try {

            //拼接路径,获取输入流,并且根目录默认指向classpath
            InputStream inputStream = super.getResourceAsStream(RESOURCE_PATH + fileName);
            if (inputStream == null)
                return null;//没找到资源
            int length = inputStream.available();
            if (length == 0) {
                return null;//没有什么可以加载的
            }

            byte[] source = new byte[length];
            inputStream.read(source, 0 , length);
            //解码
            source = decode(source);

            //加载到jvm
            Class<?> clazz = super.defineClass(className, source, 0, length);
            return clazz.newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }


    /**
     * 对xlass文件进行编码转换
     * @param source 源字节码
     * @return 返回转换后的字节码
     */
    public static byte[] decode(byte[] source) {
        byte[] result = new byte[source.length];
        for (int i = 0; i < source.length; i++) {
            result[i] = (byte) (255 - source[i]);
        }
        source = null;//及时将源数据变为垃圾数据，等待gc回收
        return result;
    }

}
