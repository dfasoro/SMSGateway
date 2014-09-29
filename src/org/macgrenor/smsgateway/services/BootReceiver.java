package org.macgrenor.smsgateway.services;

import org.macgrenor.smsgateway.MainScreen;
import org.macgrenor.smsgateway.SMSApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SMSApplication.writeToFile("Bootup");
		SMSApplication.startFromBoot = true;
		
		Intent myIntent = new Intent(context, ServiceCommunicator.class);
		context.startService(myIntent);
		SMSApplication.writeToFile("After Start Service");

        //Start App On Boot Start Up
        Intent App = new Intent(context, MainScreen.class);
        App.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(App);
        
        SMSApplication.writeToFile("After Start Activity");
	}
}
