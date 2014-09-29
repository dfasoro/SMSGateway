package org.macgrenor.smsgateway.services;

import org.macgrenor.smsgateway.Preferences;
import org.macgrenor.smsgateway.SMSApplication;
import org.macgrenor.smsgateway.data.DataProvider;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.IBinder;

public class ServiceCommunicator extends Service {

	
	private SMSreceiver mSMSreceiver;
    private IntentFilter mIntentFilter;

    @Override
    public void onCreate()
    {
        super.onCreate();
        SMSApplication.writeToFile("On Create Service");
        SMSApplication.scomm = this;
                
        //SMS event receiver
        mSMSreceiver = new SMSreceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSMSreceiver, mIntentFilter);
        SMSApplication.writeToFile("On Create SMS receiver");
        
        SMSreceiver.flushSMSCache();
        
        SMSApplication.writeToFile("On Flush Cache");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // Unregister the SMS receiver
        unregisterReceiver(mSMSreceiver);
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
