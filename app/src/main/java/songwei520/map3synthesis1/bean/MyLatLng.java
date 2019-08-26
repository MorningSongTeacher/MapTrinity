package songwei520.map3synthesis1.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class MyLatLng implements Parcelable {
	public double lat;
	public double lng;
	public String content;
	public MyLatLng(double lat, double lng){
		this.lat = lat;
		this.lng = lng;
	}
	public MyLatLng(double lat, double lng, String content){
		this.lat = lat;
		this.lng = lng;
		this.content = content;
	}
	public MyLatLng(){
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeDoubleArray(new double[]{lat,lng});
		dest.writeString(content);
	}
	public static final Parcelable.Creator<MyLatLng> CREATOR = new Parcelable.Creator<MyLatLng>(){

		@Override
		public MyLatLng createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			MyLatLng mMyLatLng = new MyLatLng();
			double[] doubles = new double[2];
			source.readDoubleArray(doubles);
			mMyLatLng.lat = doubles[0];
			mMyLatLng.lng = doubles[1];
			mMyLatLng.content = source.readString();
			return mMyLatLng;
		}

		@Override
		public MyLatLng[] newArray(int size) {
			// TODO Auto-generated method stub
			return new MyLatLng[size];
		}
		
	};

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
}
