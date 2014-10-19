package org.macgrenor.smsgateway.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.macgrenor.smsgateway.Preferences;
import org.macgrenor.smsgateway.SMSApplication;
import org.macgrenor.smsgateway.data.ClientHttpRequest;
import org.macgrenor.smsgateway.data.DataProvider;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import au.com.bytecode.opencsv.CSVWriter;

public class ProcessSMS extends Thread {
	public static final int PHONE_MIN_LEN = 8;
	private CSVWriter writer = null;
	private String csvHeaderLine = null;
	
	
	public ProcessSMS() {
		writer = new CSVWriter(null);
		csvHeaderLine = messageCSVHeaders();
	}
	
	@Override
	public void run() {
		//read DB for unprocessed threads ...
		synchronized(this) {
			try {
				wait(5 * 1000); //wait 5 secs
			} catch (InterruptedException e) { }
		}
		
		while (true) {
			SQLiteQueryBuilder qb1 = new SQLiteQueryBuilder();
			qb1.setTables("messages");
			String whereStatement = "Posted = 0";
			
			Cursor msgs = DataProvider.getDataProvider().query(qb1, null, whereStatement, null, "ID ASC", null);
			
			if (!msgs.moveToFirst()) {
				//No Item to process in database
				msgs.close();
				
				synchronized(this) {
					try {
						wait(5 * 60 * 1000); //wait 5 minutes
					} catch (InterruptedException e) { }
				}
				
				continue;
			}
			
			while (!msgs.isAfterLast()) {
				
				//process me...
				int recordId = msgs.getInt(0);
				String sender = msgs.getString(1);
				String senderOriginal = sender;
				String message = msgs.getString(3);
				int Pages = msgs.getInt(4);
				String serviceCenter = msgs.getString(2);
				String messageDate = msgs.getString(9);
				
				String criteria = "ID = " + recordId;				
				ContentValues values = new ContentValues(5);
				
				String syncUrl = "";
				//detect POST URL.
				
				if (serviceCenter != null) {
					//String smsc_split[] = Preferences.SMSCS.spl
					StringTokenizer smsc_split = new StringTokenizer(Preferences.SMSCS, "\n");
					StringTokenizer url_split = new StringTokenizer(Preferences.URLS, "\n");
					
					int posChecking = -1; int posFound = -1;
					while (smsc_split.hasMoreTokens()) {
						posChecking++;
						if (serviceCenter.startsWith(smsc_split.nextToken().trim())) {
							posFound = posChecking;
							break;
						}
					}
					
					if (posFound != -1) {
						int posCounter = -1;
						while (url_split.hasMoreTokens()) {
							posCounter++;
							String url_here = url_split.nextToken().trim();
							if (posFound == posCounter) {
								syncUrl = url_here;
								break;
							}
						}
						
						if (syncUrl == "" && posFound != -1) {
							//error here ...
						}
					}
				}
				
				if (syncUrl.length() >= 8) {
					String resp = null;
					
					ClientHttpRequest conn = null;
					try {
						conn = new ClientHttpRequest(syncUrl, 5 * 1000);
						conn.setParameter("Secret", Preferences.Secret);
						conn.setParameter("id", recordId);						
						conn.setParameter("sender", senderOriginal);
						conn.setParameter("date", messageDate);
						conn.setParameter("message", message);
						
						resp = conn.postAndRetrieve(); //Push to Server
					} catch (Exception e1) {
						resp = null;
						e1.printStackTrace();
					}
					finally {
						if (conn != null) conn.closeAll();
					}
					
					if (resp == null) {
						try {
							Thread.sleep(5000); //Wait for 5secs cos there was a conn error.
						} catch (InterruptedException e) { }
					}
					else {					
						if (resp.equals("ok")) {
							values.put("Posted", 1);
							DataProvider.getDataProvider().update("messages", values, criteria, null);
						}
					}
				}
				else {
					values.put("Posted", 1);
					DataProvider.getDataProvider().update("messages", values, criteria, null); //Unknown SMSC match to URL here...
				}
				
				msgs.moveToNext();
			}
			
			msgs.close();
		}
	}
	
	public static String convertMilliSecondsToDate(long milliSeconds)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+1")); 
		
		return sdf.format(new Date(milliSeconds));
	}
 
	public String messageToCSV(Cursor msg, ContentValues updates) {
		String[] outputs = new String[12];
		
		outputs[0] = String.valueOf(msg.getInt(0)); //ID
		outputs[1] = msg.getString(1); //Sender
		outputs[2] = msg.getString(2); //ServiceCenter
		outputs[3] = msg.getString(3); //Message
		outputs[4] = String.valueOf(msg.getInt(4)); //Pages
		
		outputs[5] = String.valueOf(updates.containsKey("Processed") ? updates.getAsInteger("Processed") : msg.getInt(5)); //Processed
		outputs[6] = String.valueOf(updates.containsKey("Valid") ? updates.getAsInteger("Valid") : msg.getInt(6)); //Valid
		outputs[7] = String.valueOf(updates.containsKey("Responded") ? updates.getAsInteger("Responded") : msg.getInt(7)); //Responded
		outputs[8] = String.valueOf(updates.containsKey("Posted") ? updates.getAsInteger("Posted") : msg.getInt(8)); //Posted
		
		outputs[9] = msg.getString(9); //MessageDate
		outputs[10] = msg.getString(10); //StoreDate
		outputs[11] = Preferences.uniqueId; //UniqueID
		
		return writer.writeNext(outputs);
	}
	
	public String messageCSVHeaders() {
		String[] outputs = new String[12];
		
		outputs[0] = "ID";
		outputs[1] = "Sender";
		outputs[2] = "ServiceCenter";
		outputs[3] = "Message";
		outputs[4] = "Pages";
		
		outputs[5] = "Processed";
		outputs[6] = "Valid";
		outputs[7] = "Responded";
		outputs[8] = "Posted";
		
		outputs[9] = "MessageDate";
		outputs[10] = "StoreDate";
		outputs[11] = "UniqueID";
		
		return writer.writeNext(outputs);
	}
}
