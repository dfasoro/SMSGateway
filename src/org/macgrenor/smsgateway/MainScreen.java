package org.macgrenor.smsgateway;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.macgrenor.smsgateway.R;
import org.macgrenor.smsgateway.data.ClientHttpRequest;
import org.macgrenor.smsgateway.data.DataProvider;
import org.macgrenor.smsgateway.services.MessageItem;
import org.macgrenor.smsgateway.services.SMSreceiver;

import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainScreen extends Activity {
	
	public ListView MessageLists = null;
	public ArrayAdapter<MessageItem> adapter = null;
	public ArrayList<MessageItem> message_list = new ArrayList<MessageItem>(20);
	public static MainScreen mScreen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		setTitle("SMS Gateway (Most Recent Messages)");
		
		MessageLists = (ListView)findViewById(R.id.view_messages);
		//message_list = {"Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama"};
		
		//String message_list1[]  = {"Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama", "Wuse", "Garki", "Maitama"};
				
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables("messages");
		
		String proj[] = {"ID", "Sender", "Message"};
		String criteria = null;
		
		Cursor c = DataProvider.getDataProvider().query(qb, proj, criteria, null, "ID DESC", "" + Preferences.DISPLAY_LENGTH);
		
		try { c.moveToFirst(); } catch (Exception e) { }
		
		while (!c.isAfterLast()) {
			message_list.add(new MessageItem(c.getInt(0), c.getString(1), c.getString(2)));
			c.moveToNext();
		}
		
		c.close();
		
		adapter=new ArrayAdapter<MessageItem>(this, android.R.layout.simple_list_item_1, message_list);
		MessageLists.setAdapter(adapter);
		
		mScreen = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		//if (!canMoveOn) {
			//return false;
		//}

		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this,  Settings.class);
			startActivity(intent);;
			return true;
		case R.id.export_csv:
			exportDatabasetoFile();
			return true;
		case R.id.flush_sms_cache:
			SMSreceiver.flushSMSCache();
			return true;
		}
		return true;
	}

	

	private void exportDatabasetoFile() {
		
		new Thread() {
			public void run() {
				try {
					if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
						showToast("Media Storage is not ready. Ensure it is not mounted on the computer!");
						return;
					}
					
					String directory = "/SMSGateway/";
					String filename = DateUtils.formatDateTime(SMSApplication.app, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_DATE + DateUtils.FORMAT_SHOW_YEAR) + " " 
					+ DateUtils.formatDateTime(SMSApplication.app, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME) + ".csv";
					
					filename = filename.replace(':', '_').replace(' ', '_');
					
					File mydirectory = new File(Environment.getExternalStorageDirectory(), directory);
					mydirectory.mkdirs();
					
					SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
					qb.setTables("messages");
					String criteria = "Exported = 0 AND processed <> 0";		
					
					Cursor msgs = DataProvider.getDataProvider().query(qb, null, criteria, null, "ID ASC", null);
					
					if (!msgs.moveToFirst()) {
						showToast("No available message(s) to export!");			
						msgs.close();
						return;
					}
					
					File outputFile = new File(mydirectory, filename);
					
					showToast("Starting message export to " + outputFile.getAbsolutePath());
					
					// now attach the OutputStream to the file object, instead of a String representation
					FileWriter fos = new FileWriter(outputFile);
					
					fos.write(SMSApplication.ProcessThread.messageCSVHeaders());
					
					while (!msgs.isAfterLast()) {
						fos.write(SMSApplication.ProcessThread.messageToCSV(msgs, new ContentValues(1)));
						
						String criteria2 = "ID = " + msgs.getInt(0);				
						ContentValues values2 = new ContentValues(5);
						values2.put("Exported", 1);
						DataProvider.getDataProvider().update("messages", values2, criteria, null);
						
						msgs.moveToNext();
					}
					
					fos.close();
					msgs.close();	
					
					showToast("Successfully exported messages to " + filename);
					return;
				}
				catch (Exception ex) {
					showToast("Export to CSV Failed");
					Log.e(MainScreen.class.getName(), "Export to CSV Failed", ex);
				}
			}
		}.start();
		
	}
	
	public void showToast(final String toast)
	{
	    runOnUiThread(new Runnable() {
	        public void run()
	        {
	            Toast.makeText(MainScreen.this, toast, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
}
