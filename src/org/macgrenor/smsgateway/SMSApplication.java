package org.macgrenor.smsgateway;

import org.macgrenor.smsgateway.data.DataProvider;
import org.macgrenor.smsgateway.services.ProcessSMS;
import org.macgrenor.smsgateway.services.ServiceCommunicator;

import android.app.Application;
import android.content.Intent;
import android.os.Looper;

public class SMSApplication extends Application {
	public static final String TAG = "SMSApplication";

    public static Application app = null;
    public static ProcessSMS ProcessThread;

    @Override
    public void onCreate() {
        super.onCreate();

        // Open database connection when the application starts.
        app = this;
        Preferences.loadPreferences(app);
        DataProvider.initDataProvider(app);
        ProcessThread = new ProcessSMS();
        ProcessThread.start();
        
        (new Thread() {
			
			@Override
			public void run() {
				try {
					sleep(500);
				} catch (InterruptedException e) {	}
				Intent myIntent = new Intent(app, ServiceCommunicator.class);
				app.startService(myIntent);
				
			}
		}).start();
        
    }

    @Override
    public void onTerminate() {

        // Close the database when the application terminates.
    	try { DataProvider.getDataProvider().close(); } catch (Exception e) { }
        super.onTerminate();
    }
}
