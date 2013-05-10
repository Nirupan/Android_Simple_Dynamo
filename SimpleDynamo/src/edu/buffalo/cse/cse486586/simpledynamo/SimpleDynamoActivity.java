package edu.buffalo.cse.cse486586.simpledynamo;



import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SimpleDynamoActivity extends Activity {

	private Button butPut1;
	private Button butPut2;
	private Button butPut3;
	private Button butGet;
	private Button butDump;
	
	private TextView textOut;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_dynamo);

		butPut1 = (Button) findViewById(R.id.button1);
		butPut2 = (Button) findViewById(R.id.button2);
		butPut3 = (Button) findViewById(R.id.button3);
		butGet = (Button) findViewById(R.id.button5);
		butDump = (Button) findViewById(R.id.button4);
		textOut = (TextView) findViewById(R.id.textView1);

		butPut1.setOnClickListener(put1);
		butPut2.setOnClickListener(put2);
		butPut3.setOnClickListener(put3);
		butGet.setOnClickListener(get);
		butDump.setOnClickListener(dump);
		
	}

	private OnClickListener put1 = new OnClickListener() {
		public void onClick(View v) {

			AccessGlobalData.flag = true;
			// GlobalData.seqNo = 0;
			for (AccessGlobalData.seqNo = 0; AccessGlobalData.seqNo < 10; AccessGlobalData.seqNo++) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				insertCV("" + AccessGlobalData.seqNo, "TEST1" + AccessGlobalData.seqNo);
			}

		}

	};

	private OnClickListener put2 = new OnClickListener() {
		public void onClick(View v) {

			AccessGlobalData.flag = true;
			for (AccessGlobalData.seqNo = 0; AccessGlobalData.seqNo < 10; AccessGlobalData.seqNo++) {
				insertCV("" + AccessGlobalData.seqNo, "TEST2" + AccessGlobalData.seqNo);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	private OnClickListener put3 = new OnClickListener() {
		public void onClick(View v) {

			AccessGlobalData.flag = true;
			for (AccessGlobalData.seqNo = 0; AccessGlobalData.seqNo < 10; AccessGlobalData.seqNo++) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				insertCV("" + AccessGlobalData.seqNo, "TEST3" + AccessGlobalData.seqNo);
			}

		}
	};

	private OnClickListener get = new OnClickListener() {
		public void onClick(View v) {
			StringBuffer buffer;

			for (AccessGlobalData.seqNo = 0; AccessGlobalData.seqNo < 10; AccessGlobalData.seqNo++) {
				buffer = query("" + AccessGlobalData.seqNo, 0);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				textOut.append(buffer);
			}

		}

	};

	private OnClickListener dump = new OnClickListener() {
		public void onClick(View v) {
			query(null, 1);
		}

	};

	private OnClickListener exitListener = new OnClickListener() {
		public void onClick(View v) {
			// ServerThread.stopServer();
			Log.d("DroidChatActivity", "S: Server Stopped");
			System.exit(0);
		}
	};

	public void insertCV(String key, String message) {
		try {

			// generate appropriate Uri
			Uri uri = Uri
					.parse("content://" + SimpleDynamoProvider.AUTHORITY + "/MessageT");
			if (uri == null)
				throw new IllegalArgumentException();
			ContentValues valuesCV = new ContentValues();
			valuesCV.put(KeyValueBase.provider_key, key);
			valuesCV.put(KeyValueBase.provider_value, message);

			// insert into ContentProvider i.e MessageProvider for my app
			getContentResolver().insert(uri, valuesCV);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public StringBuffer query(String key, int _case) {
		StringBuffer buff = new StringBuffer();
		try {
			boolean flag = false;
			Uri uri = Uri
					.parse("content://" + SimpleDynamoProvider.AUTHORITY + "/MessageT");
			Cursor cur;
			if (_case == 0) {
				// for "get" operation
				cur = getContentResolver().query(uri, null, key, null,
						KeyValueBase.provider_key + " ASC");
			} else {
				// for "dump" operation
				cur = getContentResolver().query(uri, null, null, null,
						KeyValueBase.provider_key + " ASC");

			}
			Log.d("SimpleDHTActivity", "Received Some Cursor");

			if (cur.getCount() > 0) {
				cur.moveToFirst();

				int keyColumn = cur.getColumnIndex(KeyValueBase.provider_key);
				int valueColumn = cur.getColumnIndex(KeyValueBase.provider_value);
				if (_case == 0) {
					while (!cur.isLast()) {
						if (cur.getString(keyColumn).contentEquals(key)) {
							buff.append("		" + cur.getString(keyColumn)
									+ "         	" + cur.getString(valueColumn)
									+ "\n");
							flag = true;
							Log.d("Activity", "Gte operation for key:" + key
									+ " successful");
							break;
						}
						cur.moveToNext();
					}
					if (cur.getString(keyColumn).contentEquals(key)
							&& flag == false)
						buff.append("		" + cur.getString(keyColumn)
								+ "         	" + cur.getString(valueColumn)
								+ "\n");

				} else {
					while (!cur.isLast()) {
						buff.append("		" + cur.getString(keyColumn)
								+ "         	" + cur.getString(valueColumn)
								+ "\n");
						cur.moveToNext();
					}
					buff.append("		" + cur.getString(keyColumn) + "         	"
							+ cur.getString(valueColumn) + "\n");
					textOut.append("" + KeyValueBase.provider_key + ""
							+ KeyValueBase.provider_value + "\n");
					textOut.append(buff);

				}
			} else {
				Log.d("Activity: Display Button", "Empty");
				buff.append("No Entry Yet!");
			}

			cur.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return buff;
	}
}