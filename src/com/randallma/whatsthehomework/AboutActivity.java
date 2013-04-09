package com.randallma.whatsthehomework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		setTitle("About");

		TextView v = (TextView) findViewById(R.id.about);
		v.setText(Html
				.fromHtml("<p>What's the Homework (WTH) is an Android homework manager/notifier by <a href=\"http://randallma.com\">Randall Ma</a>. WTH is a free, open source project available on <a href=\"http://github.com/randallm/whatsthehomework_android\">GitHub</a>. It is licensed under the Apache License 2.0. The full text of the license can be found at <a href=\"https://www.apache.org/licenses/LICENSE-2.0\">apache.org</a></p><p>\"Undo Bar\" and \"Swipe to Dismiss\" code, by Roman Nurik, is included in this project. These projects are licensed under the Apache License, Version 2.0. The full text of the license can be found at <a href=\"https://www.apache.org/licenses/LICENSE-2.0\">apache.org</a></p>"));
		v.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public void onBackPressed() {
		Intent mainActivityIntent = new Intent(AboutActivity.this,
				MainActivity.class);
		mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(mainActivityIntent);
		finish();
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent mainActivityIntent = new Intent(AboutActivity.this,
				MainActivity.class);
		mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(mainActivityIntent);
		finish();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

}
