package com.caipiao.common.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 时间工具类
 * Created by kouyi on 2017/9/30.
 */
public abstract class DateUtil {
    public static final String CHINESE_DATE_TIME = "yyyy年MM月dd日 HH:mm";
    public static final String CHINESE_DATE_TIME_SECOND = "yyyy年MM月dd日 HH:mm:ss";
    public static final String CHINESE_DATE = "yyyy年MM月dd日";
    public static final String CHINESE_MONTH_DATE = "MM月dd日";
    public static final String CHINESE_MONTH_TIME = "MM月dd日 HH:mm";
    public static final String CHINESE_HOUR_TIME = "HH:mm";
    public static final String DEFAULT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_TIME_SECOND = "yyyy-MM-dd HH:mm";
    public static final String DEFAULT_DATE_TIME_HOUR = "yyyy-MM-dd HH";
    public static final String DEFAULT_DATE = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_SECOND = "MM-dd";
    public static final String DEFAULT_TIME = "HH:mm:ss";
    public static final String DEFAULT_TIME1 = "HHmmss";
    public static final String LOG_DATE_TIME = "yyyyMMddHHmmssSSS";
    public static final String LOG_DATE_TIME2 = "yyyyMMddHHmmss";
    public static final String DEFAULT_DATE0 = "yyyy";
    public static final String DEFAULT_DATE1 = "yyyyMMdd";
    public static final String DEFAULT_DATE2 = "yyyy-MM";
    public static final String YM_NOSYMBOL_FORMAT = "yyyyMM";
    public static final String HM_FORMAT = "HH:mm";
    public static final String DHM_FORMAT = "dd日HH:mm";
    public static final String MDHMS_FORMAT = "MM-dd HH:mm:ss";
    public static final String MDHM_FORMAT = "MM-dd HH:mm";

    public static final long DURATION = 1000L * 60 * 60 * 24;//一天
    public static final long DURATION_HOUR = 1000L * 60 * 60;//一小时
    public static final String[] weeks = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    /**
     * 计算两个日期的时间差(after-before)-返回具体的天数
     *
     * @param before
     * @param after
     * @return
     *      异常则返回-1
     */
    public static int daysBetween(Date before, Date after) {
        try {
            return (int)((after.getTime() - before.getTime()) / DURATION);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 计算两个日期的时间差(after-before)-返回具体的小时数
     *
     * @param before
     * @param after
     * @return
     *      异常则返回-1
     */
    public static int hoursBetween(Date before, Date after) {
        try {
            return (int) ((after.getTime() - before.getTime()) / DURATION_HOUR);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 计算两个日期的时间差(after-before)-返回具体的分钟数
     *
     * @param before
     * @param after
     * @return
     *      异常则返回-1
     */
    public static int minutesBetween(Date before, Date after) {
        try {
            return (int) ((after.getTime() - before.getTime()) / (60 * 1000));
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 计算两个日期的时间差(after-before)-返回具体的秒数
     *
     * @param before
     * @param after
     * @return
     *      异常则返回-1
     */
    public static int secondsBetween(Date before, Date after) {
        try {
            return (int) ((after.getTime() - before.getTime()) / 1000L);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 根据生日计算当前年龄
     * @param birthDay
     * @return
     */
    public static int getAge(String birthDay) {
        Date aDate = null;
        if (aDate != null) {
            Calendar today = Calendar.getInstance();
            Calendar birthday = Calendar.getInstance();
            birthday.setTime(aDate);
            int yearTotal = today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
            int bDay = today.get(Calendar.DAY_OF_YEAR) - birthday.get(Calendar.DAY_OF_YEAR);
            //not yet match 1 round age, minus one year
            if (bDay < 0) {
                yearTotal = yearTotal - 1;
            }
            return yearTotal;
        }
        return 0;
    }

    /**
     * 字符串转换时间类型
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static Date dateFormat(String dateStr, String format) {
        try {
            return new SimpleDateFormat(format).parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 时间类型转换字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateFormat(Date date, String format) {
        try {
            return new SimpleDateFormat(format).format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 字符串转时间类型-默认yyyy-MM-dd HH:mm:ss
     * @param dateStr
     * @return
     */
    public static Date dateDefaultFormat(String dateStr) {
        try {
            return dateFormat(dateStr.trim(), DEFAULT_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 字符串转时间类型-默认yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String dateDefaultFormat(Date date) {
        try {
            return dateFormat(date, DEFAULT_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 比较两个时间是否相等
     * @param date
     * @param date2
     * @return
     */
    public static boolean isDateEqualTo(Date date, Date date2) {
        try {
            return date.getTime() == date2.getTime();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断date是否大于等于开始日期,小于等于结束日期
     * @param date
     * @param before
     * @param after
     * @return
     */
    public static boolean isDateInRange(Date date, Date before, Date after) {
        try {
            return (date.after(before) && date.before(after)) || date.compareTo(before) == 0 || date.compareTo(after) == 0;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 将date时间加second秒
     *
     * @param date
     * @param second
     * @return
     */
    public static Date addSecond(Date date, int second) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.SECOND, second);
            return cal.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将date时间加minute分钟
     *
     * @param date
     * @param minute
     * @return
     */
    public static Date addMinute(Date date, int minute) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MINUTE, minute);
            return cal.getTime();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 将date时间加hour小时
     *
     * @param date
     * @param hour
     * @return
     */
    public static Date addHour(Date date, int hour) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.HOUR, hour);
            return cal.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将date时间加days天
     *
     * @param date
     * @param days
     * @return
     */
    public static Date addDay(Date date, int days) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, days);
            return cal.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据创建时间与当前时间对比返回一个时间标签
     * 1.发布时间在1小时内的,显示格式为:**分钟前
     * 2.发布时间在10小时内的，显示格式为:**小时前
     * 2.发布时间为本日，1小时前，显示格式为:今天 16:01
     * 3.发布时间为非本日，显示格式为：*月*日 16:09
     * 4.发布时间为非本年，显示格式为：*年*月*日 16:09
     *
     * @param date
     * @return String
     */
    public static String getTimeStr(Date date) {
        try {
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTime(date);
            Calendar currCal = Calendar.getInstance();
            int yearBetween = currCal.get(Calendar.YEAR) - dateCal.get(Calendar.YEAR);
            int dayBetween = currCal.get(Calendar.DAY_OF_YEAR) - dateCal.get(Calendar.DAY_OF_YEAR);
            int hourBetween = hoursBetween(dateCal.getTime(), currCal.getTime());
            int minuteBetween = minutesBetween(dateCal.getTime(), currCal.getTime());
            if(yearBetween > 0) {
                return dateFormat(date, CHINESE_DATE_TIME);
            }
            if(dayBetween > 0) {
                return dateFormat(date, CHINESE_MONTH_TIME);
            }
            if(hourBetween > 0) {
                return "今天" + dateFormat(date, CHINESE_HOUR_TIME);
            }
            if(minuteBetween > 60) {
                return hourBetween <= 0 ? "1小时前" : (hourBetween + "小时前");
            }
            return minuteBetween <= 0 ? "1分钟前" : (hourBetween + "分钟前");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据创建时间与当前时间对比返回一个时间标签
     * 1.发布时间在1小时内,显示格式为:**分钟前
     * 2.发布时间在24小时内，显示格式为:**小时前
     * 2.发布时间为24小时外，显示格式为:**天前
     *
     * @param date
     * @return String
     */
    public static String getTimeStrTwo(Date date) {
        try {
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTime(date);
            Calendar currCal = Calendar.getInstance();
            int dayBetween = currCal.get(Calendar.DAY_OF_YEAR) - dateCal.get(Calendar.DAY_OF_YEAR);
            int hourBetween = hoursBetween(dateCal.getTime(), currCal.getTime());
            int minuteBetween = minutesBetween(dateCal.getTime(), currCal.getTime());
            if(dayBetween > 0) {
                return dayBetween + "天前";
            }
            if(hourBetween > 0) {
                return hourBetween + "小时前";
            }
            if(minuteBetween > 0) {
                return minuteBetween + "分钟前";
            }
            return "刚刚";
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取指定时间那周的周一日期
     * @param date
     * @return
     */
    public static String getFristDayOfWeek(Date date) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int week = cal.get(Calendar.DAY_OF_WEEK);
            if (week == 1) {
                cal.add(Calendar.DAY_OF_MONTH, -6);
            } else {
                cal.add(Calendar.DAY_OF_MONTH, 2 - week);
            }
            return dateFormat(cal.getTime(), DEFAULT_DATE);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取指定时间那周的周日日期
     * @param date
     * @return
     */
    public static String getLastDayOfWeek(Date date) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int week = cal.get(Calendar.DAY_OF_WEEK);
            if (week > 1)
                cal.add(Calendar.DAY_OF_MONTH, 8 - week);
            return dateFormat(cal.getTime(), DEFAULT_DATE);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取指定时间是周几
     * @param date
     * @return
     */
    public static String getWeekStr(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return weeks[dayOfWeek - 1];
    }

    /**
     * 获取指定时间是周几
     * @param date
     * @return
     */
    public static int getWeekInt(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK)-1;
    }

    /**
     * 返回当前年份
     * @return
     */
    public static int getCurYear() {
        return getYear(new Date());
    }

    /**
     * 返回指定时间对应年份
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * 返回当前月份
     * @return
     */
    public static int getCurMonth() {
        return getMonth(new Date());
    }

    /**
     * 返回指定时间对应月份
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 返回当前日
     * @return
     */
    public static int getCurday() {
        return getDay(new Date());
    }

    /**
     * 返回指定时间对应日
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 返回当前小时
     * @return
     */
    public static int getCurHour() {
        return getHour(new Date());
    }

    /**
     * 返回指定时间对应小时
     * @param date
     * @return
     */
    public static int getHour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 返回当前分钟
     * @return
     */
    public static int getCurMinute() {
        return getMinute(new Date());
    }

    //返回指定时间对应分钟
    public static int getMinute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }

    /**
     * 将日期转换为指定格式的日期字符串
     * @author   sjq
     * @param   date   日期
     * @param   format 格式,如："yyyy-MM-dd HH:mm:ss"
     */
    public static String formatDate(Date date,String format)
    {
        if (date == null)
        {
            return "";
        }
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * 将日期字符串转换成指定格式的日期
     * @author  mcdog
     * @param   dateString 日期字符串
     * @param   format     格式化模型,如："yyyy-MM-dd HH:mm:ss"
     */
    public static Date parseDate(String dateString, String format)
    {
        Date date = null;
        if (StringUtil.isNotEmpty(dateString))
        {
            try
            {
                DateFormat df = new SimpleDateFormat(format);
                date = df.parse(dateString);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 将日期字符串转换成指定格式的日期
     * @author  mcdog
     * @param   dateString 日期字符串
     * @param   format     格式化模型,如："yyyy-MM-dd HH:mm:ss"
     */
    public static Calendar parseCalendar(String dateString, String format)
    {
        Calendar calendar = null;
        if (StringUtil.isNotEmpty(dateString))
        {
            try
            {
                DateFormat df = new SimpleDateFormat(format);
                calendar = Calendar.getInstance();
                calendar.setTime(df.parse(dateString));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return calendar;
    }

    /**
     * 获取一年的天数
     * @author  mcdog
     * @param   year    年份
     */
    public static int getDayOfYear(int year)
    {
        int day = 365;
        if((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0))
        {
            day = 366;
        }
        return day;
    }

    /**
     * js时间格式格式化
     *
     * @author kouyi
     * @param time
     * @return
     */
    public static Date getDateTime(String time) {
        try {
            String[] temp = time.split(",");
            int M = Integer.valueOf(temp[1]) + 1;
            int d = Integer.valueOf(temp[2]);
            int h = Integer.valueOf(temp[3]);
            int m = Integer.valueOf(temp[4]);
            int s = Integer.valueOf(temp[5]);
            return dateDefaultFormat(temp[0] + "-" + M + "-" + d + " " + h + ":" + m + ":" + s);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查当前时间是否在times时间段之内
     * @param times
     *        0,6$09:05-23:59|0,1$00:00-00:55|1-5$09:05-23:55
     *        0,6$09:05-23:59|0,1$00:00-00:55|1,2,5$09:05-23:55|3,4$7:35-23:55
     * @return true=yes
     */
    public static boolean isContain(String times){
        boolean isc = false;
        try{
            if(StringUtils.isEmpty(times)){
                return true;
            }
            String[] timeSplit = StringUtils.split(times, "|");
            for(String time : timeSplit){
                isc = isIntervalTime(time);
                if(isc){
                    break;
                }
            }
        } catch (Exception e){
            //解析异常时返回true
        }
        return isc;
    }

    /**
     * 时间段字符串格式
     * @param timeExpression 周几(时间区间)，如0,6$09:05-23:59
     * @return
     */
    public static boolean isIntervalTime(String timeExpression) {
        try{
            if(StringUtils.isEmpty(timeExpression)){
                return true;
            }
            String[] times = StringUtils.splitPreserveAllTokens(timeExpression, "$");
            if(StringUtil.isEmpty(times) && times.length != 2) {
                return true;
            }
            String week = times[0];//周几
            if("*".equals(week)) {
                week = "0-6";
            }

            List<Integer> weeks = new ArrayList<>();
            if(week.indexOf("-") != -1){
                String[] wd = StringUtils.split(week, "-");
                int index = NumberUtils.toInt(wd[0]);
                int end = NumberUtils.toInt(wd[1]);
                for(int j=index; j<=end; j++) {
                    weeks.add(j);
                }
            }
            else if (week.indexOf(",") != -1) {
                String[] wd = StringUtils.split(week,",");
                for(int j=0; j<wd.length; j++ ) {
                    weeks.add(NumberUtils.toInt(wd[j]));
                }
            }
            else if(NumberUtils.isNumber(week)) {
                weeks.add(NumberUtils.toInt(week));
            }

            String time = times[1];//时间区间
            if("*".equals(time)){//无限制
                time = "00:00-23:59";
            }

            //开始时间点
            String[] date = StringUtils.split(time, "-");
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.set(Calendar.HOUR_OF_DAY, NumberUtils.toInt(StringUtils.split(date[0],":")[0]));
            startCalendar.set(Calendar.MINUTE, NumberUtils.toInt(StringUtils.split(date[0],":")[1]));
            startCalendar.set(Calendar.SECOND, 00);
            //结束时间点
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.set(Calendar.HOUR_OF_DAY,NumberUtils.toInt(StringUtils.split(date[1],":")[0]));
            endCalendar.set(Calendar.MINUTE,NumberUtils.toInt(StringUtils.split(date[1],":")[1]));
            endCalendar.set(Calendar.SECOND,00);

            int dayOfWeekIndex = getWeekInt(new Date());
            long curTime = new Date().getTime();
            if(weeks.contains(dayOfWeekIndex) && (curTime < endCalendar.getTime().getTime() && curTime > startCalendar.getTime().getTime())){
                return true;
            }
        } catch (Exception ex){
            //配置错误时返回false
        }
        return false;
    }

}
