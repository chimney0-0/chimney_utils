import com.alibaba.fastjson.JSONArray;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TTEst {

	public static void main(String[] args) {
//		Comparable c = null;
//		System.out.println((String) c);
//		System.out.println(Math.pow(2,23));
		//System.out.println(new Date(1525828691265L));
//		boolean[] isEmpty = new boolean[10];
//		System.out.println(isEmpty);
//		Long t = Long.valueOf("aa");

//		String path = "/metastore/tenant/meta/instance/schema/t165";
//		String[] strings =  path.split("/");
//		System.out.println(strings[0]);

//		System.out.println(System.currentTimeMillis());

//		System.out.println(new Date(1541001487L * 1000));

//		System.out.println(Pattern.matches("^^(\\(\\d{3,4}-)|(\\d{3,4}-)?\\d{7,8}$", "12345"));

//		Double d = 93.0;
//		System.out.println(d.intValue());

//		String str = "[]";
//		JSONArray jsonArray = JSONArray.parseArray(str);
//		System.out.println();

//		String DATATIME_FORMATS_REGX="^(?:19|20)[0-9][0-9]-(?:(?:0[1-9])|(?:1[0-2]))-(?:(?:[0-2][1-9])|(?:[1-3][0-1])) (?:(?:[0-2][0-3])|(?:[0-1][0-9])):[0-5][0-9]:[0-5][0-9]$";

//		String line = "foo,bar,c;qual=\"baz,blurb\",d;junk=\"quux,syzygy\"";
////		String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
//		String[] tokens = line.split("\",\"", -1);
//		for(String t : tokens) {
//			System.out.println("> "+t);
//		}

//		String result = "{\n" +
//				"    \"success\": true,\n" +
//				"    \"message\": \"查询成功\",\n" +
//				"    \"timestamp\": 1536750090395,\n" +
//				"    \"resultCode\": \"200\",\n" +
//				"    \"data\": {\n" +
//				"        \"timestampFrom\": \"2018-09-01 00:00\",\n" +
//				"        \"timestampTo\": \"2018-09-12 00:00\",\n" +
//				"        \"list\": [\n" +
//				"            {\n" +
//				"                \"tenantName\": \"test123\",\n" +
//				"                \"elements\": []\n" +
//				"            },\n" +
//				"            {\n" +
//				"                \"tenantName\": \"krbser\",\n" +
//				"                \"elements\": []\n" +
//				"            }\n" +
//				"        ]\n" +
//				"    }\n" +
//				"}";
//		System.out.println(result);

		/*
		String str = "你好呀";

		byte[] buff = str.getBytes();

		System.out.println(buff);

		int i = buff.length;

		System.out.println(i);

*/


//		Integer total = 9374616;
//		ObjectMapper MAPPER = new ObjectMapper();
//		try {
//			String str = MAPPER.writeValueAsString(new ArrayList<>());
//			System.out.println(str);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}

//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("D:\\项目资料\\上汽\\数据\\样例数据格式.txt")), "utf-8"));
//			String line;
//			while (( line= reader.readLine()) != null){
//				System.out.print(line+", ");
//			}
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

//		String str = "保监罚〔2010〕10号 　　当事人：庞玉超 　　身份证号：370303196911181735 　　经查明，你存在以下违法行为： 　　2009年5月4日，在未取得我会批准的情况下，美亚财产保险有限公司及其上海分公司擅自完成营业场所的搬迁工作。你作为迁址项目的项目经理，对此负有一定直接责任。 　　上述违法事实行为，违反了《中华人民共和国保险法》（2002年修正）第八十二条的规定，依据《中华人民共和国保险法》（2002年修正）第一百五十条的规定，我会决定对你给予警告的行政处罚。 　　如对本处罚决定不服，可在接到本处罚决定之日起60日内，依法向中国保监会申请行政复议或在3个月内向有管辖权的人民法院提起行政诉讼。复议和诉讼期间，上述决定不停止执行。 　　　　　　　　　　　　　　　　　　　　　　　　　　中国保险监督管理委员会　　　　　　　　　　　　　　　　　　　　　　　　　　　　二○一○年一月七日 　　";
//		String str2 = "　";
//		Pattern pattern = Pattern.compile("[\\p{Punct}]");
//		Matcher matcher = pattern.matcher(str);
//		while (matcher.find()){
//			System.out.println(matcher.group());
//		}
//		System.out.println(str);

//		String str = " 吴瑶菲";
//		String regEx = "^《.*?》.*?[\\u4e00-\\u9fa5]$|^[\\u4e00-\\u9fa5].*?[\\u4e00-\\u9fa5]$|^《.*?》$|^《.*?》.*?“.+”$|^[\\u4e00-\\u9fa5].*?“.+”]$";
//		System.out.println(str.matches(regEx));

//		String str = "连益雄（深圳市坪山新区佳好宜多百货店\n" +
//				"）销售超过保质期的食品案";
//		System.out.println(str.replaceAll("\\s", ""));

//		List list1 = Arrays.asList(new Integer[]{1,2,3,4,5});
//
//		List list2= list1.subList(0,0);

//		String sql = "insert overwrite table sdtmp_xdc_fctr_mid_ch_batt_time partition (pt='${spt_date}')\n" +
//				"select\n" +
//				"    vin as vin,\n" +
//				"    date_time as daytime,\n" +
//				"    ch_vehbatt as ch_vehbatt,\n" +
//				"    lag(tmp.ch_vehbatt) over (partition by tmp.vin order by tmp.ch_starttime) as lag_ch_vehbatt,\n" +
//				"    ch_starttime as ch_starttime,\n" +
//				"    lag(tmp.ch_starttime) over (partition by tmp.vin order by tmp.ch_starttime) as lag_ch_starttime\n" +
//				"from (\n" +
//				"    select\n" +
//				"        t.vin as vin,\n" +
//				"        '${spt_date}' as date_time,\n" +
//				"        (case when t.vehsyspwrmod=0 and\n" +
//				"                   lead(t.vehsyspwrmod) over (partition by t.vin order by t.starttime)!=0 and\n" +
//				"                   lead(t.vehsyspwrmod) over (partition by t.vin order by t.starttime) is not null\n" +
//				"              then t.vehbatt\n" +
//				"              else null\n" +
//				"        end) as ch_vehbatt,\n" +
//				"        (case when t.vehsyspwrmod=0 and\n" +
//				"                   lead(t.vehsyspwrmod) over (partition by t.vin order by t.starttime)!=0 and\n" +
//				"                   lead(t.vehsyspwrmod) over (partition by t.vin order by t.starttime) is not null\n" +
//				"              then t.starttime\n" +
//				"              else null\n" +
//				"        end) as ch_starttime\n" +
//				"    from sdtmp_zxq_sd_xudianc_mid_spt t\n" +
//				"    where t.spt between ${spt_date}00 and ${spt_date}23) tmp\n" +
//				"where tmp.ch_vehbatt is not null and tmp.ch_starttime is not null";
//		Matcher matcher = Pattern.compile("(?<=\\$\\{[a-zA-Z_]+})([0-9]{1,2})").matcher(sql);
//		if (matcher.find()){
//			sql = matcher.replaceAll("");
//		}
//		System.out.println(sql);

		String str = "　";
		String str2 = " ";

		System.out.println(str.matches("\\s"));

	}

}
