package org.macgrenor.smsgateway;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

	public static String Secret = "";

	public static String SMSCS = "";
	public static String URLS = "";

	public static String uniqueId = "";
	
	public static Boolean captureMessages = false;

	public static final String PREF_NAME = "SMS_PREF";
	public static final int DISPLAY_LENGTH = 20;
	

	/**
	 * Load the value of the settings / preference variable.
	 * 
	 * @param Context
	 *            context - The context of the calling activity.
	 * @return void
	 */
	public static void loadPreferences(Context context) {

		final SharedPreferences settings = context.getSharedPreferences(
				PREF_NAME, 0);
		Secret = settings.getString("Secret", "");
		SMSCS = settings.getString("SMSCSPref", "1\n2");
		URLS = settings.getString("URLSPref", "http://firstone.com/\nhttp://secondone.com");
		uniqueId = settings.getString("UniqueId", "");
		captureMessages = settings.getBoolean("captureMessages", false);
	}
}
