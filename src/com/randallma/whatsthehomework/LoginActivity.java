package com.randallma.whatsthehomework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class LoginActivity extends Activity {

	private Button login;
	private EditText attemptUsername;
	private EditText attemptPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		login = (Button) findViewById(R.id.sign_in_button);
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptUsername = (EditText) findViewById(R.id.username);
				attemptPassword = (EditText) findViewById(R.id.password);

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(attemptPassword.getWindowToken(), 0);

				attemptLogin(attemptUsername, attemptPassword);
			}
		});
	}

	private void loginRedirect() {
		Intent showNewsFeedIntent = new Intent(this, MainActivity.class);
		startActivity(showNewsFeedIntent);
		finish();
	}

	private void attemptLogin(EditText attemptUsername, EditText attemptPassword) {
		AsyncHttpClient client = new AsyncHttpClient();

		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		client.setCookieStore(cookieStore);

		RequestParams params = new RequestParams();
		params.put("username", attemptUsername.getText().toString());
		params.put("password", attemptPassword.getText().toString());

		client.post("http://192.168.1.42:5000/login/", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						LinearLayout loginStatus = (LinearLayout) findViewById(R.id.login_status);
						loginStatus.setVisibility(View.VISIBLE);
					}

					@Override
					public void onSuccess(String response) {
						if (response.equals("bad_user_or_pass")) {
							TextView view = (TextView) findViewById(R.id.login_response);
							view.setText("Invalid username or password");

							LinearLayout loginStatus = (LinearLayout) findViewById(R.id.login_status);
							loginStatus.setVisibility(View.GONE);

						} else {
							loginRedirect();
						}
					}

					@Override
					public void onFailure(Throwable e, String response) {
						LinearLayout loginStatus = (LinearLayout) findViewById(R.id.login_status);
						loginStatus.setVisibility(View.GONE);
					}

				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
}