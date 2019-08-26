package songwei520.map3synthesis1.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;


import songwei520.map3synthesis1.R;
import songwei520.map3synthesis1.utils.Noti;
import songwei520.map3synthesis1.utils.SharedPreferencesHelper;

public class MService extends Service {
	private Thread notiThread = null;
	private boolean firstLoad = true;
	private MService mContext;
	private SharedPreferencesHelper sp;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		sp = new SharedPreferencesHelper(this);
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Intent notificationIntent = new Intent();
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		Notification notification;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationChannel mChannel = new NotificationChannel(getString(R.string.app_name), getString(R.string.app_name), NotificationManager.IMPORTANCE_MAX);
			mChannel.enableLights(false);
			mChannel.setShowBadge(false);
			mChannel.enableVibration(false);
			mChannel.setSound(null, null);
			notificationManager.createNotificationChannel(mChannel);
			notification = new Notification.Builder(this)
							.setChannelId(getString(R.string.app_name))
							.setAutoCancel(true).setContentIntent(pendingIntent)
							.setWhen(System.currentTimeMillis()).build();
		} else {
			NotificationCompat.Builder notificationBuilder =
					new NotificationCompat.Builder(this)
							.setAutoCancel(true)
							.setContentIntent(pendingIntent)
							.setWhen(System.currentTimeMillis());
			notification = notificationBuilder.build();
		}
		notification.defaults = Notification.DEFAULT_VIBRATE;
		// 把该service创建为前台service
		startForeground(1, notification);
		try {
			notiThread.start();
		} catch (IllegalThreadStateException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (notiThread != null)
			notiThread.interrupt();
	}

	private void playSoundAndVibrate(){
		if((Boolean) sp.getSharedPreference("IsNotiVibrate",false)){
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			 long[] pattern = {0, 100};         
		     vibrator.vibrate(pattern,-1);
		}
		if((Boolean) sp.getSharedPreference("IsNotiSound",false)){
			playSound();
		}
	}

	private void playSound(){
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		MediaPlayer player = new MediaPlayer();
		try {
			player.setDataSource(this, alert);
			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
				player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
				
				player.setLooping(false);
				
				player.prepare();
				
				player.start();
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	
	private void sendNotify(int deviceID, String DeviceName, int type, String str) {
		//String DeviceName = mDeviceModel.getDeviceName();
		//String DeviceName = mDeviceDao.getDevice(deviceID).getDeviceName();
		Intent intent = new Intent();
//		if(sp.getSharedPreference("LoginMode",0) == 2)
//			intent.setClass(mContext, MainUser.class);
//		else
//			intent.setClass(mContext, MainDevice.class);
		intent.putExtra("type", type);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if(!havePlaySoundAndVibrate){
			Noti.sendNoti(mContext, DeviceName, str, intent, deviceID, (Boolean) sp.getSharedPreference("IsNotiVibrate",
					false), (Boolean) sp.getSharedPreference("IsNotiSound",false));
			havePlaySoundAndVibrate = true;
		}else{
			Noti.sendNoti(mContext, DeviceName, str, intent, deviceID, false, false);
		}
	}

	public static final int _GetNewWarn = 1;
	private boolean havePlaySoundAndVibrate = false;

	
}
