package org.bosik.compensation.persistence.repository;

import org.bosik.compensation.face.R;
import org.bosik.compensation.persistence.repository.diary.DiaryRepository;
import org.bosik.compensation.persistence.repository.diary.LocalDiaryRepository;
import org.bosik.compensation.persistence.repository.diary.WebDiaryRepository;
import org.bosik.compensation.persistence.repository.foodbase.FoodBaseXMLFormatter;
import org.bosik.compensation.persistence.repository.foodbase.LocalFoodBaseRepository;
import org.bosik.compensation.persistence.repository.foodbase.WebFoodBaseRepository;
import org.bosik.compensation.persistence.repository.providers.WebClient;
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
	// @SuppressWarnings("unused")
	private static final String TAG = Storage.class.getSimpleName();

	// настройки
	private static SharedPreferences pref;

	// настройки
	private static String PREF_SERVER;
	private static String PREF_USERNAME;
	private static String PREF_PASSWORD;
	private static String PREF_DEFAULT_SERVER;
	private static String PREF_DEFAULT_USERNAME;
	private static String PREF_DEFAULT_PASSWORD;

	// компоненты (в качестве типа указана конкретная реализация - требуется для внешней настройки)
	public static WebClient web_client = null;
	public static DiaryRepository local_diary = null;
	public static DiaryRepository web_diary = null;
	public static LocalFoodBaseRepository local_foodbase = null;
	public static WebFoodBaseRepository web_foodbase = null;

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

		if (null == web_client)
		{
			Log.d(TAG, "init(): web client initialization...");
			web_client = new WebClient(Integer.parseInt(context.getString(R.string.connectionTimeout)));
		}
		if (null == local_diary)
		{
			Log.d(TAG, "init(): local diary initialization...");
			local_diary = new LocalDiaryRepository(resolver);
		}
		if (null == web_diary)
		{
			Log.d(TAG, "init(): web diary initialization...");
			web_diary = new WebDiaryRepository(web_client);
		}
		if (null == local_foodbase)
		{
			Log.d(TAG, "init(): local foodbase initialization...");
			String fileName = context.getString(R.string.fileNameFoodBase);
			local_foodbase = new LocalFoodBaseRepository(fileName, context, new FoodBaseXMLFormatter());
		}
		if (null == Storage.web_foodbase)
		{
			Log.d(TAG, "init(): web foodbase initialization...");
			web_foodbase = new WebFoodBaseRepository(web_client);
		}

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
			web_client.setServer(preferences.getString(PREF_SERVER, PREF_DEFAULT_SERVER));
		}

		if (check(key, PREF_USERNAME))
		{
			web_client.setUsername(preferences.getString(PREF_USERNAME, PREF_DEFAULT_USERNAME));
		}

		if (check(key, PREF_PASSWORD))
		{
			web_client.setPassword(preferences.getString(PREF_PASSWORD, PREF_DEFAULT_PASSWORD));
		}

		// TODO: как узнавать об ошибках, произошедших у пользователя в release-mode? Email? Web?
	}
}