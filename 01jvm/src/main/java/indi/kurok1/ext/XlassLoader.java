package indi.kurok1.ext;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * XlassLoader加载器，专门用于加载.xlass文件
 * 还可以设置xar文件路径，xar文件类似与jar文件，用于将一些列xlass文件打包
 * xar可以在META-INF/deps/xlass.dep里设置依赖，依赖可以设置依赖的xar文件路径
 *
 * @author <a href="mailto:chan@ittx.com.cn">韩超</a>
 * @version 2021.05.04
 * @see FileInputStream
 */
public class XlassLoader extends ClassLoader {

    private final static String DEPENDENCIES_PATH = "META-INF/deps/xlass.dep";

    private final XlassPath xlassPath;

    public XlassLoader() {
        xlassPath = new XlassPath();
    }

    public XlassLoader(ClassLoader parent) {
        super(parent);
        xlassPath = new XlassPath();
    }

    public XlassLoader(String[] urls) {
        xlassPath = new XlassPath(Arrays.asList(urls));
    }

    public XlassLoader(String[] urls, ClassLoader parent) {
        super(parent);
        xlassPath = new XlassPath(Arrays.asList(urls));
    }



    /**
     * 文件默认后缀
     */
    private final String suffix = ".xlass";

    /**
     * xlass文件编码翻译方法
     * @param source 目标源二进制编码
     */
    private void decode(byte[] source) {
        for (int i = 0; i < source.length; i++) {
            source[i] = (byte) (255 - source[i]);
        }
    }

    /**
     * Finds the class with the specified <a href="#name">binary name</a>.
     * This method should be overridden by class loader implementations that
     * follow the delegation model for loading classes, and will be invoked by
     * the {@link #loadClass <tt>loadClass</tt>} method after checking the
     * parent class loader for the requested class.  The default implementation
     * throws a <tt>ClassNotFoundException</tt>.
     *
     * @param name The <a href="#name">binary name</a> of the class
     * @return The resulting <tt>Class</tt> object
     * @throws ClassNotFoundException If the class could not be found
     * @since 1.2
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> result = null;

        //根据路径查找xlass文件
        final String path = name.replace('.', '/').concat(this.suffix);
        InputStream res = getResourceAsStream(path);
        if (res == null)
            throw new ClassNotFoundException(name);
        try {
            byte[] bytes = new byte[res.available()];
            res.read(bytes);
            result = defineXlass(name, bytes);
        } catch (Throwable t) {
            throw new ClassNotFoundException(name, t);
        } finally {
            try {
                res.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (result == null)
            throw new ClassNotFoundException(name);
        return result;
    }

    protected final Class<?> defineXlass(String name, byte[] source)
            throws ClassFormatError
    {
        decode(source);
        return this.defineClass(name, source, 0, source.length);
    }


    @Override
    public InputStream getResourceAsStream(String name) {
        //优先从父类加载器中查找
        if (super.getParent() instanceof XlassLoader)
            return super.getParent().getResourceAsStream(name);
        //xlassPath查找
        return xlassPath.getResourceAsStream(name);
    }

    /**
     * xar模块依赖解析
     */
    private static class DependenciesParser {

    }
}
