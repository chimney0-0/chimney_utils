import com.alibaba.fastjson.JSONObject;
import com.jfinal.json.Json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonTest {


    public static void main(String[] args) {

//        String text = "{\"title\":\"pinjie\",\"nodes\":{\"1530172303447\":{\"name\":\"test1x6.csv\",\"left\":67,\"top\":86,\"type\":\"add_box\",\"width\":160,\"height\":36,\"style\":\"fileInput\",\"nodeKey\":\"1530172303447\",\"alt\":true,\"rid\":499,\"path\":\"/user/root/suichao/ypx_test/原始数据/test1x6.csv\",\"outputData\":[{\"name\":\"_c0\",\"type\":null,\"remark\":null},{\"name\":\"_c1\",\"type\":null,\"remark\":null},{\"name\":\"_c2\",\"type\":null,\"remark\":null},{\"name\":\"_c3\",\"type\":null,\"remark\":null},{\"name\":\"_c4\",\"type\":null,\"remark\":null},{\"name\":\"_c5\",\"type\":null,\"remark\":null}]},\"1530172322327\":{\"name\":\"test1x6.csv\",\"left\":66,\"top\":255,\"type\":\"add_box\",\"width\":160,\"height\":36,\"style\":\"fileInput\",\"nodeKey\":\"1530172322327\",\"alt\":true,\"rid\":499,\"path\":\"/user/root/suichao/ypx_test/原始数据/test1x6.csv\",\"outputData\":[{\"name\":\"_c0\",\"type\":null,\"remark\":null},{\"name\":\"_c1\",\"type\":null,\"remark\":null},{\"name\":\"_c2\",\"type\":null,\"remark\":null},{\"name\":\"_c3\",\"type\":null,\"remark\":null},{\"name\":\"_c4\",\"type\":null,\"remark\":null},{\"name\":\"_c5\",\"type\":null,\"remark\":null}]},\"1530172328607\":{\"name\":\"拼接_5\",\"left\":330,\"top\":171,\"type\":\"grid_on\",\"width\":160,\"height\":36,\"style\":\"splice\",\"nodeKey\":\"1530172328607\",\"alt\":true,\"inputData\":{\"1530172303447\":[{\"name\":\"_c0\",\"targetName\":\"_c0\"}]},\"outputData\":[{\"name\":\"_c0\",\"type\":null,\"remark\":null}],\"mainNodeKey\":1530172303447},\"1530172386351\":{\"name\":\"输出文件_9\",\"left\":617,\"top\":184,\"type\":\"system_update_alt\",\"width\":160,\"height\":36,\"style\":\"fileOutput\",\"nodeKey\":\"1530172386351\",\"alt\":true,\"outputData\":[{\"name\":\"_c0\",\"type\":null,\"remark\":null}],\"path\":\"/user/root/suichao/ypx_test/探索数据/uuuuu\",\"fileName\":\"uuuuu\",\"dicPath\":\"ypx_test/探索数据\"}},\"lines\":{\"1530172333207\":{\"type\":\"sl\",\"from\":\"1530172303447\",\"to\":\"1530172328607\",\"name\":\"\"},\"1530172334775\":{\"type\":\"sl\",\"from\":\"1530172322327\",\"to\":\"1530172328607\",\"name\":\"\"},\"1530172390495\":{\"type\":\"sl\",\"from\":\"1530172328607\",\"to\":\"1530172386351\",\"name\":\"\"}},\"areas\":{},\"initNum\":11,\"id\":143,\"type\":\"workflow\"}\n";
//
//        System.out.println(text.substring(25, 27));
//
//        JSONObject object = JSONObject.parseObject(text);

//        String[] strings = "1853,,14".split(",");

//        List<String> list = Arrays.asList(strings);

//        System.out.println(strings.length);

        List<String> list = new ArrayList<>();
        list.add("ss,ss");
        list.add("");
        list.add(null);

        String[] strings = list.toArray(new String[list.size()]);

        List<String> newList = Arrays.asList(strings);

        System.out.println(newList.contains(""));

        System.out.println(strings.length);

    }

}
