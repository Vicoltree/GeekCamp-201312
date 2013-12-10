package com.example.day_2;

import java.util.Timer;
import java.util.TimerTask;

import com.example.aidls.IRemoteService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class EchoService extends Service {

	//com.example.day_2.intent.action.EchoService
	private  final String TAG=EchoService.class.getSimpleName();
	public int index;
	
	private IRemoteService.Stub binder=new IRemoteService.Stub() {
		
		@Override
		public int getIndex() throws RemoteException {
			return index;
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		
		return binder;
	}
	Timer timer=new Timer();
	TimerTask task=null;
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		task=new TimerTask() {
			
			@Override
			public void run() {
				index++;
				System.out.println(index);
			}
		};
		timer.schedule(task, 1000, 1000);
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		task.cancel();
		timer.cancel();
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
}
