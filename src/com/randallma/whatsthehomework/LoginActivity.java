package com.randallma.whatsthehomework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class LoginActivity extends Activity {
	
	Button login;
	private EditText attemptUsername;
	private EditText attemptPassword;
	public AsyncHttpClient client;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	
		login = (Button) findViewById(R.id.sign_in_button);
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptUsername = (EditText) findViewById(R.id.username);
				attemptPassword = (EditText) findViewById(R.id.password);
				AttemptLogin(attemptUsername, attemptPassword);
			}
		});
	}
	
	private void AttemptLogin(EditText attemptUsername, EditText attemptPassword) {
		AsyncHttpClient client = new AsyncHttpClient();
 
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		client.setCookieStore(cookieStore);

		RequestParams params = new RequestParams();
		params.put("username", attemptUsername.getText().toString());
		params.put("password", attemptPassword.getText().toString());
		
		client.post("http://192.168.1.42:5000/login/", params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				LinearLayout loginStatus = (LinearLayout) findViewById(R.id.login_status);
				loginStatus.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onSuccess(String response) {
				LinearLayout loginStatus = (LinearLayout) findViewById(R.id.login_status);
				System.out.println(response);

				if (response.equals("wrong")) {
					TextView view = (TextView) findViewById(R.id.login_response);
					view.setText("Invalid username or password");
					loginStatus.setVisibility(View.GONE);
				}
				else {
					// catch some other stuff ???
				}
			}
			
			@Override
			public void onFailure(Throwable e, String response) {
				// catch network errors
			}
			
		});
		
		// test user logged in:
		client.get("http://192.168.1.42:5000/testperms/", new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				System.out.println(response);
			}
		});
		
//		Intent intent = new Intent(this, NewsFeedActivity.class);
//		intent.putExtra(CLIENT_SESSION, client);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
}


//
///**
// * Activity which displays a login screen to the user, offering registration as
// * well.
// */
//public class LoginActivity extends Activity {
//	/**
//	 * A dummy authentication store containing known user names and passwords.
//	 * TODO: remove after connecting to a real authentication system.
//	 */
//	private static final String[] DUMMY_CREDENTIALS = new String[] {
//			"foo@example.com:hello", "bar@example.com:world" };
//
//	/**
//	 * The default email to populate the email field with.
//	 */
//	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
//
//	/**
//	 * Keep track of the login task to ensure we can cancel it if requested.
//	 */
//	private UserLoginTask mAuthTask = null;
//
//	// Values for email and password at the time of the login attempt.
//	private String mEmail;
//	private String mPassword;
//
//	// UI references.
//	private EditText mEmailView;
//	private EditText mPasswordView;
//	private View mLoginFormView;
//	private View mLoginStatusView;
//	private TextView mLoginStatusMessageView;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		setContentView(R.layout.activity_login);
//
//		// Set up the login form.
//		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
//		mEmailView = (EditText) findViewById(R.id.username);
//		mEmailView.setText(mEmail);
//
//		mPasswordView = (EditText) findViewById(R.id.password);
//		mPasswordView
//				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//					@Override
//					public boolean onEditorAction(TextView textView, int id,
//							KeyEvent keyEvent) {
//						if (id == R.id.login || id == EditorInfo.IME_NULL) {
//							attemptLogin();
//							return true;
//						}
//						return false;
//					}
//				});
//
//		mLoginFormView = findViewById(R.id.login_form);
//		mLoginStatusView = findViewById(R.id.login_status);
//		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
//
//		findViewById(R.id.sign_in_button).setOnClickListener(
//				new View.OnClickListener() {
//					@Override
//					public void onClick(View view) {
//						attemptLogin();
//					}
//				});
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
//		getMenuInflater().inflate(R.menu.activity_login, menu);
//		return true;
//	}
//
//	/**
//	 * Attempts to sign in or register the account specified by the login form.
//	 * If there are form errors (invalid email, missing fields, etc.), the
//	 * errors are presented and no actual login attempt is made.
//	 */
//	public void attemptLogin() {
//		if (mAuthTask != null) {
//			return;
//		}
//
//		// Reset errors.
//		mEmailView.setError(null);
//		mPasswordView.setError(null);
//
//		// Store values at the time of the login attempt.
//		mEmail = mEmailView.getText().toString();
//		mPassword = mPasswordView.getText().toString();
//
//		boolean cancel = false;
//		View focusView = null;
//
//		// Check for a valid password.
//		if (TextUtils.isEmpty(mPassword)) {
//			mPasswordView.setError(getString(R.string.error_field_required));
//			focusView = mPasswordView;
//			cancel = true;
//		} else if (mPassword.length() < 4) {
//			mPasswordView.setError(getString(R.string.error_invalid_password));
//			focusView = mPasswordView;
//			cancel = true;
//		}
//
//		// Check for a valid email address.
//		if (TextUtils.isEmpty(mEmail)) {
//			mEmailView.setError(getString(R.string.error_field_required));
//			focusView = mEmailView;
//			cancel = true;
//		} else if (!mEmail.contains("@")) {
//			mEmailView.setError(getString(R.string.error_invalid_email));
//			focusView = mEmailView;
//			cancel = true;
//		}
//
//		if (cancel) {
//			// There was an error; don't attempt login and focus the first
//			// form field with an error.
//			focusView.requestFocus();
//		} else {
//			// Show a progress spinner, and kick off a background task to
//			// perform the user login attempt.
//			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
//			showProgress(true);
//			mAuthTask = new UserLoginTask();
//			mAuthTask.execute((Void) null);
//		}
//	}
//
//	/**
//	 * Shows the progress UI and hides the login form.
//	 */
//	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//	private void showProgress(final boolean show) {
//		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//		// for very easy animations. If available, use these APIs to fade-in
//		// the progress spinner.
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//			int shortAnimTime = getResources().getInteger(
//					android.R.integer.config_shortAnimTime);
//
//			mLoginStatusView.setVisibility(View.VISIBLE);
//			mLoginStatusView.animate().setDuration(shortAnimTime)
//					.alpha(show ? 1 : 0)
//					.setListener(new AnimatorListenerAdapter() {
//						@Override
//						public void onAnimationEnd(Animator animation) {
//							mLoginStatusView.setVisibility(show ? View.VISIBLE
//									: View.GONE);
//						}
//					});
//
//			mLoginFormView.setVisibility(View.VISIBLE);
//			mLoginFormView.animate().setDuration(shortAnimTime)
//					.alpha(show ? 0 : 1)
//					.setListener(new AnimatorListenerAdapter() {
//						@Override
//						public void onAnimationEnd(Animator animation) {
//							mLoginFormView.setVisibility(show ? View.GONE
//									: View.VISIBLE);
//						}
//					});
//		} else {
//			// The ViewPropertyAnimator APIs are not available, so simply show
//			// and hide the relevant UI components.
//			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
//			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//		}
//	}
//
//	/**
//	 * Represents an asynchronous login/registration task used to authenticate
//	 * the user.
//	 */
//	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//		@Override
//		protected Boolean doInBackground(Void... params) {
//			// TODO: attempt authentication against a network service.
//
//			try {
//				// Simulate network access.
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				return false;
//			}
//
//			for (String credential : DUMMY_CREDENTIALS) {
//				String[] pieces = credential.split(":");
//				if (pieces[0].equals(mEmail)) {
//					// Account exists, return true if the password matches.
//					return pieces[1].equals(mPassword);
//				}
//			}
//
//			// TODO: register the new account here.
//			return true;
//		}
//
//		@Override
//		protected void onPostExecute(final Boolean success) {
//			mAuthTask = null;
//			showProgress(false);
//
//			if (success) {
//				finish();
//			} else {
//				mPasswordView
//						.setError(getString(R.string.error_incorrect_password));
//				mPasswordView.requestFocus();
//			}
//		}
//
//		@Override
//		protected void onCancelled() {
//			mAuthTask = null;
//			showProgress(false);
//		}
//	}
//}
