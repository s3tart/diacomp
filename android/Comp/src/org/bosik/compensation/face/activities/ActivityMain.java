package org.bosik.compensation.face.activities;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.bosik.compensation.face.BuildConfig;
import org.bosik.compensation.face.R;
import org.bosik.compensation.face.UIUtils;
import org.bosik.compensation.persistence.repository.Storage;
import org.bosik.compensation.persistence.repository.providers.web.WebClient.AuthException;
import org.bosik.compensation.persistence.repository.providers.web.WebClient.DeprecatedAPIException;
import org.bosik.compensation.persistence.repository.providers.web.WebClient.LoginResult;
import org.bosik.compensation.persistence.repository.providers.web.WebClient.ConnectionException;
import org.bosik.compensation.persistence.repository.providers.web.WebClient.ResponseFormatException;
import org.bosik.compensation.persistence.repository.providers.web.WebClient.UndefinedFieldException;
import org.bosik.compensation.persistence.sync.SyncBase;
import org.bosik.compensation.persistence.sync.SyncBase.SyncResult;
import org.bosik.compensation.persistence.sync.SyncDiaryRepository;
import org.bosik.compensation.persistence.sync.SyncDiaryRepository.Callback;
import org.bosik.compensation.utils.ErrorHandler;
import org.bosik.compensation.utils.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityMain extends Activity implements OnSharedPreferenceChangeListener, OnClickListener
{
	/* =========================== КОНСТАНТЫ ================================ */

	private static final String	TAG				= ActivityMain.class.getSimpleName();
	// private static final int RESULT_SPEECH_TO_TEXT = 620;

	/* =========================== ПОЛЯ ================================ */

	// компоненты
	private Button				buttonDiary;
	private Button				buttonFoodBase;
	private Button				buttonDishBase;
	private Button				buttonPref;
	private Button				buttonAuth;
	private Button				buttonTestMealEditor;

	private static boolean		timerSettedUp	= false;

	/* =========================== КЛАССЫ ================================ */

	// TODO: вынести в отдельный модуль, отвязать
	// TODO: refresh diary view on productive (non-trivial) sync

	private class SyncParams
	{
		private boolean	showProgress;

		public SyncParams()
		{
		}

		public SyncParams(SyncParams origin)
		{
			setShowProgress(origin.getShowProgress());
		}

		public boolean getShowProgress()
		{
			return showProgress;
		}

		public void setShowProgress(boolean showProgress)
		{
			this.showProgress = showProgress;
		}
	}

	private class AsyncTaskAuthAndSync extends AsyncTask<SyncParams, Integer, LoginResult> implements Callback
	{
		// <Params, Progress, Result>
		private ProgressDialog		dialog_login;
		private ProgressDialog		dialog_sync;
		private int					syncPagesCount;
		private boolean				syncFoodBase;
		private SyncParams			syncParams;

		// константы для управления progressbar
		private static final int	COM_SHOW_AUTH		= 11;
		private static final int	COM_SHOW_SYNC		= 12;
		private static final int	COM_PROGRESS_MAX	= 21;
		private static final int	COM_PROGRESS_CUR	= 22;

		@Override
		protected void onPreExecute()
		{
			dialog_login = new ProgressDialog(ActivityMain.this);
			dialog_login.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog_login.setCancelable(false);
			dialog_login.setMessage("Авторизация...");

			dialog_sync = new ProgressDialog(ActivityMain.this);
			dialog_sync.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog_sync.setCancelable(false);
			dialog_sync.setMessage("Синхронизация...");
		}

		@Override
		protected LoginResult doInBackground(SyncParams... par)
		{
			Log.i(TAG, "Sync()");
			syncPagesCount = 0;

			syncParams = new SyncParams(par[0]);

			/* АВТОРИЗАЦИЯ */

			if (!Storage.web_client.isOnline())
			{
				Log.d(TAG, "Not logged, trying to auth (username=" + Storage.web_client.getUsername() + ", password="
						+ Storage.web_client.getPassword() + ")");

				publishProgress(COM_SHOW_AUTH);
				try
				{
					Storage.web_client.login();
					Log.d(TAG, "Logged OK");
				}
				catch (ConnectionException e)
				{
					return LoginResult.FAIL_CONNECTION;
				}
				catch (ResponseFormatException e)
				{
					return LoginResult.FAIL_FORMAT;
				}
				catch (DeprecatedAPIException e)
				{
					return LoginResult.FAIL_APIVERSION;
				}
				catch (AuthException e)
				{
					return LoginResult.FAIL_AUTH;
				}
				catch (UndefinedFieldException e)
				{
					return LoginResult.FAIL_UNDEFIELDS;
				}
			}

			/* СИНХРОНИЗАЦИЯ */

			publishProgress(COM_SHOW_SYNC);
			try
			{
				Log.d(TAG, "Sync...");
				// TODO: хранить время последней синхронизации
				Date since = new Date(2013 - 1900, 04 - 1, 1, 0, 0, 0); // а затем мы получаем
																		// громадный синхролист, ага
				syncPagesCount = SyncDiaryRepository.synchronize(Storage.local_diary, Storage.web_diary, since);

				SyncResult r = SyncBase.synchronize(Storage.localFoodBaseRepository, Storage.webFoodbaseRepository);
				syncFoodBase = (r != SyncResult.EQUAL);

				// if (Storage.localFoodBaseRepository.modified())
				// {
				// Storage.localFoodBaseRepository.save();
				// }

				Log.d(TAG, "Sync done OK...");
				return LoginResult.DONE;
			}
			catch (ConnectionException e)
			{
				// Storage.logged = false;
				Log.e(TAG, e.getLocalizedMessage());
				return LoginResult.FAIL_CONNECTION;
			}
			catch (ResponseFormatException e)
			{
				// Storage.logged = false;
				Log.e(TAG, e.getLocalizedMessage());
				return LoginResult.FAIL_FORMAT;
			}
			catch (DeprecatedAPIException e)
			{
				// Storage.logged = false;
				Log.e(TAG, e.getLocalizedMessage());
				return LoginResult.FAIL_APIVERSION;
			}
			catch (AuthException e)
			{
				// Storage.logged = false;
				Log.e(TAG, e.getLocalizedMessage());
				return LoginResult.FAIL_AUTH;
			}
			catch (UndefinedFieldException e)
			{
				// Storage.logged = false;
				Log.e(TAG, e.getLocalizedMessage());
				return LoginResult.FAIL_UNDEFIELDS;
			}
		}

		@Override
		protected void onProgressUpdate(Integer... msg)
		{
			if (!syncParams.getShowProgress())
			{
				return;
			}

			switch (msg[0])
			{
				case COM_SHOW_AUTH:
					dialog_login.show();
					break;
				case COM_SHOW_SYNC:
					dialog_login.dismiss();
					dialog_sync.show();
					break;
			/*
			 * case COM_PROGRESS_MAX: dialog_sync.setMax(msg[1]); break; case COM_PROGRESS_CUR:
			 * dialog_sync.setProgress(msg[1]); break;
			 */
			}
		}

		@Override
		protected void onPostExecute(LoginResult result)
		{
			if (syncParams.getShowProgress())
			{
				if (dialog_login.isShowing())
				{
					dialog_login.dismiss();
				}
				if (dialog_sync.isShowing())
				{
					dialog_sync.dismiss();
				}
			}
			switch (result)
			{
				case FAIL_UNDEFIELDS:
					if (syncParams.getShowProgress())
					{
						UIUtils.showTip(ActivityMain.this, "Ошибка авторизации: укажите адрес сервера, логин и пароль");
					}
					break;
				case FAIL_AUTH:
					if (syncParams.getShowProgress())
					{
						UIUtils.showTip(ActivityMain.this, "Ошибка авторизации: неверный логин/пароль");
					}
					break;
				case FAIL_CONNECTION:
					if (syncParams.getShowProgress())
					{
						UIUtils.showTip(ActivityMain.this, "Ошибка: сервер не отвечает");
					}
					break;
				case FAIL_APIVERSION:
					if (syncParams.getShowProgress())
					{
						UIUtils.showTip(ActivityMain.this, "Ошибка: версия API устарела, обновите приложение");
					}
					break;
				case FAIL_FORMAT:
					if (syncParams.getShowProgress())
					{
						UIUtils.showTip(ActivityMain.this, "Ошибка: неверный формат");
					}
					break;
				case DONE:
				{
					String s;

					if (syncPagesCount > 0)
					{
						s = "Синхронизация дневника прошла успешно, передано страниц: "
								+ String.valueOf(syncPagesCount);
						UIUtils.showTip(ActivityMain.this, s);
					}
					else
						if (syncParams.getShowProgress())
						{
							s = "Дневник уже синхронизирован";
							UIUtils.showTip(ActivityMain.this, s);
						}

					if (syncFoodBase)
					{
						UIUtils.showTip(ActivityMain.this, "База продуктов синхронизирована");
					}

					break;
				}
			}
		}

		// implemented
		@Override
		public void update_max(int max)
		{
			publishProgress(COM_PROGRESS_MAX, max);
		}

		@Override
		public void update_progress(int progress)
		{
			publishProgress(COM_PROGRESS_CUR, progress);
		}
	}

	/* =========================== МЕТОДЫ ================================ */

	// СТАНДАРТНЫЕ

	// handled
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		try
		{
			if (BuildConfig.DEBUG)
			{
				UIUtils.showTip(this, "Debug mode is on");
			}

			// инициализация хранилища
			Storage.init(this, getContentResolver());

			// НАСТРОЙКА ИНТЕРФЕЙСА

			// устанавливаем макет
			setContentView(R.layout.main_menu);

			// определяем компоненты
			buttonDiary = (Button) findViewById(R.id.ButtonDiary);
			buttonFoodBase = (Button) findViewById(R.id.ButtonFoodBase);
			buttonDishBase = (Button) findViewById(R.id.ButtonDishBase);
			buttonTestMealEditor = (Button) findViewById(R.id.buttonTestMealEditor);
			buttonPref = (Button) findViewById(R.id.ButtonPreferences);
			buttonAuth = (Button) findViewById(R.id.buttonAuth);

			// назначаем обработчики
			buttonDiary.setOnClickListener(this);
			buttonFoodBase.setOnClickListener(this);
			buttonDishBase.setOnClickListener(this);
			buttonTestMealEditor.setOnClickListener(this);
			buttonPref.setOnClickListener(this);
			buttonAuth.setOnClickListener(this);

			// TODO: add force single sync on start

			setupSyncTimer(10 * 60 * 1000);

			showDiary();
		}
		catch (Exception e)
		{
			ErrorHandler.handle(e, this);
		}
	}

	// handled
	@Override
	protected void onResume()
	{
		super.onResume();
		try
		{
			PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
		}
		catch (Exception e)
		{
			ErrorHandler.handle(e, this);
		}
	}

	// handled
	@Override
	protected void onPause()
	{
		super.onPause();
		try
		{
			PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
		}
		catch (Exception e)
		{
			ErrorHandler.handle(e, this);
		}
	}

	// handled
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		try
		{
			Log.d(TAG, "Preferences changed, key=" + key);
			Storage.applyPreference(sharedPreferences, key);
		}
		catch (Exception e)
		{
			ErrorHandler.handle(e, this);
		}
	}

	// handled
	@Override
	public void onClick(View v)
	{
		try
		{
			switch (v.getId())
			{
				case R.id.ButtonDiary:
					showDiary();
					break;
				case R.id.ButtonFoodBase:
					break;
				case R.id.ButtonDishBase:
					break;
				case R.id.ButtonPreferences:
					Intent settingsActivity = new Intent(getBaseContext(), ActivityPreferences.class);
					startActivity(settingsActivity);
					break;
				case R.id.buttonAuth:
					SyncParams par = new SyncParams();
					par.setShowProgress(true);
					new AsyncTaskAuthAndSync().execute(par);
					break;
				case R.id.buttonTestMealEditor:
					throw new RuntimeException("Test exception");
					// break;
			}
		}
		catch (Exception e)
		{
			ErrorHandler.handle(e, this);
		}
	}

	private void setupSyncTimer(long interval)
	{
		if (timerSettedUp)
		{
			return;
		}
		timerSettedUp = true;

		final SyncParams par = new SyncParams();
		par.setShowProgress(false);

		TimerTask task = new TimerTask()
		{
			private Handler	mHandler	= new Handler(Looper.getMainLooper());

			@Override
			public void run()
			{
				mHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						new AsyncTaskAuthAndSync().execute(par);
						// UIUtils.showTip(mainActivity, "Tick!");
					}
				});
			}
		};

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, interval);
	}

	// РАБОЧИЕ: ИНТЕРФЕЙС

	private void showDiary()
	{
		// TODO: константы
		Intent intent = new Intent(this, ActivityDiary.class);
		intent.putExtra("date", Utils.now());
		startActivity(intent);
	}

	/*
	 * private void clearLocalDiary() { // формируем параметры String mSelectionClause =
	 * DiaryProvider.COLUMN_DATE + " > ?"; String[] mSelectionArgs = {"2014-01-01"};
	 * 
	 * // выполняем запрос int count = getContentResolver().delete( DiaryProvider.CONTENT_URI,
	 * mSelectionClause, mSelectionArgs);
	 * 
	 * Log.w(TAG, "Deleted records: " + count); }
	 */

	// АЛЬФА-ТЕСТИРОВАНИЕ

	/*
	 * private void executeTaskAsync(final Runnable R, final long timeOut) { boolean result;
	 * 
	 * final Thread taskThread = new Thread() {
	 * 
	 * @Override public void run() { R.run(); } };
	 * 
	 * final Thread controlThread = new Thread() {
	 * 
	 * @Override public void run() { try { taskThread.run(); taskThread.join(timeOut); if
	 * (taskThread.isAlive()) { result = false; taskThread.interrupt(); } else { result = true; } }
	 * catch (InterruptedException e) { e.printStackTrace(); } } };
	 * 
	 * controlThread.run(); }
	 */
}
