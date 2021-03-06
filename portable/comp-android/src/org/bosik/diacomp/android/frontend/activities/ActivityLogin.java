/*  
 *  Diacomp - Diabetes analysis & management system
 *  Copyright (C) 2013 Nikita Bosik
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.bosik.diacomp.android.frontend.activities;

import org.bosik.diacomp.android.R;
import org.bosik.diacomp.android.backend.common.DiaryContentProvider;
import org.bosik.diacomp.android.backend.common.webclient.WebClient;
import org.bosik.diacomp.android.frontend.UIUtils;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as well.
 */
public class ActivityLogin extends AccountAuthenticatorActivity
{
	public static final String	EXTRA_EMAIL					= "com.example.android.authenticatordemo.extra.EMAIL";
	public static final String	EXTRA_PASS					= "com.example.android.authenticatordemo.extra.PASS";

	public static final String	ARG_ACCOUNT_TYPE			= "org.bosik.diacomp.activityLogin.accountType";
	public static final String	ARG_AUTH_TYPE				= "org.bosik.diacomp.activityLogin.authType";
	public static final String	ARG_IS_ADDING_NEW_ACCOUNT	= "org.bosik.diacomp.activityLogin.newAccount";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	UserLoginTask				mAuthTask					= null;

	// Values for email and password at the time of the login attempt.
	String						mEmail;
	String						mPassword;

	String						mAccountType;
	private String				mAuthTokenType;
	private boolean				mNewAccount;

	// UI references.
	private EditText			mEmailView;
	EditText					mPasswordView;
	View						mLoginFormView;
	View						mLoginStatusView;
	private TextView			mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
		mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
		mNewAccount = getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false);

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.accountName);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.accountPassword);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
			{
				if (id == R.id.login || id == EditorInfo.IME_NULL)
				{
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.buttonLoginSignIn).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				attemptLogin();
			}
		});

		findViewById(R.id.buttonLoginRegister).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String url = getString(R.string.server_url) + "register/";
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(browserIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form. If there are form
	 * errors (invalid email, missing fields, etc.), the errors are presented and no actual login
	 * attempt is made.
	 */
	public void attemptLogin()
	{
		if (mAuthTask != null)
		{
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword))
		{
			mPasswordView.setError(getString(R.string.login_error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}
		else if (mPassword.length() < 6)
		{
			mPasswordView.setError(getString(R.string.login_error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail))
		{
			mEmailView.setError(getString(R.string.login_error_field_required));
			focusView = mEmailView;
			cancel = true;
		}
		else if (!mEmail.contains("@"))
		{
			mEmailView.setError(getString(R.string.login_error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel)
		{
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		}
		else
		{
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	void showProgress(final boolean show)
	{
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
		{
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});
		}
		else
		{
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... params)
		{
			final int connectionTimeout = Integer.parseInt(getString(R.string.server_timeout));
			final String serverURL = getString(R.string.server_url);

			WebClient client = new WebClient(connectionTimeout);
			client.setServer(serverURL);
			client.setUsername(mEmail);
			client.setPassword(mPassword);

			try
			{
				client.login();
				return true;
			}
			catch (Exception e)
			{
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success)
		{
			mAuthTask = null;
			showProgress(false);

			if (success)
			{
				submit();
			}
			else
			{
				mPasswordView.setError(getString(R.string.login_error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled()
		{
			mAuthTask = null;
			showProgress(false);
		}
	}

	public void submit()
	{
		final String userName = ((TextView) findViewById(R.id.accountName)).getText().toString();
		final String userPass = ((TextView) findViewById(R.id.accountPassword)).getText().toString();
		new AsyncTask<Void, Void, Intent>()
		{
			@Override
			protected Intent doInBackground(Void... params)
			{
				String authtoken = "token-goes-here";// sServerAuthenticate.userSignIn(userName,
														// userPass, mAuthTokenType);
				final Intent res = new Intent();
				res.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
				res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
				res.putExtra(AccountManager.KEY_AUTHTOKEN, authtoken);
				res.putExtra(EXTRA_PASS, userPass);
				return res;
			}

			@Override
			protected void onPostExecute(Intent intent)
			{
				finishLogin(intent);
			}
		}.execute();
	}

	void finishLogin(Intent intent)
	{
		final AccountManager mAccountManager = AccountManager.get(this);

		String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
		String accountPassword = intent.getStringExtra(EXTRA_PASS);
		final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
		if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false))
		{
			// String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
			// Creating the account on the device and setting the auth token we got
			// (Not setting the auth token will cause another call to the server to authenticate the
			// user)
			mAccountManager.addAccountExplicitly(account, accountPassword, null);
			// mAccountManager.setAuthToken(account, mAuthTokenType, authtoken);
		}
		else
		{
			mAccountManager.setPassword(account, accountPassword);
		}
		setAccountAuthenticatorResult(intent.getExtras());

		// Enabling sync
		Account[] accounts = ActivityMain.getAccounts(this);
		if (accounts.length > 0)
		{
			long SYNC_INTERVAL = 60; // sec
			ContentResolver.setIsSyncable(accounts[0], DiaryContentProvider.AUTHORITY, 1);
			ContentResolver.setSyncAutomatically(accounts[0], DiaryContentProvider.AUTHORITY, true);
			ContentResolver.addPeriodicSync(accounts[0], DiaryContentProvider.AUTHORITY, Bundle.EMPTY, SYNC_INTERVAL);

			UIUtils.showTip(this, getString(R.string.login_tip_sync_started));
		}

		setResult(RESULT_OK, intent);
		finish();
	}
}
