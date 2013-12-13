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
		dialog = ProgressDialog.show(this, "加载中", "正在加载.....");

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
				// 最后一条纪录到达末尾时；
				if (arg0.getLastVisiblePosition() == arg0.getCount() - 1) {
					selectId = arg0.getLastVisiblePosition() - 7;
					// 当前监听事件也属于主线程，无法直接访问网络；
					// 再次调用线程，获取最后一次的分页连接，并加载到listview 中；
					Toast toast = Toast.makeText(getApplicationContext(),
							"loading", Toast.LENGTH_SHORT);
					// 居中显示；
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
			// 存放数据；
			myList = jsonCode(content);

			dialog.dismiss();

			// 通过adapter 传递给list;
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					MainActivity.this,
					android.R.layout.simple_dropdown_item_1line, myList);
			// listview 加载list;
			lv = (ListView) findViewById(R.id.lv);
			lv.setAdapter(adapter);
			// 如果是分页。则定位到上次的位置；
			if (MainActivity.selectId > 0) {
				lv.setSelection(MainActivity.selectId);
				adapter.notifyDataSetChanged();
			}
			// 添加滑动监听事件；
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
			// 定义获取文件内容的url；
			URL myURL = new URL(url);
			// 开始连接;
			URLConnection urlConnection = myURL.openConnection();
			// 读取数据;
			InputStream input = urlConnection.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(input);
			// 缓存;
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int len;
			while ((len = bis.read()) != -1) {
				baf.append((byte) len);
			}
			// 将缓存内容转化为String ,用utf-8；
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
			// 获取分页url；
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
