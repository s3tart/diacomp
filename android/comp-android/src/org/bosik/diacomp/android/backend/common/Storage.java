package org.bosik.diacomp.android.backend.common;

import java.util.Date;
import java.util.List;
import org.bosik.diacomp.android.backend.common.webclient.WebClient;
import org.bosik.diacomp.android.backend.features.diary.DiaryLocalService;
import org.bosik.diacomp.android.backend.features.diary.DiaryWebService;
import org.bosik.diacomp.android.backend.features.dishbase.DishBaseLocalService;
import org.bosik.diacomp.android.backend.features.dishbase.DishBaseWebService;
import org.bosik.diacomp.android.backend.features.foodbase.FoodBaseLocalService;
import org.bosik.diacomp.android.backend.features.foodbase.FoodBaseWebService;
import org.bosik.diacomp.android.frontend.activities.ActivityPreferences;
import org.bosik.diacomp.android.utils.ErrorHandler;
import org.bosik.diacomp.core.entities.business.foodbase.FoodItem;
import org.bosik.diacomp.core.entities.tech.Versioned;
import org.bosik.diacomp.core.services.analyze.AnalyzeExtracter;
import org.bosik.diacomp.core.services.analyze.AnalyzeService;
import org.bosik.diacomp.core.services.analyze.AnalyzeServiceImpl;
import org.bosik.diacomp.core.services.analyze.entities.KoofList;
import org.bosik.diacomp.core.services.diary.DiaryService;
import org.bosik.diacomp.core.services.dishbase.DishBaseService;
import org.bosik.diacomp.core.services.foodbase.FoodBaseService;
import org.bosik.diacomp.core.utils.Utils;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Stores application DAOs as singletons
 * 
 * @author Bosik
 */
public class Storage
{
	// FIXME: don't refer to ActivityPreferences here

	private static final String		TAG					= Storage.class.getSimpleName();

	private static final int		CONNECTION_TIMEOUT	= 6000;

	// DAO

	public static WebClient			webClient;

	public static DiaryService		localDiary;
	public static DiaryService		webDiary;
	public static FoodBaseService	localFoodBase;
	public static FoodBaseService	webFoodBase;
	public static DishBaseService	localDishBase;
	public static DishBaseService	webDishBase;

	private static AnalyzeService	analyzeService;
	public static KoofList			koofs;

	private static int				ANALYZE_DAYS_PERIOD	= 20;

	/**
	 * Initializes the storage. Might be called sequentially
	 * 
	 * @param context
	 * @param resolver
	 * @param preferences
	 */
	public static void init(Context context, ContentResolver resolver, SharedPreferences preferences)
	{
		Log.v(TAG, "Storage unit initialization...");

		// DAO's setup

		if (null == webClient)
		{
			Log.v(TAG, "Web client initialization...");
			webClient = new WebClient(CONNECTION_TIMEOUT);
		}
		if (null == localDiary)
		{
			Log.v(TAG, "Local diary initialization...");
			localDiary = new DiaryLocalService(resolver);
		}
		if (null == webDiary)
		{
			Log.v(TAG, "Web diary initialization...");
			webDiary = new DiaryWebService(webClient);
		}
		if (null == localFoodBase)
		{
			Log.v(TAG, "Local food base initialization...");
			localFoodBase = new FoodBaseLocalService(resolver);
		}
		if (null == localDishBase)
		{
			Log.v(TAG, "Local dish base initialization...");
			localDishBase = new DishBaseLocalService(resolver);
		}
		if (null == webFoodBase)
		{
			Log.v(TAG, "Web food base initialization...");

			webFoodBase = new FoodBaseWebService(webClient);
		}
		if (null == webDishBase)
		{
			Log.v(TAG, "Web dish base initialization...");
			webDishBase = new DishBaseWebService(webClient);
		}

		if (null == analyzeService)
		{
			analyzeService = new AnalyzeServiceImpl();
		}

		if (koofs == null)
		{
			Date toTime = new Date();
			Date fromTime = new Date(toTime.getTime() - (ANALYZE_DAYS_PERIOD * Utils.MsecPerDay));
			double adaptation = 0.25; // [0..0.5]

			koofs = AnalyzeExtracter.analyze(analyzeService, localDiary, fromTime, toTime, adaptation);
		}

		ErrorHandler.init(webClient);

		// this applies all preferences
		applyPreference(preferences, null);

		// analyze using
		// RelevantIndexator.indexate(localDiary, localFoodBase, localDishBase);

		// buildFoodList();
	}

	private static String pair(String name, double value)
	{
		return String.format("%s=\"%.1f\"", name, value).replace(",", ".");
	}

	private static String pair(String name, String value)
	{
		return String.format("%s=\"%s\"", name, value.replace("\"", "&quot;"));
	}

	private static void buildFoodList()
	{
		String result = "";

		List<Versioned<FoodItem>> foods = localFoodBase.findAll(false);
		for (Versioned<FoodItem> item : foods)
		{
			FoodItem food = item.getData();
			if (food.getName().contains("Теремок"))
			{
				result = String.format("\t<food %s %s %s %s %s %s table=\"True\" tag=\"0\"/>",
						pair("id", Utils.generateGuid().toUpperCase()), pair("name", food.getName()),
						pair("prots", food.getRelProts()), pair("fats", food.getRelFats()),
						pair("carbs", food.getRelCarbs()), pair("val", food.getRelValue()));
				Log.e(TAG, result);
			}
		}
	}

	private static boolean check(String testKey, String baseKey)
	{
		return (testKey == null) || testKey.equals(baseKey);
	}

	/**
	 * Applies changed preference for specified key (if null, applies all settings)
	 * 
	 * @param pref
	 *            Preference unit
	 * @param key
	 *            Change prefernce's key
	 */
	public static void applyPreference(SharedPreferences pref, String key)
	{
		Log.v(TAG, "applyPreferences(): key = '" + key + "'");

		if (check(key, ActivityPreferences.PREF_ACCOUNT_SERVER_KEY))
		{
			webClient.setServer(pref.getString(ActivityPreferences.PREF_ACCOUNT_SERVER_KEY,
					ActivityPreferences.PREF_ACCOUNT_SERVER_DEFAULT));
		}

		if (check(key, ActivityPreferences.PREF_ACCOUNT_USERNAME_KEY))
		{
			webClient.setUsername(pref.getString(ActivityPreferences.PREF_ACCOUNT_USERNAME_KEY,
					ActivityPreferences.PREF_ACCOUNT_USERNAME_DEFAULT));
		}

		if (check(key, ActivityPreferences.PREF_ACCOUNT_PASSWORD_KEY))
		{
			webClient.setPassword(pref.getString(ActivityPreferences.PREF_ACCOUNT_PASSWORD_KEY,
					ActivityPreferences.PREF_ACCOUNT_PASSWORD_DEFAULT));
		}
	}
}