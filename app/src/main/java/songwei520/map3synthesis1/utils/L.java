package songwei520.map3synthesis1.utils;



import android.util.Log;

public class L {

	private static boolean isDebug = true;
	private static String TAG = "地图3合1" +"-----------";
	
	public static void i(String msg){
		if(isDebug){
			Log.i(TAG, msg);
		}
	}
	
	public static void i(String msg1, String msg2){
		if(isDebug){
			Log.i(TAG, msg1 + "--->" + msg2);
		}
	}
	
	public static void i(int msg){
		if(isDebug){
			Log.i(TAG, msg + "");
		}
	}
	
	public static void i(int msg1, int msg2){
		if(isDebug){
			Log.i(TAG, msg1 + "--->" + msg2);
		}
	}
	
	public static void d(String msg){
		if(isDebug){
			Log.d(TAG, msg);
		}
	}
	
	public static void e(String msg){
		if(isDebug){
			Log.e(TAG, msg);
		}
	}
}
