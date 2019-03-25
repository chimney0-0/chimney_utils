package utils.time;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @Description: 解决Java读取Excel变成数字情况
 * @author: Leon
 * @date:2018年11月12日 上午10:58:33
 * @Copyright: 2018 www.seassoon.com Inc. All rights reserved.
 *             注意：本内容仅限于上海思贤信息技术股份有限公司内部传阅，禁止外泄以及用于其他的商业行为
 */
public class DateUtil {

	/**
	 * 
	 * @Description: 把数字类型的Excel日期转为标准的yyyy-MM-dd格式 @param @param
	 *               oldDate @param @return 参数 @return String 返回类型 @throws
	 */
	public static String getDate(String oldDate) {
		if (StringUtils.isNotBlank(oldDate)) {
			oldDate = DateUtil.replaceBlank(oldDate);
			// 格式校验yyyy-MM-dd
			if (oldDate.contains("-")) {
				try {
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					return sdf1.format(sdf1.parse(oldDate));
				} catch (ParseException e) {
				}
				return oldDate;
			}
			if(oldDate.contains("/")) {
				try {
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
					Date newDate = sdf2.parse(oldDate);
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					return sdf1.format(newDate);
				} catch (ParseException e) {
				}
			}
			// 校验格式yyyy年MM月dd日
			if (oldDate.contains("年")) {
				try {
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日");
					Date newDate = sdf2.parse(oldDate);
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					return sdf1.format(newDate);
				} catch (ParseException e) {
				}
			}
			// 校验格式yyyy年MM月dd日
			if (oldDate.contains("年")) {
				try {
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年M月dd日");
					Date newDate = sdf2.parse(oldDate);
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					return sdf1.format(newDate);
				} catch (ParseException e) {
				}
			}

			// 格式校验yyyy.MM.dd
			if (oldDate.contains(".")) {
				SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy.MM.dd");
				try {
					Date newDate = sdf3.parse(oldDate);
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					return sdf1.format(newDate);
				} catch (ParseException e) {

				}
			}
			// 格式校验yyyy-MM-dd
			try {
				Double a = Double.parseDouble(oldDate);
				if (a > 50000) {
//					oldDate = DateUtil.getDoubleStr(a);
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
					Date newDate = sdf2.parse(oldDate);
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					return sdf1.format(newDate);
				}
			} catch (ParseException e1) {

			}
			// 数字类型4万多
			try {
				Double parseDouble = Double.parseDouble(oldDate);
				if (null != parseDouble) {
					int num = parseDouble.intValue();
					Calendar c = new GregorianCalendar(1900, 0, -1);
					Date d = c.getTime();
					Date newDate = DateUtils.addDays(d, num); // num是距离1900年1月1日的天数
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					return sdf1.format(newDate);
				}
			} catch (NumberFormatException e) {
				System.out.println("非数字类型日期");
			}
		}
		return oldDate;
	}

	/**
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param d
	 * @param @return    参数
	 * @return String    返回类型
	 * @throws
	 */
	public static String getDoubleStr(Double d) {
		DecimalFormat format = (DecimalFormat) NumberFormat.getPercentInstance();
		format.applyPattern("#####0 ");
		String temp = format.format(2.018071E7);
		return temp;
	}
	
	/**
	 * 替换掉换行符，
	 * @param @param str
	 * @param @return    参数
	 * @return String    返回类型
	 * @throws
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n|");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	public static String convertCnDate(String cprq) {
		int yearPos = cprq.indexOf("年");
		int monthPos = cprq.indexOf("月");
		String cnYear = cprq.substring(0, yearPos);
		String cnMonth = cprq.substring(yearPos + 1, monthPos);
		String cnDay = cprq.substring(monthPos + 1, cprq.length() - 1);
		String year = ConvertCnYear(cnYear);
		String month = ConvertCnDateNumber(cnMonth);
		String day = ConvertCnDateNumber(cnDay);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(year));
		c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
		return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
	}
	public static String convertDate(String dateStr) throws Exception {
		Pattern p=Pattern.compile("([0-9]{4})[年]([0-9]{1,2})[月]([0-9]{1,2})[日]\\s*([0-9]{0,2})[\\S]?([0-9]{0,2})");
		Matcher mat =p.matcher(dateStr);
		if(!mat.find()) {
			throw new Exception("格式不对"); 
		}
		String cnYear = mat.group(1);
		String cnMonth = mat.group(2);
		String cnDay =  mat.group(3);
		String year = ConvertCnYear(cnYear);
		String month = ConvertCnDateNumber(cnMonth);
		String day = ConvertCnDateNumber(cnDay);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(year));
		c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
		return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
	}
	
	

	private static String ConvertCnYear(String cnYear) {
		if (cnYear.length() == 2) return "20" + ConvertCnNumberChar(cnYear);
		else {
			return ConvertCnNumberChar(cnYear);
		}
	}

	private static String ConvertCnDateNumber(String cnNumber) {
		if (cnNumber.length() == 1) {
			if(cnNumber.equals("十")){
				return "10";
			}
			if(cnNumber.equals("元")){
				return "1";
			}
			return ConvertCnNumberChar(cnNumber);
		} else if (cnNumber.length() == 2) {
			if (cnNumber.startsWith("十")) {
				return "1" + ConvertCnNumberChar(cnNumber.substring(1, 2));
			} else if (cnNumber.endsWith("十")) {
				return ConvertCnNumberChar(cnNumber.substring(0, 1)) + "0";
			} else {
				return ConvertCnNumberChar(cnNumber);
			}
		} else if (cnNumber.length() == 3) {
			return ConvertCnNumberChar(cnNumber.substring(0, 1) + cnNumber.substring(2, 3));
		}
		return null;
	}

	private static String ConvertCnNumberChar(String cnNumberStr) {
		String ALL_CN_NUMBER = "ОΟＯ〇○O零一二三四五六七八九";
		String ALL_NUMBER = "0000000123456789";
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < cnNumberStr.length(); i++) {
			char c = cnNumberStr.charAt(i);
			int index = ALL_CN_NUMBER.indexOf(c);
			if (index != -1) {
				buf.append(ALL_NUMBER.charAt(index));
			} else {
				buf.append(cnNumberStr.charAt(i));
			}
		}
		return buf.toString();
	}

	
	public static void main(String[] args) {
		String str = "2018年10月31日 16:48";
		try {
			System.out.println(DateUtil.convertDate(str));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
