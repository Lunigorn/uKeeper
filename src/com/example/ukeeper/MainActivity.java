package com.example.ukeeper;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.os.Build;
import android.view.View;

public class MainActivity extends ActionBarActivity {
	private final static String SYSTEM_MODE = "system";
	private final static String BUILTIN_MODE = "builtin";
	private EditText usernameEdit;
	private EditText passwordEdit;
	private String mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		usernameEdit = (EditText) findViewById(R.id.usernameEdit);
		passwordEdit = (EditText) findViewById(R.id.passwordEdit);
		SharedPreferences sharedPref = getSharedPreferences("My", Context.MODE_PRIVATE);
		usernameEdit.setText(sharedPref.getString(
				getString(R.string.key_username), new String()));
		passwordEdit.setText(sharedPref.getString(
				getString(R.string.key_password), new String()));
		mode = sharedPref.getString(getString(R.string.key_mode), SYSTEM_MODE);
		if (mode.equals(SYSTEM_MODE)) {
			((RadioButton) findViewById(R.id.radio_system_mail_agent)).toggle();
			((View) findViewById(R.id.builtin_mail_view)).setVisibility(4);
		} else {
			((RadioButton) findViewById(R.id.radio_builtin_mail_agent))
					.toggle();
			((View) findViewById(R.id.builtin_mail_view)).setVisibility(0);
		}
	}

	public void modeClick(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch (view.getId()) {
		case R.id.radio_system_mail_agent:
			if (checked) {
				mode = SYSTEM_MODE;
				((View) findViewById(R.id.builtin_mail_view)).setVisibility(4);
			}
			break;
		case R.id.radio_builtin_mail_agent:
			if (checked)
				mode = BUILTIN_MODE;
			((View) findViewById(R.id.builtin_mail_view)).setVisibility(0);
			break;
		}
	}

	public void save(View view) {
		SharedPreferences sharedPref = getSharedPreferences("My",Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.key_username), usernameEdit
				.getText().toString());
		editor.putString(getString(R.string.key_password), passwordEdit
				.getText().toString());
		editor.putString(getString(R.string.key_mode), mode);
		editor.commit();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
