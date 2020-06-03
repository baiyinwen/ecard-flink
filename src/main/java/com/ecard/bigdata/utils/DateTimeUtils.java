package com.ecard.bigdata.utils;

import com.ecard.bigdata.constants.CONFIGS;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *@Description 日期时间工具类
 *@Author WangXuedong
 *@Date 2019/9/20 11:52
 **/
public class DateTimeUtils {

	/**
	 * @Description 获取自定义时间
	 * @Param diff 距今相差天数：-1、0、1...
	 * @Param format 时间格式：yyyy-MM-dd HH:mm:ss...
	 * @Return String
	 * @Author WangXueDong
	 * @Date 2019/9/20 11:52
	 **/
	public static String customDateTime(int diff, String format) {

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, diff);
		Date date = cal.getTime();

		return sdf.format(date);
	}

	/**
	 * @Description 格式化时间
	 * @Param date
	 * @Param format
	 * @Return java.lang.String
	 * @Author WangXueDong
	 * @Date 2019/9/20 11:52
	 **/
	public static String formatDateTime(Date date, String format) {

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * @Description 解析时间
	 * @Param dateTime
	 * @Param format
	 * @Return java.util.Date
	 * @Author WangXueDong
	 * @Date 2019/9/20 11:52
	 **/
	public static Date parseDateTime(String dateTime, String format) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(dateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	/**
	 * @Description 字符串转时间戳
	 * @Param dateTime
	 * @Param format
	 * @Return java.sql.Timestamp
	 * @Author WangXueDong
	 * @Date 2020/4/23 13:49
	 **/
	public static Timestamp toTimestamp(String dateTime, String format) {

		return new Timestamp(parseDateTime(dateTime, format).getTime());
	}

	/**
	 * @Description 时间戳转字符串
	 * @Param timestamp 
	 * @Param format 
	 * @Return String
	 * @Author WangXueDong
	 * @Date 2020/4/23 15:36
	 **/
	public static String toDateTime(long timestamp, String format) {

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(timestamp);
	}

	public static Timestamp getIntervalBasicTime(Timestamp eventTime){

		long interval = ConfigUtils.getLong(CONFIGS.SIGN_TUMBLING_WINDOW_SIZE) * 1000;
		long basicDateTime = eventTime.getTime();
		if (interval < 60*60*1000) {
			basicDateTime = basicDateTime - basicDateTime % interval;
		}
		return new Timestamp(basicDateTime);
	}

}
