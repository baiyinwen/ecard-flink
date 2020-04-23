package com.ecard.bigdata.utils;

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

	public static String customDateTime(int diff, String format) {

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, diff);
		Date date = cal.getTime();

		return sdf.format(date);
	}

	public static String formatDateTime(Date date, String format) {

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static Date parseDateTime(String dateTime, String format) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(dateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static Timestamp toTimestamp(String dateTime, String format) {

		return new Timestamp(parseDateTime(dateTime, format).getTime());
	}

	public static String toDateTime(long timestamp, String format) {

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(timestamp);
	}

}
