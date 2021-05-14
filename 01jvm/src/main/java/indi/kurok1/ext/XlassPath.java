package indi.kurok1.ext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * xlass，xar文件所处的全部path集合
 * 所有路径都是相对于classpath
 *
 * @author <a href="mailto:chan@ittx.com.cn">韩超</a>
 * @version 2021.05.06
 */
public class XlassPath {

    private final Set<String> urls = new CopyOnWriteArraySet<>();

    private final String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();

    public XlassPath() {
    }

    public XlassPath(Collection<String> path) {
        if (path.size() > 0)
            urls.addAll(path);
    }

    protected void addUrl(String path) {
        if (!urls.contains(path))
            urls.add(path);
    }

    public InputStream getResourceAsStream(String path) {
        if (urls.isEmpty())
            return null;
        InputStream in = null;
        ZipEntry ze = null;
        for (String url : urls) {
            ZipFile file = null;
            try {
                file = new ZipFile(basePath + url);
                ze = file.getEntry(path);
                if (ze == null)
                    continue;
                in = file.getInputStream(ze);
                return in;
            } catch (IOException ignored) {
            }
        }
        return null;
    }
}
