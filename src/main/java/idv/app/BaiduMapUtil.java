package idv.app;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 百度地图接口
 * 支持功能：地理编码
 */
public class BaiduMapUtil {
    //企业认证开发者ak
    private static final String ak = "YpIXI7DFzRku28zn45pkNC3xOzrppmio";
    //坐标系类型常量
    public static final String GCG = "gcj02ll";//国测局经纬度坐标
    public static final String BD = "bd09ll";//百度经纬度坐标
    public static final String BDM = "bd09mc";//百度墨卡托坐标

    //坐标系类型，默认为百度经纬度坐标
    private String coordtype = "bd09ll";

    public void setCoordtype(String coordtype) {
        this.coordtype = coordtype;
    }


    public static void main(String[] args) {
        // 使用方法参考
        System.out.println(new BaiduMapUtil().geoCoder("上海市淮海西路55号", null));
    }


    /**
     * 地理编码接口 返回json格式的字符串
     *
     * @param address
     * @return
     * @throws Exception
     */
    public String geoCoder(String address, String city) {
        try {
            String url = "http://api.map.baidu.com/geocoder/v2/?address=" + URLEncoder.encode(address, "UTF-8")
                    + "&output=json&ak=" + ak + "&ret_coordtype=" + coordtype;
            if (city != null && !city.equals("")) url = url + "&city=" + city;
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Connection", "close");
            CloseableHttpResponse response = httpclient.execute(httpGet);
            return EntityUtils.toString(response.getEntity());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


}
