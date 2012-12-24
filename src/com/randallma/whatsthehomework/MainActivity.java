package com.randallma.whatsthehomework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

public class MainActivity extends Activity {
	
	public void login() {
		Intent loginActivityIntent = new Intent(this, LoginActivity.class);
		startActivity(loginActivityIntent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// redirect if logged in

		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		AsyncHttpClient clientSession = g.getClientSession();
		PersistentCookieStore cookieStore = g.getCookieStore();
		
		if(cookieStore == null) {
			login();
		} else {
			System.out.println("cookies exist!");
		}
		
//		Button loginButton = (Button) findViewById(R.id.loginButton);
//		loginButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				login(view);
//			}
//		});
		
		// otherwise, display news feed
		
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
}
