package indi.kurok1.ext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Xlass文件生成
 *
 * @author <a href="mailto:chan@ittx.com.cn">韩超</a>
 * @version 2021.05.12
 */
public class XlassMaker {

    public static void make(String path) {
        String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        InputStream in = XlassMaker.class.getClassLoader().getResourceAsStream(path);
        try {
            String filePath = basePath + path.replace("class", "xlass");
            File file = new File(filePath);
            if (file.exists())
                file.delete();

            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file, true);
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            encode(bytes);
            outputStream.write(bytes);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void encode(byte[] sources) {
        for (int i = 0; i < sources.length; i++) {
            sources[i] = (byte) (255 - sources[i]);
        }
    }

    public static void main(String[] args) {
        make("indi/kuroky/Test.class");
    }

}
