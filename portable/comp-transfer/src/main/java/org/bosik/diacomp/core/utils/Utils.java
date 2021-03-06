/*
 * Diacomp - Diabetes analysis & management system
 * Copyright (C) 2013 Nikita Bosik
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bosik.diacomp.core.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import org.json.JSONArray;

public class Utils
{
	/**
	 * CONSTANTS
	 */

	// Energy values

	/**
	 * Value of proteins, kcal/g
	 */
	public static final double							KCAL_PER_PROTS		= 3.8;
	/**
	 * Value of fats, kcal/g
	 */
	public static final double							KCAL_PER_FATS		= 9.3;
	/**
	 * Value of carbohydrates, kcal/g
	 */
	public static final double							KCAL_PER_CARBS		= 4.1;

	// Time

	public static final int								MsecPerSec			= 1000;
	public static final int								SecPerMin			= 60;
	public static final int								MinPerHour			= 60;
	public static final int								HourPerDay			= 24;
	public static final int								SecPerHour			= SecPerMin * MinPerHour;
	public static final int								SecPerDay			= SecPerMin * MinPerHour * HourPerDay;
	public static final int								MinPerDay			= MinPerHour * HourPerDay;
	public static final int								HalfMinPerDay		= (MinPerHour * HourPerDay) / 2;
	public static final long							MsecPerMin			= MsecPerSec * SecPerMin;
	public static final long							MsecPerDay			= MsecPerSec * SecPerMin * MinPerHour
																					* HourPerDay;

	// Epsilon values

	public static final double							EPS					= 0.0000001;

	// Format settings

	public static char									DECIMAL_DOT;
	private static DecimalFormat						DF;

	private static Random								r					= new Random();

	// Formatters

	private static final String							FORMAT_DATE_TIME	= "yyyy-MM-dd HH:mm:ss";
	private static final String							FORMAT_DATE			= "yyyy-MM-dd";
	private static final String							FORMAT_TIME_SHORT	= "HH:mm";

	static final TimeZone								TIMEZONE_UTC		= TimeZone.getTimeZone("UTC");

	private static final ThreadLocal<SimpleDateFormat>	FORMATTER_DATE_UTC	= new ThreadLocal<SimpleDateFormat>()
																			{
																				@Override
																				protected SimpleDateFormat initialValue()
																				{
																					SimpleDateFormat format = new SimpleDateFormat(
																							FORMAT_DATE, Locale.US);
																					format.setTimeZone(TIMEZONE_UTC);
																					return format;
																				}
																			};

	private static final ThreadLocal<SimpleDateFormat>	FORMATTER_TIME_UTC	= new ThreadLocal<SimpleDateFormat>()
																			{
																				@Override
																				protected SimpleDateFormat initialValue()
																				{
																					SimpleDateFormat format = new SimpleDateFormat(
																							FORMAT_DATE_TIME, Locale.US);
																					format.setTimeZone(TIMEZONE_UTC);
																					return format;
																				}
																			};

	public static final String							ALPHANUMERIC		= "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";

	static
	{
		NumberFormat f = NumberFormat.getInstance(Locale.US);
		if (f instanceof DecimalFormat)
		{
			DF = (DecimalFormat)f;
			DECIMAL_DOT = DF.getDecimalFormatSymbols().getDecimalSeparator();
		}
		else
		{
			throw new RuntimeException("Number format is not a decimal format");
		}
	}

	private static SimpleDateFormat getFormatDateLocal(TimeZone timeZone)
	{
		SimpleDateFormat format = new SimpleDateFormat(FORMAT_DATE, Locale.getDefault());
		format.setTimeZone(timeZone);
		return format;
	}

	private static SimpleDateFormat getFormatTimeLocal(TimeZone timeZone)
	{
		SimpleDateFormat format = new SimpleDateFormat(FORMAT_DATE_TIME, Locale.getDefault());
		format.setTimeZone(timeZone);
		return format;
	}

	private static SimpleDateFormat getFormatTimeLocalShort(TimeZone timeZone)
	{
		SimpleDateFormat format = new SimpleDateFormat(FORMAT_TIME_SHORT, Locale.getDefault());
		format.setTimeZone(timeZone);
		return format;
	}

	// ===========================================================================================================

	/**
	 * PARSERS
	 */

	/**
	 * Replaces all . and , with actual locale decimal separator (DECIMAL_DOT)
	 * 
	 * @param s
	 * @return
	 */
	public static String checkDot(String s)
	{
		return s.replace('.', DECIMAL_DOT).replace(',', DECIMAL_DOT);
	}

	/**
	 * Parses double value, replacing ./, decimal separator if need
	 * 
	 * @param s
	 * @return
	 * @throws ParseException
	 */
	public static double parseDouble(String s) throws ParseException
	{
		return DF.parse(checkDot(s)).doubleValue();
	}

	/**
	 * [tested] Parses minute-time
	 * 
	 * @param S
	 *            Time in format "hh:mm"
	 * @return Minute time (number of minutes since midnight)
	 */
	public static int parseMinuteTime(String S)
	{
		int hour = Integer.parseInt(S.substring(0, 2));
		int min = Integer.parseInt(S.substring(3, 5));

		if (checkTime(hour, min))
		{
			return (hour * MinPerHour) + min;
		}
		else
		{
			throw new IllegalArgumentException("Incorrect time (" + S + ")");
		}
	}

	public static Date parseDateUTC(String date) throws ParseException
	{
		return FORMATTER_DATE_UTC.get().parse(date);
	}

	public static Date parseDateLocal(TimeZone timeZone, String date) throws ParseException
	{
		return getFormatDateLocal(timeZone).parse(date);
	}

	/**
	 * [tested] Читает время из строки формата STD_FORMAT_TIME_UTC
	 * 
	 * @param time
	 *            Строка, хранящая время
	 * @return Время
	 * @throws ParseException
	 */
	public static Date parseTimeUTC(String time)
	{
		try
		{
			return FORMATTER_TIME_UTC.get().parse(time);
		}
		catch (ParseException e)
		{
			try
			{
				return FORMATTER_DATE_UTC.get().parse(time);
			}
			catch (ParseException e2)
			{
				// TODO: don't wrap (or wrap time parser too)
				throw new RuntimeException(e2);
			}
		}
	}

	/**
	 * Calculates value of simple math expression. Four operations supported: +, -, *, /.
	 * Correct processing of negative values is not guaranteed (f.e., calculate(-1/-2) = -3 instead of expected 0.5)
	 * 
	 * @param s
	 *            String to calculate (f.e., "2+3*4", "-10*2")
	 */
	public static double parseExpression(String s) throws NumberFormatException
	{
		s = s.trim();

		if (s.isEmpty())
		{
			return 0;
		}

		try
		{
			s = s.replaceAll("\\.", String.valueOf(DECIMAL_DOT));
			s = s.replaceAll("\\,", String.valueOf(DECIMAL_DOT));
			return Double.parseDouble(s);
		}
		catch (NumberFormatException e)
		{
			// well, try next...
		}

		if (s.contains("+"))
		{
			int k = s.lastIndexOf("+");
			String op1 = s.substring(0, k).trim();
			String op2 = s.substring(k + 1).trim();

			if (!op1.isEmpty() && op2.isEmpty())
			{
				return parseExpression(op1);
			}
			if (op1.isEmpty() && !op2.isEmpty())
			{
				return parseExpression(op2);
			}
			if (!op1.isEmpty() && !op2.isEmpty())
			{
				return parseExpression(op1) + parseExpression(op2);
			}
		}

		if (s.contains("-"))
		{
			int k = s.lastIndexOf("-");
			String op1 = s.substring(0, k).trim();
			String op2 = s.substring(k + 1).trim();

			if (!op1.isEmpty() && op2.isEmpty())
			{
				return parseExpression(op1);
			}
			if (op1.isEmpty() && !op2.isEmpty())
			{
				return -parseExpression(op2);
			}
			if (!op1.isEmpty() && !op2.isEmpty())
			{
				return parseExpression(op1) - parseExpression(op2);
			}
		}

		if (s.contains("*"))
		{
			int k = s.lastIndexOf("*");
			String op1 = s.substring(0, k).trim();
			String op2 = s.substring(k + 1).trim();

			if (!op1.isEmpty() && op2.isEmpty())
			{
				return parseExpression(op1);
			}
			if (op1.isEmpty() && !op2.isEmpty())
			{
				return parseExpression(op2);
			}
			if (!op1.isEmpty() && !op2.isEmpty())
			{
				return parseExpression(op1) * parseExpression(op2);
			}
		}

		if (s.contains("/"))
		{
			int k = s.lastIndexOf("/");
			String op1 = s.substring(0, k).trim();
			String op2 = s.substring(k + 1).trim();

			if (!op1.isEmpty() && op2.isEmpty())
			{
				return parseExpression(op1);
			}
			if (op1.isEmpty() && !op2.isEmpty())
			{
				return parseExpression(op2);
			}
			if (!op1.isEmpty() && !op2.isEmpty())
			{
				return parseExpression(op1) / parseExpression(op2);
			}
		}

		throw new NumberFormatException("Can't parse expression: " + s);

		//		if (s.isEmpty())
		//		{
		//			return 0.0;
		//		}
		//
		//		s = s.replaceAll("\\.", String.valueOf(DECIMAL_DOT));
		//		s = s.replaceAll("\\,", String.valueOf(DECIMAL_DOT));
		//
		//		try
		//		{
		//			return (Double)engine.eval(s);
		//		}
		//		catch (ScriptException e)
		//		{
		//			throw new RuntimeException(e);
		//		}
	}

	/**
	 * FORMATTERS
	 */

	/**
	 * [tested] Converts integer into string; non-negative single-digit numbers get one leading zero. Negative values are returning as-is.
	 * 
	 * @param Integer
	 *            number
	 * @return
	 */
	public static String intTo00(int n)
	{
		return (n >= 0) && (n < 10) ? "0" + String.valueOf(n) : String.valueOf(n);
	}

	/**
	 * Converts double value into string dismissing ".0" part if the value is integer.
	 * 
	 * @param x
	 * @return
	 */
	public static String formatDoubleShort(double x)
	{
		return (x % 1 == 0) ? String.valueOf((int)x) : String.valueOf(x);
	}

	public static String formatDoubleSigned(double x)
	{
		return (x > 0 ? "+" : "") + String.format("%.1f", x);
	}

	public static String formatDateUTC(Date date)
	{
		return FORMATTER_DATE_UTC.get().format(date);
	}

	public static String formatDateLocal(TimeZone timeZone, Date date)
	{
		return getFormatDateLocal(timeZone).format(date);
	}

	public static String formatBooleanStr(boolean x)
	{
		return x ? "true" : "false";
	}

	public static String formatBooleanInt(boolean x)
	{
		return x ? "1" : "0";
	}

	public static String formatJSONArray(List<String> list)
	{
		JSONArray json = new JSONArray();

		for (String item : list)
		{
			json.put(item);
		}

		return json.toString();
	}

	/**
	 * [tested] Преобразует время в формат сервера STD_FORMAT_TIME_UTC
	 * 
	 * @param time
	 *            Время
	 * @return Строка
	 */
	public static String formatTimeUTC(Date time)
	{
		return FORMATTER_TIME_UTC.get().format(time);
	}

	public static String formatTimeLocal(TimeZone timeZone, Date time)
	{
		return getFormatTimeLocal(timeZone).format(time);
	}

	public static String formatTimeLocalShort(TimeZone timeZone, Date time)
	{
		return getFormatTimeLocalShort(timeZone).format(time);
	}

	/**
	 * VALIDATORS
	 */

	/**
	 * [tested] Validates the (hour,minute) pair
	 * 
	 * @param hour
	 * @param min
	 * @return True if pair is correct, false otherwise
	 */
	public static boolean checkTime(int hour, int min)
	{
		return (hour >= 0) && (hour < HourPerDay) && (min >= 0) && (min < MinPerHour);
	}

	/**
	 * CONVERTORS
	 */

	/**
	 * [tested] Преобразует время во время дневника.
	 * 
	 * @param time
	 *            Время
	 * @return Время дневника
	 */
	public static int timeToMin(Date time)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TIMEZONE_UTC);
		c.setTime(time);
		return (c.get(Calendar.HOUR_OF_DAY) * MinPerHour) + c.get(Calendar.MINUTE);
	}

	/**
	 * Rounds up to specified number of digits after dot
	 * 
	 * @param x
	 * @param digits
	 * @return
	 */
	public static double round(double x, int digits)
	{
		// TODO: seems bad approach
		double factor;

		switch (digits)
		{
			case 1:
			{
				factor = 10;
				break;
			}
			case 2:
			{
				factor = 100;
				break;
			}
			case 3:
			{
				factor = 1000;
				break;
			}
			default:
			{
				factor = Math.pow(10, digits);
			}
		}

		return (Math.round(x * factor)) / factor;
	}

	/**
	 * Rounds up to 2 digits after dot
	 * 
	 * @param x
	 * @return
	 */
	public static double round2(double x)
	{
		return round(x, 2);
	}

	/**
	 * Calculates summ
	 * 
	 * @param values
	 * @return
	 */
	public static double getSumm(List<Double> values)
	{
		double summ = 0.0;

		for (Double x : values)
		{
			summ += x;
		}

		return summ;
	}

	/**
	 * Calculates mean value
	 * 
	 * @param values
	 * @return
	 */
	public static double getMean(List<Double> values)
	{
		if (values.size() > 0)
		{
			return getSumm(values) / values.size();
		}
		else
		{
			return 0.0;
		}
	}

	/**
	 * Calculates standard deviation using pre-calculated mean
	 * 
	 * @param values
	 * @param mean
	 * @return
	 */
	public static double getDeviation(List<Double> values, double mean)
	{
		double s = 0.0;

		if (values.size() > 0)
		{
			for (Double x : values)
			{
				s += (x - mean) * (x - mean);
			}
			s /= values.size();
		}
		return Math.sqrt(s);
	}

	/**
	 * RANDOM
	 */

	/**
	 * Returns random string from supplied string array
	 * 
	 * @param strings
	 * @return
	 */
	public static String randomString(String... strings)
	{
		return strings[r.nextInt(strings.length)];
	}

	/**
	 * Returns random date of period [2000-01-01 00:00:00, 2029-12-28 23:59:59]. Day of month is always in [1, 28] interval.
	 */
	public static Date randomTime()
	{
		final int year = 2000 + r.nextInt(30);
		final int month = 1 + r.nextInt(12);
		final int day = 1 + r.nextInt(28);
		final int hour = r.nextInt(24);
		final int min = r.nextInt(60);
		final int sec = r.nextInt(60);

		return time(year, month, day, hour, min, sec);
	}

	/**
	 * DATE UTILS
	 */

	/**
	 * [tested] Получает предыдущую дату по отношению к указанной
	 * 
	 * @param date
	 *            Дата
	 * @return Предыдущая дата
	 */
	public static Date getPrevDay(Date date)
	{
		return shiftDate(date, -1);
	}

	/**
	 * [tested] Получает следующую дату по отношению к указанной
	 * 
	 * @param date
	 *            Дата
	 * @return Следующая дата
	 */
	public static Date getNextDay(Date date)
	{
		return shiftDate(date, +1);
	}

	public static Date getNextMonth(Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, 1);
		return c.getTime();
	}

	/**
	 * Adds specified amount of days to the date
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date shiftDate(Date date, int days)
	{
		return new Date(date.getTime() + MsecPerDay * days);
	}

	/**
	 * [tested] Returns sorted dates list (lastDate-period+1 ... lastDate)
	 * 
	 * @param lastDate
	 *            Current date
	 * @param period
	 *            Days
	 * @return
	 */
	public static List<Date> getPeriodDates(Date lastDate, int period)
	{
		List<Date> dates = new ArrayList<Date>();

		Calendar c = Calendar.getInstance();
		c.setTime(lastDate);

		c.add(Calendar.DATE, -period);

		for (int i = 0; i < period; i++)
		{
			c.add(Calendar.DATE, +1);
			dates.add(c.getTime());
		}

		return dates;
	}

	/**
	 * Constructs date (UTC)
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static Date date(int year, int month, int day)
	{
		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTimeZone(TIMEZONE_UTC);
		c.set(year, month - 1, day);
		return c.getTime();
	}

	/**
	 * Constructs time (UTC)
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param min
	 * @param sec
	 * @return
	 */
	public static Date time(int year, int month, int day, int hour, int min, int sec)
	{
		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTimeZone(TIMEZONE_UTC);
		c.set(year, month - 1, day, hour, min, sec);
		return c.getTime();
	}

	/**
	 * Constructs date (local)
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static Date dateLocal(TimeZone timeZone, int year, int month, int day)
	{
		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTimeZone(timeZone);
		c.set(year, month - 1, day);
		return c.getTime();
	}

	/**
	 * Constructs time (local)
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param min
	 * @param sec
	 * @return
	 */
	public static Date timeLocal(TimeZone timeZone, int year, int month, int day, int hour, int min, int sec)
	{
		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTimeZone(timeZone);
		c.set(year, month - 1, day, hour, min, sec);
		return c.getTime();
	}

	/**
	 * Constructs today's midnight date (local)
	 * 
	 * @return
	 */
	public static Date today(TimeZone timeZone)
	{
		Calendar c = Calendar.getInstance(timeZone);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DATE);

		c.clear();
		c.setTimeZone(timeZone);
		c.set(year, month, day);
		return c.getTime();
	}

	public static int getDayMinutes(Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.HOUR_OF_DAY) * MinPerHour + c.get(Calendar.MINUTE);
	}

	/**
	 * MISC
	 */

	public static void sleep(long time)
	{
		try
		{
			Thread.sleep(time);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static boolean hasWordStartedWith(String s, String editText)
	{
		if (editText.isEmpty())
		{
			return true;
		}

		s = s.toUpperCase();
		editText = editText.toUpperCase();

		boolean ignore = false;
		int j = 0;
		for (int i = 0; i < s.length(); i++)
		{
			if (ALPHANUMERIC.indexOf(s.charAt(i)) != -1)
			{
				if (!ignore)
				{
					if (s.charAt(i) == editText.charAt(j))
					{
						if (++j == editText.length())
						{
							return true;
						}
					}
					else
					{
						j = 0;
						ignore = true;
					}
				}
			}
			else
			{
				j = 0;
				ignore = false;
			}
		}

		return false;
	}

	public static boolean isNullOrEmpty(String s)
	{
		return (s == null) || (s.isEmpty());
	}

	// private static String formatArray(byte array[])
	// {
	// StringBuilder sb = new StringBuilder("{");
	//
	// for (int i = 0; i < array.length; i++)
	// {
	// sb.append(array[i]);
	// if (i < (array.length - 1))
	// {
	// sb.append(", ");
	// }
	// }
	//
	// sb.append("}");
	// return sb.toString();
	// }
}
