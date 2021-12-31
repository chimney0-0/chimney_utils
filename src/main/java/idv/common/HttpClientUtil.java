package idv.common;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 封装了采用HttpClient发送HTTP请求的方法
 * <p>
 * <p>
 * 本工具所采用的是HttpComponents-Client-4.5.6  HttpComponents-httpmime-4.5.6
 * ===========================
 * ===========================
 * sendPostSSLRequest 发送https post请求，  兼容 http post 请求
 * sendGetSSLRequest  发送https get请求，  兼容 http get 请求
 * * @author Berry_Cooper
 * * @date 2017/12/25.
 */
public class HttpClientUtil {
    private static final Logger log = LoggerFactory.getLogger(com.seassoon.sixiang.utils.HttpClientUtil.class);


    /***
     * 连接超时,建立链接超时时间,毫秒.
     */
    private static final int CONN_TIMEOUT = 800000;
    /**
     * 响应超时,响应超时时间,毫秒.
     */
    private static final int READ_TIMEOUT = 800000;
    /**
     * https 请求方式
     */
    private static String https = "https";

    /**
     * 初始化client
     */
    private static HttpClient client;

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(128);
        cm.setDefaultMaxPerRoute(128);
        client = HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * Https Post
     * <p>
     * 默认：
     * UTF-8编码
     * 参数类型 json
     *
     * @param url          serverUrl
     * @param parameterStr 参数json串
     * @return 请求结果
     */
    public static String doPost(String url, String parameterStr) {
        return sendPostSSLRequest(url, parameterStr, Consts.UTF_8.name(), "application/json");
    }

    /**
     * Https Post
     *
     * @param url          serverUrl
     * @param parameterStr 参数json串
     * @param charset      指定字符编码
     * @param mimeType     请求对象MIME类型
     * @return 请求结果
     */
    public static String doPost(String url, String parameterStr, String charset, String mimeType) {
        return sendPostSSLRequest(url, parameterStr, charset, mimeType);
    }

    /**
     * https or http Get
     * 默认UTF-8编码
     *
     * @param url serverUrl
     * @return 请求结果
     */
    public static String doGet(String url) {
        return sendGetSSLRequest(url, Consts.UTF_8.name());
    }

    /**
     * https or http Get
     * 自定义字符编码
     *
     * @param url     serverUrl
     * @param charset 指定字符编码
     * @return 请求结果
     */
    public static String doGet(String url, String charset) {
        return sendGetSSLRequest(url, charset);
    }

    /**
     * 发送一个 Post 请求, 使用指定的字符集编码.
     *
     * @param url      地址
     * @param body     RequestBody
     * @param charset  编码
     * @param mimeType 例如 application/xml "application/x-www-form-urlencoded" a=1&b=2&c=3
     * @return 结果
     */
    private static String sendPostSSLRequest(String url, String body, String charset, String mimeType) {
        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        String result = "通信失败";
        try {
            if (StringUtils.isNotBlank(body)) {
                HttpEntity entity = new StringEntity(body, ContentType.create(mimeType, charset));
                post.setEntity(entity);
            }
            RequestConfig customReqConf = getRequestConfig();
            post.setConfig(customReqConf);
            client = getClient(url);
            HttpResponse res = client.execute(post);
            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } catch (Exception e) {
            log.error("请求失败", e);
        } finally {
            post.releaseConnection();
            closeClient(url, client);
        }
        return result;
    }


    /**
     * 提交form表单
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param headers 请求头
     * @return 响应
     * @throws Exception 连接异常
     */
    public static String postForm(String url, Map<String, String> params, Map<String, String> headers) throws Exception {

        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> formParams = new ArrayList<>();
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
                post.setEntity(entity);
            }

            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置参数
            RequestConfig customReqConf = getRequestConfig();

            post.setConfig(customReqConf);
            client = getClient(url);
            HttpResponse res = client.execute(post);
            return IOUtils.toString(res.getEntity().getContent(), Consts.UTF_8);
        } finally {
            post.releaseConnection();
            if (url.startsWith(https) && (client instanceof CloseableHttpClient)) {
                ((CloseableHttpClient) client).close();
            }
        }
    }

    /**
     * 发送一个 GET 请求(https or http)
     *
     * @param url     url
     * @param charset 字符编码
     * @return 结果
     */
    private static String sendGetSSLRequest(String url, String charset) {

        HttpClient client = null;
        HttpGet get = new HttpGet(url);
        String result = "通信失败";
        try {
            // 设置参数
            RequestConfig customReqConf = getRequestConfig();
            get.setConfig(customReqConf);
            HttpResponse res;
            client = getClient(url);
            res = client.execute(get);
            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } catch (Exception e) {
            log.error("请求失败", e);
        } finally {
            get.releaseConnection();
            closeClient(url, client);
        }
        return result;
    }

    /**
     * 关闭client
     *
     * @param url    只关闭 https ?
     * @param client client
     */
    private static void closeClient(String url, HttpClient client) {
        if (url.startsWith(https) && client instanceof CloseableHttpClient) {
            try {
                ((CloseableHttpClient) client).close();
            } catch (IOException e) {
                log.error("关闭失败", e);
            }
        }
    }


    /**
     * 设置请求配置对象
     *
     * @return RequestConfig
     */
    private static RequestConfig getRequestConfig() {
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectTimeout(CONN_TIMEOUT);
        builder.setSocketTimeout(READ_TIMEOUT);
        return builder.build();
    }

    /**
     * 根据url获取client
     *
     * @param url 请求url
     * @return HttpClient
     * @throws Exception 创建ssl连接异常
     */
    private static HttpClient getClient(String url) throws Exception {
        if (url.startsWith(https)) {
            return createSSLInsecureClient();
        } else {
            return com.seassoon.sixiang.utils.HttpClientUtil.client;
        }
    }


    /**
     * 创建 SSL连接
     *
     * @return 建立 连接result
     */
    private static CloseableHttpClient createSSLInsecureClient() throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true).build();
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext, (s, sslSession) -> true);
        return HttpClients.custom().setSSLSocketFactory(factory).build();
    }

    /**
     * 发送一个 Post 请求, 传输流的方式
     *
     * @param url         地址
     * @param inputStream 文件
     * @param fileName    文件名称
     * @param param       参数名
     * @return 结果
     */
    public static String doPostByFile(InputStream inputStream, String fileName, String url, String param) throws Exception {
        byte[] fileBytes = FileUtils.readStream(inputStream);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            MultipartEntityBuilder entity = MultipartEntityBuilder.create();
            entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.setCharset(Charset.forName("UTF-8"));
            if (fileBytes != null) {
                ContentType OCTEC_STREAM = ContentType.create("application/octet-stream", Charset.forName("UTF-8"));
                //添加文件
                entity.addBinaryBody(param, fileBytes, OCTEC_STREAM, fileName);
            }
            httpPost.setEntity(entity.build());
            //发起请求，并返回请求响应
            response = httpClient.execute(httpPost);
            String uploadResult = IOUtils.toString(response.getEntity().getContent(), Consts.UTF_8);
            return uploadResult;
        } catch (Exception e) {
            log.error("文件上传错误", e);
        }
        return "文件上传错误";
    }


}