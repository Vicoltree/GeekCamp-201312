package com.example.day_2_test;

import com.example.aidls.IRemoteService;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.sax.StartElementListener;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private IRemoteService binder = null;
	protected ServiceConnection bindRemoteService = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			binder = null;
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			binder = IRemoteService.Stub.asInterface(arg1);
		}
	};
	private OnClickListener serviceClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bindServerBtn:
				bindService(new Intent(
						"com.example.day_2.intent.action.EchoService"),
						bindRemoteService, BIND_AUTO_CREATE);
				break;
			case R.id.unbindServerBtn:
				unbindService(bindRemoteService);
				binder = null;
				break;
			case R.id.getIndexBtn:
				if (binder != null) {
					try {
						Toast.makeText(MainActivity.this, "indexÖµÎª£º" + binder.getIndex(),
								Toast.LENGTH_SHORT).show();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			}

		}
	};
	// com.example.day_2.intent.action.EchoService
	Button bindBtn, unbindBtn, getIndexBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bindBtn = (Button) findViewById(R.id.bindServerBtn);
		unbindBtn = (Button) findViewById(R.id.unbindServerBtn);
		getIndexBtn = (Button) findViewById(R.id.getIndexBtn);
		bindBtn.setOnClickListener(serviceClickListener);
		unbindBtn.setOnClickListener(serviceClickListener);
		getIndexBtn.setOnClickListener(serviceClickListener);

	}

}
