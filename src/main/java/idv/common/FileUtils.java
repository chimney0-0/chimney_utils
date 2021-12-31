package idv.common;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-03-30 15:43
 * fileName：FileUtils
 * Use：
 */
public class FileUtils extends org.apache.commons.io.FileUtils {

    private static final Logger log = LoggerFactory.getLogger(com.seassoon.sixiang.utils.FileUtils.class);

    /**
     * 解压文件到指定目录
     * 解压后的文件名，和之前一致
     *
     * @param zipFile 待解压的zip文件
     * @param descDir 指定目录
     * @return 第一个entry name
     */
    public static String unZipFiles(File zipFile, String descDir) throws IOException {

        if (!zipFile.getName().toLowerCase().endsWith(".zip")) {
            throw new RuntimeException("文件不是zip格式");
        }

        //解决中文文件夹乱码
        ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));

        Enumeration<? extends ZipEntry> entries = zip.entries();
        int count = 1;
        String firstEntryName = "";
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String zipEntryName = entry.getName();
            if (zipEntryName.contains("MACOSX")) {
                continue;
            }
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + "/" + zipEntryName).replaceAll("\\*", "/");
            if (count == 1 && zipEntryName.contains("/")) {
                firstEntryName = zipEntryName.substring(0, zipEntryName.indexOf("/"));
            }
            count++;

            // 判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            // 如果文件全路径为文件夹,上面已经创建,跳过
            if (new File(outPath).isDirectory()) {
                file.mkdirs();
                continue;
            }

            FileOutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
        zip.close();
        log.info(descDir);
        log.info("******************解压完毕********************");
        return firstEntryName;
    }

    /**
     * 根据文件目录递归获取其下所有指定匹配类型的文件，包括任意层级子目录下匹配的文件
     *
     * @param filePath   目录
     * @param acceptType 匹配类型
     * @return 文件集合
     */
    public static List<File> getAllFileByPath(String filePath, List<String> acceptType) {
        File file = new File(filePath);
        List<File> fileList = new ArrayList<>(16);
        if (file.exists() && file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                return new ArrayList<>();
            }
            for (File item : listFiles) {
                if (item.isFile() && acceptType.stream().anyMatch(item.getName()::endsWith)) {
                    fileList.add(item);
                } else if (item.isDirectory()) {
                    List<File> temp = getAllFileByPath(item.getPath(), acceptType);
                    if (!CollectionUtils.isEmpty(temp)) {
                        fileList.addAll(temp);
                    }
                }
            }
        }
        return fileList;
    }


    /**
     * web下载 文件输出到网络
     *
     * @param inputStream 输入流
     * @param response
     */
    @SuppressWarnings("deprecation")
    public static void downloadFile(InputStream inputStream, String fileName, HttpServletResponse response) throws IOException {
        try {
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF8"));
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Expose-Headers", "Content-Length, Content-disposition");
            response.setContentType("application/x-zip-compressed;charset=UTF-8;");
            response.setCharacterEncoding("utf-8");
            byte[] bs = new byte[255 * 1024];
            int len;
            while (-1 != (len = inputStream.read(bs))) {
                response.getOutputStream().write(bs, 0, len);
            }
        } catch (IOException e) {
            log.error("download fail", e);
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
            response.flushBuffer();
        }
    }

    /**
     * web下载(本地输出到网络)
     *
     * @param response
     */
    @SuppressWarnings("deprecation")
    public static void downloadFile(String basePath, HttpServletResponse response) {
        FileInputStream inputStream = null;
        try {
            File file = new File(basePath);
            if (file.exists() && file.isFile()) {
                response.reset();
                response.addHeader("Access-Control-Allow-Origin", "*");
                response.addHeader("Content-Length", "" + file.length());
                response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(file.getName(), "UTF8"));
                response.setHeader("Access-Control-Expose-Headers", "Content-Length, Content-disposition");
                response.setContentType("application/x-zip-compressed;charset=UTF-8;");
                response.setCharacterEncoding("utf-8");
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

                inputStream = new FileInputStream(file);

                byte[] bs = new byte[1024];
                int len;
                while (-1 != (len = inputStream.read(bs))) {
                    response.getOutputStream().write(bs, 0, len);
                }
            } else {
                log.error("文件不存在");
            }
        } catch (IOException e) {
            log.error("file downlaod fail", e);
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    /**
     * 添加压缩文件
     *
     * @param sourceFilePath 目标文件夹
     * @param zipFilePath    保存zip路径
     * @param fileName       保存zip文件名
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean fileToZip(String sourceFilePath, String zipFilePath, String fileName) {
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        ZipOutputStream zos = null;
        try {
            File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
            if (zipFile.exists()) {
                log.info(zipFilePath + "目录下存在名字为:" + fileName + ".zip" + "打包文件.");
            } else {
                File[] sourceFiles = sourceFile.listFiles();
                if (null != sourceFiles && sourceFiles.length > 0) {
                    zos = new ZipOutputStream(new FileOutputStream(zipFile));
                    for (File file : sourceFiles) {
                        //创建ZIP实体，并添加进压缩包
                        ZipEntry zipEntry = new ZipEntry(file.getName());
                        zos.putNextEntry(zipEntry);
                        //读取待压缩的文件并写进压缩包里
                        FileInputStream fis = new FileInputStream(file);
                        IOUtils.copy(fis, zos);
                        IOUtils.closeQuietly(fis);
                    }
                    flag = true;
                }
            }
        } catch (IOException e) {
            log.error("add zip file fail ", e);
        } finally {
            //关闭流
            try {
                if (null != zos) {
                    zos.close();
                }
            } catch (IOException e) {
                log.error("close zos stream fail", e);
            }
        }
        return flag;
    }

    /**
     * 根据文件路径 返回 其 文本字符串
     *
     * @param filePath 相对项目根路径
     * @return
     */
    public static String loadFileStrByPath(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException(filePath);
        }
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }


    public static String saveFile(InputStream inputStream, String path)
            throws IOException {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (Exception e) {
            log.error("save file fail", e);
            return null;
        }
        return path;
    }

    public static void toFile(String jsonstr, String baseDir, String encoding) throws IOException {
        File file = new File(baseDir);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        // 存入文件
        OutputStream outputStream = new FileOutputStream(file);
        try {
            // UTF-8写入BOM头部
            encoding = encoding.toUpperCase();
//            if (encoding.equals("UTF-8")) {
//                byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
//                outputStream.write(bom);
//            }
            byte[] data = jsonstr.getBytes(encoding);
            outputStream.write(data);
        } finally {
            // 确保文件被关闭
            try {
                outputStream.close();
            } catch (Exception e) {
                log.error("close outputStream fail ",e);
            }
        }
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    public static void getFile(InputStream is, String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            file.mkdirs();
        }
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        in = new BufferedInputStream(is);
        out = new BufferedOutputStream(new FileOutputStream(fileName));
        int len = -1;
        byte[] b = new byte[1024];
        while ((len = in.read(b)) != -1) {
            out.write(b, 0, len);
        }
        is.close();
        in.close();
        out.close();
    }
}

