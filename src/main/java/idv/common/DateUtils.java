package idv.common;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Title DateUtils
 * Description
 * Copyright (c) 2019
 * Company  上海思贤信息技术股份有限公司
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/7/30 17:17
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    /**
     * 获取当前日期(时间 23:59:59)
     *
     * @return
     * @ author sys
     */
    public static Date getTodayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        return todayEnd.getTime();
    }

    public static String getDateTimeDay() {
        return DateUtils.getDateTime("yyyyMMdd");
    }

    /**
     * 获取系统当前时间
     *
     * @return
     * @ formatStyle  时间格式
     * @ author sys
     */
    public static String getDateTime(String formatStyle) {
        if (formatStyle == null || "".equals(formatStyle)) {
            formatStyle = "yyyyMMddHHmmss";
        }
        SimpleDateFormat myFormat = new SimpleDateFormat(formatStyle);
        return myFormat.format(new Date());
    }

    /**
     * 获取当前日期(时间 00:00:00)
     *
     * @return
     * @ author sys
     */
    public static Timestamp getDateFirst() {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar calendar = Calendar.getInstance();
        String dateStr = myFormat.format(calendar.getTime());
        return Timestamp.valueOf(dateStr);
    }

    /**
     * 获取当前日期(时间 23:59:59)
     *
     * @return
     * @ author sys
     */
    public static Timestamp getDateLast() {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        Calendar calendar = Calendar.getInstance();
        String dateStr = myFormat.format(calendar.getTime());
        return Timestamp.valueOf(dateStr);
    }

    /**
     * 获取昨日开始时间 00:00:00
     *
     * @return
     */
    public static Timestamp getYesterdayBegin() {
        long today = getDateFirst().getTime();
        long yesterday = today - (24 * 60 * 60 * 1000 - 1);
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = myFormat.format(yesterday);
        return Timestamp.valueOf(dateStr);
    }

    /**
     * 获取昨日最后时间- 23:59:59
     *
     * @return
     */
    public static Timestamp getYesterdayEnd() {
        long today = getDateLast().getTime();
        long yesterday = today - (24 * 60 * 60 * 1000 - 1);
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = myFormat.format(yesterday);
        return Timestamp.valueOf(dateStr);
    }

    public static Date timeStampTurnDate(long timeLong) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(timeLong);
        try {
            Date date = format.parse(d);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
