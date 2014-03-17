package com.kemas;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class hupernikao {
	public static HashMap<String, Object> Convert_UTCtoGMT_Str(String DateToConvert) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return Convert_UTCtoGMT_Str(DateToConvert, simpleDateFormat);
	}

	public static HashMap<String, Object> Convert_UTCtoGMT_Str(String DateToConvert, SimpleDateFormat simpleDateFormat) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		Date resDate = Convert_UTCtoGMT(DateToConvert, simpleDateFormat);
		Calendar cal = Calendar.getInstance();
		cal.setTime(resDate);

		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int year = cal.get(Calendar.YEAR);

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int seconds = cal.get(Calendar.SECOND);

		String date = CompletarCadena(day) + "/" + CompletarCadena(month) + "/" + CompletarCadena(year);
		String hours = CompletarCadena(hour) + ":" + CompletarCadena(minute) + ":" + CompletarCadena(seconds);
		result.put("date", date);
		result.put("hour", hours);

		return result;
	}

	public static Date Convert_UTCtoGMT(String DateToConvert) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return Convert_UTCtoGMT(DateToConvert, simpleDateFormat);
	}

	public static Date Convert_UTCtoGMT(String DateToConvert, SimpleDateFormat simpleDateFormat) {
		Date result = null;
		try {
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			result = simpleDateFormat.parse(DateToConvert);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String Convert_UTCtoTZ(String DatoToConvert, String TzName) {
		String result = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date parsed;
			parsed = format.parse(DatoToConvert);
			TimeZone tz = TimeZone.getTimeZone(TzName);
			format.setTimeZone(tz);
			result = format.format(parsed);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Este método devuelve una conexión a OpenERP en el caso de que ya se
	 * tengan todas las credenciales necesarias
	 **/
	public static OpenERP BuildOpenERPConnection(Configuration config) {
		OpenERP oerp = null;
		if (config.getUserID() == null) {
			return oerp;
		}
		try {
			oerp = new OpenERP(config.getServer(), config.getPort(), config.getDataBase(), config.getLogin(), config.getPassword(), config.getUserID());
		} catch (MalformedURLException e) {
			Log.v("Crando Conector a OpenERP", "No se pudo crear el conector a OpenERP");
		}
		return oerp;
	}

	/** Este método Verifica si hay conexión a internet **/
	public static boolean TestNetwork(Context Context) {
		boolean result = false;
		ConnectivityManager connec = (ConnectivityManager) Context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
		// No sólo wifi, también GPRS
		NetworkInfo[] networks = connec.getAllNetworkInfo();
		// este bucle debería no ser tan ñapa
		for (int i = 0; i < networks.length; i++) {
			// ¿Tenemos conexión? ponemos a true
			if (networks[i].getState() == NetworkInfo.State.CONNECTED) {
				result = true;
			}
		}
		return result;
	}

	public static int ConvertStringtoDateSeconds(String date_str) {
		return ConvertStringtoDateSeconds(date_str, "yyyy-MM-dd HH:mm:ss");
	}

	public static int ConvertStringtoDateSeconds(String date_str, String formato) {
		int result = 0;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatoFecha = new SimpleDateFormat(formato);
		try {
			Date date_dt = formatoFecha.parse(date_str);
			cal.setTime(date_dt);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int seconds = cal.get(Calendar.SECOND);

			result = (hour * 3600) + (minute * 60) + seconds;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static HashMap<String, Object> ConvertDatetoString(HashMap<String, Object> range_dates) {
		return ConvertDatetoString(range_dates, "yyyy-MM-dd");
	}

	public static HashMap<String, Object> ConvertDatetoString(HashMap<String, Object> range_dates, String formato) {
		SimpleDateFormat formatoFecha = new SimpleDateFormat(formato);
		int year, month, day;
		Calendar cal = Calendar.getInstance();

		// DATE START
		String date_start = range_dates.get("date_start") + "";
		Date date_start_dt;
		try {
			date_start_dt = formatoFecha.parse(date_start);
			cal.setTime(date_start_dt);
			month = cal.get(Calendar.MONTH) + 1;
			day = cal.get(Calendar.DAY_OF_MONTH);
			year = cal.get(Calendar.YEAR);
			date_start = day + "/" + month + "/" + year;
			range_dates.put("date_start", date_start);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// DATE STOP
		String date_stop = range_dates.get("date_stop") + "";
		Date date_stop_dt;
		try {
			date_stop_dt = formatoFecha.parse(date_stop);
			cal.setTime(date_stop_dt);
			month = cal.get(Calendar.MONTH) + 1;
			day = cal.get(Calendar.DAY_OF_MONTH);
			year = cal.get(Calendar.YEAR);
			date_stop = day + "/" + month + "/" + year;
			range_dates.put("date_stop", date_stop);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return range_dates;
	}

	/**
	 * Este método devuelve la hora en ton formato de texto para presentar en
	 * pantalla: 20:00
	 **/
	public static String ConvertToHourFormat(float hour) {
		return ConvertToHourFormat(hour, false);
	}

	public static String ConvertToHourFormat(float hour, boolean with_seconds) {
		String result = "";
		int hours = (int) (hour / 3600);
		float res_hours = hour % 3600;

		int mins = (int) (res_hours / 60);
		float res_mins = res_hours % 60;

		int secs = (int) res_mins;

		if (!with_seconds && secs > 30.0) {
			mins++;
		}

		result = CompletarCadena(hours + "") + ":" + CompletarCadena(mins + "");
		if (with_seconds) {
			result = result + ":" + CompletarCadena(secs + "");
		}
		return result;
	}

	/**
	 * Metodo para completar una cadena con alguna caracter
	 **/
	public static String CompletarCadena(int cadena) {
		return CompletarCadena(cadena, 2);
	}

	public static String CompletarCadena(int cadena, int num) {
		return CompletarCadena(cadena, num, false);
	}

	public static String CompletarCadena(int cadena, int num, boolean right) {
		return CompletarCadena(cadena, num, right, "0");
	}

	public static String CompletarCadena(int cadena, int num, boolean right, String character) {
		return CompletarCadena(cadena + "", num, right, character);
	}

	public static String CompletarCadena(String cadena) {
		return CompletarCadena(cadena, 2);
	}

	public static String CompletarCadena(String cadena, int num) {
		return CompletarCadena(cadena, num, false);
	}

	public static String CompletarCadena(String cadena, int num, boolean right) {
		return CompletarCadena(cadena, num, right, "0");
	}

	public static String CompletarCadena(String cadena, int num, boolean right, String character) {
		while (cadena.length() < num) {
			if (right) {
				cadena = cadena + character;
			} else {
				cadena = character + cadena;
			}
		}
		return cadena;
	}

	/** Metodo para poner un marco Ligeramente redondeado a una imagen **/
	public static Bitmap getRoundedCornerBitmapSimple(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 12;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/** Metodo para poner un marco Totalmente redondeado a una imagen **/
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, boolean square) {
		int width = 0;
		int height = 0;

		if (square) {
			if (bitmap.getWidth() < bitmap.getHeight()) {
				width = bitmap.getWidth();
				height = bitmap.getWidth();
			} else {
				width = bitmap.getHeight();
				height = bitmap.getHeight();
			}
		} else {
			height = bitmap.getHeight();
			width = bitmap.getWidth();
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);
		final float roundPx = 90;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap getRoundedCornerBitmap(Drawable drawable, boolean square) {
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		return getRoundedCornerBitmap(bitmap, square);
	}
}
