package org.macgrenor.smsgateway;

import java.util.regex.Pattern;
import org.macgrenor.smsgateway.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class Settings extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.prefs);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	protected void onPause() {
		super.onPause();

		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		SharedPreferences settings = SMSApplication.app.getSharedPreferences(
				Preferences.PREF_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		if (key.equalsIgnoreCase("captureMessages")) {
			editor.putBoolean(key, sharedPreferences.getBoolean(key, false));
		}
		else {
			editor.putString(key, sharedPreferences.getString(key, ""));
		}
				
		editor.commit();

		Preferences.loadPreferences(SMSApplication.app);
	}

}
