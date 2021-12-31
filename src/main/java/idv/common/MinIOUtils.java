package idv.common;

import com.seassoon.sixiang.frame.exception.BaseException;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chimney
 * @date 2021/12/14
 */
@Configuration
public class MinIOUtils {

    protected Log log = LogFactory.getLog(this.getClass());

    @Value("${minio.url}")
    private String url;
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;
    @Value("${minio.bucket}")
    private String bucket;

    private static MinioClient minioClient;

    private static final String PREFIX_MONGO = "mongofile";
    private static final String PREFIX_SERVER = "serverfile";

    private final Map<String, String> CONTENT_TYPE_MAP = new HashMap<String, String>() {{
        put("png", "image/png");
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("gif", "image/gif");
        put("json", "application/json");
        put("pdf", "application/pdf");
        put("doc", "application/msword");
        put("docx", "application/msword");
        put("zip", "application/x-zip-compressed");
        put("rar", "application/octet-stream");
    }};

    @Bean
    public MinioClient minioClient() {
        minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        return minioClient;
    }

    /*
        替代原gridFs操作
     */
    public InputStream getFileOupByIdGrid(String objectId) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucket)
                    .object(PREFIX_MONGO + "/" + objectId).build());
        } catch (Exception e) {
            log.error("读取文件失败", e);
            throw new BaseException(404, "读取文件失败");
        }
    }

    public void downloadFileById(String objectId, HttpServletResponse response) throws IOException{
        InputStream inputStream = getFileOupByIdGrid(objectId);
        Map<String, String> userMetadata;
        // 获取fileName
        try {
            StatObjectResponse objectStat =
                    minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(PREFIX_MONGO + "/" + objectId).build());
            userMetadata = objectStat.userMetadata();
        } catch (Exception e) {
            log.error("读取文件失败", e);
            throw new BaseException(404, "读取文件失败");
        }
        FileUtils.downloadFile(inputStream, userMetadata.getOrDefault("filename", objectId), response);
    }

    public void downloadFileById(String objectId, String baseDir) throws IOException{
        InputStream inputStream = getFileOupByIdGrid(objectId);
        Map<String, String> userMetadata;
        // 获取fileName
        try {
            StatObjectResponse objectStat =
                    minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(PREFIX_MONGO + "/" + objectId).build());
            userMetadata = objectStat.userMetadata();
        } catch (Exception e) {
            log.error("读取文件失败", e);
            throw new BaseException(404, "读取文件失败");
        }
        FileUtils.saveFile(inputStream, baseDir + userMetadata.getOrDefault("filename", objectId));
    }

    public Map<String, String> statById(String objectId){
        try {
            StatObjectResponse objectStat =
                    minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(PREFIX_MONGO + "/" + objectId).build());
            return objectStat.userMetadata();
        } catch (Exception e) {
            log.error("读取文件失败", e);
            throw new BaseException(404, "读取文件失败");
        }
    }

    public List<InputStream> getFileOupByIdGridBatch(List<String> objectIdList){
        List<InputStream> list = new ArrayList<>();
        try {
            for(String objectId : objectIdList) {
                list.add(minioClient.getObject(GetObjectArgs.builder().bucket(bucket)
                        .object(PREFIX_MONGO + "/" + objectId).build()));
            }
        } catch (Exception e) {
            log.error("读取文件失败", e);
            throw new BaseException(404, "读取文件失败");
        }
        return list;
    }

    public String saveFileGrid(InputStream inputStream, String fileName) {
        return saveFileGrid(inputStream, fileName, new HashMap<>());
    }

    public String saveFileGrid(InputStream inputStream, String fileName, Map<String, String> metadata) {
        metadata.put("filename", fileName);
        // 路径沿用 _id 加前缀
        String objectId = generateObjectId();
        try {
            // 根据后缀名 设置不同的contentType
            String contentType = getContentType(fileName);
            PutObjectArgs putObjectArgs;
            if(StringUtils.isEmpty(contentType)){
                log.debug("未匹配到后缀:"+fileName);
                putObjectArgs = PutObjectArgs.builder().bucket(bucket)
                        .object(PREFIX_MONGO + "/" + objectId).stream(inputStream, -1, 10485760)
                        .userMetadata(metadata)
                        .build();
            }else {
                putObjectArgs = PutObjectArgs.builder().bucket(bucket)
                        .object(PREFIX_MONGO + "/" + objectId).stream(inputStream, -1, 10485760)
                        .userMetadata(metadata)
                        .contentType(contentType)
                        .build();
            }
            minioClient.putObject(putObjectArgs);
            IOUtils.closeQuietly(inputStream);
        } catch (Exception e) {
            log.error("保存文件失败", e);
            throw new BaseException(404, "保存文件失败");
        }
        return objectId;
    }

    public String saveFileGridAssignedId(String objectId, InputStream inputStream, String fileName, Map<String, String> metadata) {
            metadata.put("filename", fileName);
            try {
                // 根据后缀名 设置不同的contentType
                String contentType = getContentType(fileName);
                if(StringUtils.isEmpty(contentType)){
                    contentType = getContentType(metadata.get("filepath"));
                }
                PutObjectArgs putObjectArgs;
                if(StringUtils.isEmpty(contentType)){
                    // 看一下哪些格式后缀没有匹配到
                    log.debug(objectId+"未匹配到后缀:"+fileName);
                    putObjectArgs = PutObjectArgs.builder().bucket(bucket)
                            .object(PREFIX_MONGO + "/" + objectId).stream(inputStream, -1, 10485760)
                            .userMetadata(metadata)
                            .build();
                }else {
                    putObjectArgs = PutObjectArgs.builder().bucket(bucket)
                            .object(PREFIX_MONGO + "/" + objectId).stream(inputStream, -1, 10485760)
                            .userMetadata(metadata)
                            .contentType(contentType)
                            .build();
                }
                minioClient.putObject(putObjectArgs);
                IOUtils.closeQuietly(inputStream);
            } catch (Exception e) {
                log.error("保存文件失败: "+ objectId, e);
            }
            return objectId;
    }

    public void deleteFileId(String objectId){
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucket).object(PREFIX_MONGO + "/" + objectId).build());
        } catch (Exception e) {
            log.error("删除文件失败", e);
            throw new BaseException(404, "删除文件失败");
        }
    }

    public void deleteFileIdBatch(List<String> idList){
        Iterable<Result<DeleteError>> results =
                minioClient.removeObjects(
                        RemoveObjectsArgs.builder().bucket(bucket).objects(idList.stream()
                                .map( id -> new DeleteObject(PREFIX_MONGO + "/" + id)).collect(Collectors.toList())).build());
        try {
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("Error in deleting object " + error.objectName() + "; " + error.message());
            }
        } catch (Exception e) {
            log.error("删除文件失败", e);
            throw new BaseException(404, "删除文件失败");
        }
    }

    /**
     * 生成ObjectId 调用mongo本身的方法
     * @return
     */
    private String generateObjectId() {
        //4字节：UNIX时间戳
        //3字节：机器标识符
        //2字节：表示生成此_id的进程
        //3字节：由一个随机数开始的计数器生成的值
        return ObjectId.get().toHexString();
    }

    private String getContentType(String fileName) {
        if(StringUtils.isEmpty(fileName)){
            return "";
        }
        return CONTENT_TYPE_MAP.get(fileName.substring(fileName.lastIndexOf(".") + 1));
    }

    private String getFileName(String filePath) {
        if(StringUtils.isEmpty(filePath)){
            return "";
        }
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /*
        TODO 替代原服务器操作
     */
    public String saveFilePath(InputStream inputStream, String filePath) {
        return saveFilePath(inputStream, filePath, new HashMap<>());
    }

    public String saveFilePath(InputStream inputStream, String filePath, Map<String, String> metadata) {
        try {
            // 根据后缀名 设置不同的contentType
            String contentType = getContentType(filePath);
            PutObjectArgs putObjectArgs;
            if(StringUtils.isEmpty(contentType)){
                putObjectArgs = PutObjectArgs.builder().bucket(bucket)
                        .object(PREFIX_SERVER + "/" + filePath).stream(inputStream, -1, 10485760)
                        .userMetadata(metadata)
                        .build();
            }else {
                putObjectArgs = PutObjectArgs.builder().bucket(bucket)
                        .object(PREFIX_SERVER + "/" + filePath).stream(inputStream, -1, 10485760)
                        .userMetadata(metadata)
                        .contentType(contentType)
                        .build();
            }
            minioClient.putObject(putObjectArgs);
            IOUtils.closeQuietly(inputStream);
        } catch (Exception e) {
            log.error("保存文件失败", e);
            throw new BaseException(404, "保存文件失败");
        }
        return filePath;
    }

    public void downloadFileFullPath(String filePath, HttpServletResponse response) throws IOException{
        try {
            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucket)
                    .object(PREFIX_SERVER + "/" + filePath).build());
            FileUtils.downloadFile(inputStream, getFileName(filePath), response);
        } catch (Exception e) {
            log.error("下载文件失败", e);
            throw new BaseException(404, "下载文件失败");
        }
    }

    public InputStream getFileOupFullPath(String filePath) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucket)
                    .object(PREFIX_SERVER + "/" + filePath).build());
        } catch (Exception e) {
            log.error("读取文件失败", e);
            throw new BaseException(404, "读取文件失败");
        }
    }

}
