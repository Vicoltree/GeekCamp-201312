package com.example.demo_json;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	ListView lv = null;
	ArrayList<String> myList = new ArrayList<String>();
	static String more_url = null;
	static String urlContent = null;
	ProgressDialog dialog;
	static int selectId = 0;

	// "http://api.eoe.cn/client/blog?k=lists&t=top";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new Thread(runnable).start();
		dialog = ProgressDialog.show(this, "������", "���ڼ���.....");

	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {

			more_url = more_url != null ? more_url
					: "http://api.eoe.cn/client/blog?k=lists&t=top";
			urlContent = getUrlContent(more_url);

			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("content", urlContent);
			msg.setData(data);
			handler.sendMessage(msg);
		}
	};

	private Handler handler = new Handler() {

		private OnScrollListener listener = new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// ���һ����¼����ĩβʱ��
				if (arg0.getLastVisiblePosition() == arg0.getCount() - 1) {
					selectId = arg0.getLastVisiblePosition() - 7;
					// ��ǰ�����¼�Ҳ�������̣߳��޷�ֱ�ӷ������磻
					// �ٴε����̣߳���ȡ���һ�εķ�ҳ���ӣ������ص�listview �У�
					Toast toast = Toast.makeText(getApplicationContext(),
							"loading", Toast.LENGTH_SHORT);
					// ������ʾ��
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					new Thread(runnable).start();
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

			}
		};

		public void handleMessage(Message msg) {

			Bundle data = msg.getData();
			String content = data.getString("content");
			// ������ݣ�
			myList = jsonCode(content);

			dialog.dismiss();

			// ͨ��adapter ���ݸ�list;
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					MainActivity.this,
					android.R.layout.simple_dropdown_item_1line, myList);
			// listview ����list;
			lv = (ListView) findViewById(R.id.lv);
			lv.setAdapter(adapter);
			// ����Ƿ�ҳ����λ���ϴε�λ�ã�
			if (MainActivity.selectId > 0) {
				lv.setSelection(MainActivity.selectId);
				adapter.notifyDataSetChanged();
			}
			// ��ӻ��������¼���
			lv.setOnScrollListener(listener);
		};
	};

	private static String getUrlContent(String url) {
		String myUrlString = null;

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			// �����ȡ�ļ����ݵ�url��
			URL myURL = new URL(url);
			// ��ʼ����;
			URLConnection urlConnection = myURL.openConnection();
			// ��ȡ����;
			InputStream input = urlConnection.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(input);
			// ����;
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int len;
			while ((len = bis.read()) != -1) {
				baf.append((byte) len);
			}
			// ����������ת��ΪString ,��utf-8��
			myUrlString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return myUrlString;
	}

	private ArrayList<String> jsonCode(String content) {

		try {
			JSONObject jsonObject = new JSONObject(content);
			JSONArray array = jsonObject.getJSONObject("response")
					.getJSONArray("items");
			// ��ȡ��ҳurl��
			more_url = jsonObject.getJSONObject("response").getString(
					"more_url");
			// try {
			// String more_url2 = jsonObject.getJSONObject("response")
			// .getJSONObject("more_url").toString();
			// Log.i("object", more_url2);
			//
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			int lenght = array.length();

			for (int i = 0; i < lenght; i++) {
				JSONObject arrayobject = array.getJSONObject(i);
				myList.add(arrayobject.getString("title").toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return myList;
	}

}
