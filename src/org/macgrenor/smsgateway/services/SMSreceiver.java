package org.macgrenor.smsgateway.services;

import org.macgrenor.smsgateway.MainScreen;
import org.macgrenor.smsgateway.Preferences;
import org.macgrenor.smsgateway.SMSApplication;
import org.macgrenor.smsgateway.data.DataProvider;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSreceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!Preferences.captureMessages) return;
		
		long thisTimeStamp = System.currentTimeMillis();

		if (intent.getExtras() != null) {
			int SimId = intent.getExtras().getInt("simId", 0);
		    SimId += 1;
		    
			SmsMessage[] messages = getMessagesFromIntent(intent);
			SmsMessage sms = messages[0];
			if (messages != null) {
				String From;
				String Body;
				long Timestamp;

				// extract message details. phone number and the message body
				From = sms.getOriginatingAddress();
				Timestamp = sms.getTimestampMillis();
				
				String sender = From;
				if (sender.startsWith("+")) sender = sender.substring(1);
				sender = sender.replace("-", "");
				if (sender.length() < ProcessSMS.PHONE_MIN_LEN || !PhoneNumberUtils.isGlobalPhoneNumber(From)) return;
				

				if (messages.length == 1 || sms.isReplace()) {
					Body = sms.getDisplayMessageBody();

				} else {
					StringBuilder bodyText = new StringBuilder();
					for (int i = 0; i < messages.length; i++) {
						bodyText.append(messages[i].getMessageBody());
					}
					Body = bodyText.toString();
				}
				String serviceCenter = "" + SimId;

				
				//Save to DB.
				ContentValues values = new ContentValues(15);
				values.put("Sender", From);
				values.put("ServiceCenter", serviceCenter);
				values.put("Message", Body);
				values.put("Pages", messages.length);

				values.put("Processed", 1);
				values.put("Valid", 1);
				values.put("Responded", 1);
				
				values.put("MessageDate", ProcessSMS.convertMilliSecondsToDate(Timestamp));
				values.put("StoreDate", ProcessSMS.convertMilliSecondsToDate(thisTimeStamp));
				
				int mRecordId = (int)DataProvider.getDataProvider().insert("messages",	values);
				
				try {
					MainScreen.mScreen.message_list.add(0, new MessageItem(mRecordId, From, Body));
					//if (MainScreen.mScreen.message_list.size() > Preferences.DISPLAY_LENGTH) {
						//MainScreen.mScreen.message_list.remove(Preferences.DISPLAY_LENGTH - 1);
					//}

					MainScreen.mScreen.adapter.notifyDataSetChanged();
				}
				catch (Exception e) {
					Log.e(getClass().getName(), "List Injection", e);					
				}
				
				SMSApplication.ProcessThread.interrupt();
			}
		}
	}

	private SmsMessage[] getMessagesFromIntent(Intent intent) {

		Log.i(getClass().getName(), "Incoming Message");

		Object[] messages = (Object[]) intent.getSerializableExtra("pdus");

		if (messages == null) {
			return null;
		}

		if (messages.length == 0) {
			return null;
		}

		byte[][] pduObjs = new byte[messages.length][];

		for (int i = 0; i < messages.length; i++) {
			pduObjs[i] = (byte[]) messages[i];
		}

		byte[][] pdus = new byte[pduObjs.length][];
		int pduCount = pdus.length;

		SmsMessage[] msgs = new SmsMessage[pduCount];
		for (int i = 0; i < pduCount; i++) {
			pdus[i] = pduObjs[i];
			msgs[i] = SmsMessage.createFromPdu(pdus[i]);
			if (msgs[i].isStatusReportMessage())
				return null;
		}
		return msgs;
	}

}
