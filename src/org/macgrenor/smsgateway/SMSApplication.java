package org.macgrenor.smsgateway;

import java.io.File;
import java.io.FileWriter;

import org.macgrenor.smsgateway.data.DataProvider;
import org.macgrenor.smsgateway.services.ProcessSMS;
import org.macgrenor.smsgateway.services.ServiceCommunicator;

import android.app.Application;
import android.content.Intent;
import android.os.Environment;
import android.os.Looper;
import android.text.format.DateUtils;
import android.widget.Toast;

public class SMSApplication extends Application {
	public static final String TAG = "SMSApplication";
	
	public static ServiceCommunicator scomm = null;
	public static boolean startFromBoot = false;
    public static Application app = null;
    public static ProcessSMS ProcessThread;

    @Override
    public void onCreate() {
        super.onCreate();
        SMSApplication.writeToFile("On App Start");
        // Open database connection when the application starts.
        app = this;

        Preferences.loadPreferences(this.getApplicationContext());
        SMSApplication.writeToFile("Load Pref");
        DataProvider.initDataProvider(this.getApplicationContext());
        SMSApplication.writeToFile("Init Data provider");
        SMSApplication.ProcessThread = new ProcessSMS();
        SMSApplication.ProcessThread.start();
        
        if (startFromBoot == false) {
			Intent myIntent = new Intent(app, ServiceCommunicator.class);
			app.startService(myIntent);	
		}
        SMSApplication.writeToFile("Finish App creation");
        Toast.makeText(this, "Starting SMS Gateway", Toast.LENGTH_LONG).show();
        startFromBoot = false;
    }

    @Override
    public void onTerminate() {
    	try { scomm.stopSelf(); } catch (Exception e) { }
        // Close the database when the application terminates.
    	try { DataProvider.getDataProvider().close(); } catch (Exception e) { }
        super.onTerminate();
    }
    
    public static void writeToFile(String msg) {
    	if (true) return;
    	String directory = "/SMSGateway/";
		String filename =  "output.txt";
		File mydirectory = new File(Environment.getExternalStorageDirectory(), directory);
		mydirectory.mkdirs();
		
		File outputFile = new File(mydirectory, filename);
		
		// now attach the OutputStream to the file object, instead of a String representation
		try {
			FileWriter fos = new FileWriter(outputFile, true);
			fos.write(DateUtils.formatDateTime(SMSApplication.app, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_DATE + DateUtils.FORMAT_SHOW_YEAR) + " " 
					+ DateUtils.formatDateTime(SMSApplication.app, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME) +
					" : " + msg + "\r\n");
			fos.close();
		}
		catch (Exception e) { }
		
    }
}
