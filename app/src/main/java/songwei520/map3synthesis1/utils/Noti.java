package songwei520.map3synthesis1.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import songwei520.map3synthesis1.R;

public class Noti {

	public static Context context;
	
	public static void createChannel(Context mContext){
		context = mContext;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			return;
		}
		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Uri mUri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.alarm);

		String name = SharedPreferencesNameFile.APPName + "-hasSound-hasVibration";
		NotificationChannel mChannel = new NotificationChannel(
				name, name, NotificationManager.IMPORTANCE_HIGH);

		mChannel.setSound(mUri, Notification.AUDIO_ATTRIBUTES_DEFAULT);
		mChannel.enableVibration(true);
		mChannel.setVibrationPattern(new long[]{
				100, 200, 300
		});

		notificationManager.createNotificationChannel(mChannel);

		name = SharedPreferencesNameFile.APPName + "-noSound-noVibration";
		mChannel = new NotificationChannel(
				name, name, NotificationManager.IMPORTANCE_HIGH);
		mChannel.setSound(null, null);
		mChannel.enableVibration(false);
		notificationManager.createNotificationChannel(mChannel);

		name = SharedPreferencesNameFile.APPName + "-hasSound-noVibration";
		mChannel = new NotificationChannel(
				name, name, NotificationManager.IMPORTANCE_HIGH);
		mChannel.setSound(mUri, Notification.AUDIO_ATTRIBUTES_DEFAULT);
		mChannel.enableVibration(false);
		notificationManager.createNotificationChannel(mChannel);

		name = SharedPreferencesNameFile.APPName + "-noSound-hasVibration";
		mChannel = new NotificationChannel(
				name, name, NotificationManager.IMPORTANCE_HIGH);
		mChannel.setSound(null, null);
		mChannel.enableVibration(true);
		mChannel.setVibrationPattern(new long[]{
				100, 200, 300
		});
		notificationManager.createNotificationChannel(mChannel);

	}
	public static void sendNoti(Context mContext, String title, String content, Intent intent, int NotiID){
		context = mContext;
		PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = null;
		SharedPreferencesHelper sp = new SharedPreferencesHelper(mContext);
		boolean isVibration = (Boolean) sp.getSharedPreference("IsNotiVibrate",false);
		boolean isSound = (Boolean) sp.getSharedPreference("IsNotiSound",false);
		Bitmap btp = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			String id = SharedPreferencesNameFile.APPName + "-hasSound-hasVibration";

			if (isVibration  && isSound){
				id = SharedPreferencesNameFile.APPName + "-hasSound-hasVibration";
			}else if(!isVibration && !isSound){
				id = SharedPreferencesNameFile.APPName + "-noSound-noVibration";
			}else if(!isVibration && isSound){
				id = SharedPreferencesNameFile.APPName + "-hasSound-noVibration";
			}else if(isVibration && !isSound){
				id = SharedPreferencesNameFile.APPName + "-noSound-hasVibration";
			}
			notification = new Notification.Builder(mContext)
					.setChannelId(id)
					.setContentTitle(title)
					.setContentText(content)
					.setSmallIcon(R.drawable.poi)
					.setLargeIcon(btp)
					.setContentIntent(mPendingIntent)
					.setAutoCancel(true)
					.build();
			if (isVibration)
				notification.defaults = Notification.DEFAULT_VIBRATE;
			if (isSound)
				notification.sound = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.alarm);
    	   /* NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
    	            .setContentTitle(title)
    	            .setContentText(content)
    	            .setSmallIcon(R.drawable.icon)
    	            .setContentIntent(mPendingIntent)
    	            //.setOngoing(true)
    	            .setAutoCancel(true);
    	    if (AppData.GetInstance(mContext).getAlertVibration())
    	    	notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
    	    if (AppData.GetInstance(mContext).getAlertSound())
    	    	notificationBuilder.setSound(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.alarm));
    	    notification = notificationBuilder.build();*/
		} else {
			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
					.setContentTitle(title)
					.setContentText(content)
					.setSmallIcon(R.drawable.poi)
					.setContentIntent(mPendingIntent)
					.setLargeIcon(btp)
					//.setOngoing(true)
					.setAutoCancel(true);
			if (isVibration)
				notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
			if (isSound)
				notificationBuilder.setSound(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.alarm));
			notification = notificationBuilder.build();
		}
		notificationManager.notify(NotiID, notification);
	}
	
	public static void sendNoti(Context mContext, String title, String content, Intent intent, int NotiID, boolean isVibration, boolean isSound){
		context = mContext;
		PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = null;
		Bitmap btp = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			String id = SharedPreferencesNameFile.APPName + "-hasSound-hasVibration";

			if (isVibration  && isSound){
				id = SharedPreferencesNameFile.APPName + "-hasSound-hasVibration";
			}else if(!isVibration && !isSound){
				id = SharedPreferencesNameFile.APPName + "-noSound-noVibration";
			}else if(!isVibration && isSound){
				id = SharedPreferencesNameFile.APPName + "-hasSound-noVibration";
			}else if(isVibration && !isSound){
				id = SharedPreferencesNameFile.APPName + "-noSound-hasVibration";
			}
			notification = new Notification.Builder(mContext)
					.setChannelId(id)
					.setContentTitle(title)
					.setContentText(content)
					.setSmallIcon(R.drawable.poi)
					.setLargeIcon(btp)
					.setContentIntent(mPendingIntent)
					.setAutoCancel(true)
					.build();
			if (isVibration)
				notification.defaults = Notification.DEFAULT_VIBRATE;
			if (isSound)
				notification.sound = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.alarm);
    	   /* NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
    	            .setContentTitle(title)
    	            .setContentText(content)
    	            .setSmallIcon(R.drawable.icon)
    	            .setContentIntent(mPendingIntent)
    	            //.setOngoing(true)
    	            .setAutoCancel(true);
    	    if (AppData.GetInstance(mContext).getAlertVibration())
    	    	notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
    	    if (AppData.GetInstance(mContext).getAlertSound())
    	    	notificationBuilder.setSound(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.alarm));
    	    notification = notificationBuilder.build();*/
		} else {
			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
					.setContentTitle(title)
					.setContentText(content)
					.setSmallIcon(R.drawable.poi)
					.setContentIntent(mPendingIntent)
					.setLargeIcon(btp)
					//.setOngoing(true)
					.setAutoCancel(true);
			if (isVibration)
				notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
			if (isSound)
				notificationBuilder.setSound(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.alarm));
			notification = notificationBuilder.build();
		}
		notificationManager.notify(NotiID, notification);
	}
}
