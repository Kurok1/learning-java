package indi.kurok1.ext;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * TODO
 *
 * @author <a href="mailto:chan@ittx.com.cn">韩超</a>
 * @version 2021.05.06
 */
public class Application {

    public static void main(String[] args) throws IOException {
        XlassLoader xlassLoader = new XlassLoader(new String[]{"Hello.xar"});
        try {
            //xlassLoader.loadClass("indi.kuroky.Test");
            Class<?> clazz = Class.forName("indi.kuroky.Test", false, xlassLoader);
            Object instance = clazz.newInstance();
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                System.out.printf("find method : [%s]\n", method.getName());
            }

            Method method = clazz.getDeclaredMethod("print");
            method.invoke(instance);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
