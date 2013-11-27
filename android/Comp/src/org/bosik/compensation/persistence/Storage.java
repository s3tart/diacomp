package org.bosik.compensation.persistence;

import java.io.IOException;
import java.util.List;
import org.bosik.compensation.bo.foodbase.FoodItem;
import org.bosik.compensation.face.R;
import org.bosik.compensation.persistence.dao.BaseDAO;
import org.bosik.compensation.persistence.dao.DiaryDAO;
import org.bosik.compensation.persistence.dao.local.LocalDiaryDAO;
import org.bosik.compensation.persistence.dao.local.LocalFoodBaseDAO;
import org.bosik.compensation.persistence.dao.web.WebDiaryDAO;
import org.bosik.compensation.persistence.dao.web.WebFoodBaseDAO;
import org.bosik.compensation.persistence.dao.web.utils.client.WebClient;
import org.bosik.compensation.utils.ErrorHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Хранит все данные приложения
 * 
 * @author Bosik
 */
public class Storage
{
	private static final String			TAG	= Storage.class.getSimpleName();

	// настройки
	private static SharedPreferences	pref;

	// настройки
	private static String				PREF_SERVER;
	private static String				PREF_USERNAME;
	private static String				PREF_PASSWORD;
	private static String				PREF_DEFAULT_SERVER;
	private static String				PREF_DEFAULT_USERNAME;
	private static String				PREF_DEFAULT_PASSWORD;

	// данные
	public static WebClient				webClient;
	public static DiaryDAO				localDiary;
	public static DiaryDAO				webDiary;
	public static BaseDAO<FoodItem>		localFoodBase;
	public static BaseDAO<FoodItem>		webFoodBase;
	public static List<FoodItem>		foodBase;

	/**
	 * Инициализирует хранилище. Метод можно вызывать повторно.
	 * 
	 * @param context
	 * @param resolver
	 */
	public static void init(Context context, ContentResolver resolver)
	{
		Log.d(TAG, "init()");

		// ПОЛУЧЕНИЕ НАСТРОЕК

		PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
		pref = PreferenceManager.getDefaultSharedPreferences(context);
		PREF_SERVER = context.getString(R.string.prefServer);
		PREF_USERNAME = context.getString(R.string.prefUsername);
		PREF_PASSWORD = context.getString(R.string.prefPassword);
		PREF_DEFAULT_SERVER = context.getString(R.string.prefDefaultServer);
		PREF_DEFAULT_USERNAME = context.getString(R.string.prefDefaultUsername);
		PREF_DEFAULT_PASSWORD = context.getString(R.string.prefDefaultPassword);

		// НАСТРОЙКА СИСТЕМЫ

		if (null == webClient)
		{
			Log.d(TAG, "init(): web client initialization...");
			webClient = new WebClient(Integer.parseInt(context.getString(R.string.connectionTimeout)));
		}
		if (null == localDiary)
		{
			Log.d(TAG, "init(): local diary initialization...");
			localDiary = new LocalDiaryDAO(resolver);
		}
		if (null == webDiary)
		{
			Log.d(TAG, "init(): web diary initialization...");
			webDiary = new WebDiaryDAO(webClient);
		}
		if (null == localFoodBase)
		{
			Log.d(TAG, "init(): local foodbase initialization...");
			String fileName = context.getString(R.string.fileNameFoodBase);
			try
			{
				localFoodBase = new LocalFoodBaseDAO(context, fileName);
				foodBase = localFoodBase.findAll();
			}
			catch (IOException e)
			{
				localFoodBase = null;
				throw new RuntimeException("Failed to create local base DAO", e);
			}
		}
		if (null == Storage.webFoodBase)
		{
			Log.d(TAG, "init(): web foodbase initialization...");
			webFoodBase = new WebFoodBaseDAO(webClient);
		}

		ErrorHandler.init(webClient);

		// this applies all preferences
		applyPreference(pref, null);
	}

	private static boolean check(String testKey, String baseKey)
	{
		return (testKey == null) || testKey.equals(baseKey);
	}

	/**
	 * Применяет изменённое значение. Если ключ равен null, то пересчитываются все настройки.
	 * 
	 * @param preferences
	 *            Настройки
	 * @param key
	 *            Имя изменившейся настройки
	 */
	public static void applyPreference(SharedPreferences preferences, String key)
	{
		Log.d(TAG, "applyPreferences(): key = '" + key + "'");

		if (check(key, PREF_SERVER))
		{
			webClient.setServer(preferences.getString(PREF_SERVER, PREF_DEFAULT_SERVER));
		}

		if (check(key, PREF_USERNAME))
		{
			webClient.setUsername(preferences.getString(PREF_USERNAME, PREF_DEFAULT_USERNAME));
		}

		if (check(key, PREF_PASSWORD))
		{
			webClient.setPassword(preferences.getString(PREF_PASSWORD, PREF_DEFAULT_PASSWORD));
		}

		// THINK: как узнавать об ошибках, произошедших у пользователя в release-mode? Email? Web?
	}

	// public static void saveFoodbase()
	// {
	// if (localFoodBase.modified())
	// {
	// Log.d(TAG, "init(): saving food base...");
	// localFoodBase.save();
	// }
	// }
}