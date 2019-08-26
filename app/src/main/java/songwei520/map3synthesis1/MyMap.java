package songwei520.map3synthesis1;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.TileOverlay;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import songwei520.map3synthesis1.bean.MyLatLng;
import songwei520.map3synthesis1.mapUtils.LocalTileTileProvider;
import songwei520.map3synthesis1.utils.SharedPreferencesHelper;
import songwei520.map3synthesis1.utils.SharedPreferencesNameFile;


public class MyMap extends Fragment implements LocationSource,
        AMapLocationListener, OnMyLocationChangeListener {

    /*高德地图*/
    private MapView aMapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private Marker phoneAMarker;
    private Map<Integer, Marker> AMarkerMap;
    private Map<Integer, Marker> APopMarkerMap;
    List<Polyline> APolylines;
    List<Marker> AMarkers;

    /*百度地图*/
    private com.baidu.mapapi.map.MapView baiduMapView;
    private BaiduMap baiduMap;
    private LocationClient mLocClient;
    private com.baidu.mapapi.map.Marker phoneBaiduMarker;
    private Map<Integer, com.baidu.mapapi.map.Marker> BaiduMarkerMap;
    private Map<Integer, com.baidu.mapapi.map.Marker> BaiduPopMarkerMap;
    private InfoWindow mInfoWindow;
    List<com.baidu.mapapi.map.Polyline> BPolylines;

    /*谷歌地图*/
    private GoogleMap googleMap;
    private com.google.android.gms.maps.model.Marker phoneGoogleMarker;
    private Map<Integer, com.google.android.gms.maps.model.Marker> GoogleMarkerMap;
    private Map<Integer, com.google.android.gms.maps.model.Marker> GooglePopMarkerMap;
    List<com.google.android.gms.maps.model.Polyline> GPolylines;

    public FragmentActivity mContext;
    private boolean mapTypeEnable = true, CameraListenerEnable = false;
    private YWLocationListener mYWLocationListener;
    private YWCameraListener mYWCameraListener;
    private YWZoomListener mYWZoomListener;
    private YWMarkerClickListener mYWMarkerClickListener;
    private YWMapOnClickListener mYWMapOnClickListener;
    private YWInfoWindowAdapter mYWInfoWindowAdapter;
    private YWonCreate mYWonCreate;
    private YWInfoWindowClick mYWInfoWindowClick;
    public double centerLat, centerLng;


    private YWMapLongClickListener mYWMapLongClickListener;   //长按监听
    public boolean isCompass = true;       //是否开启地图指南针
    public boolean direction;       //是否开启方向传感器
    /*** 方向传感器的监听器*/
    private MyOrientationListener myOrientationListener;
    /*** 方向传感器X方向的值*/
    private int mXDirection;
    private SharedPreferencesHelper sp;

    public MyMap(Context context){
        sp = new SharedPreferencesHelper(context);
    }

    /*
    高德：1
    百度：2
    谷歌：3
    高德 + 谷歌：4
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        sp = new SharedPreferencesHelper(getActivity());
        if (sp.getSharedPreference("MapType","").equals("Google")) {
            if (!isGoogleMapsInstalled() || Locale.getDefault().toString().contains("zh")) {
                sp.put(SharedPreferencesNameFile.MapTypeInt,4);
            } else {
                sp.put(SharedPreferencesNameFile.MapTypeInt,3);
            }
        }
        View view = null;
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                view = inflater.inflate(R.layout.amap, container, false);
                break;
            case 2:
                view = inflater.inflate(R.layout.baidumap, container, false);
                break;
            case 3:
                view = inflater.inflate(R.layout.googlemap, container, false);
                break;
            case 4:
                view = inflater.inflate(R.layout.amap, container, false);
                break;
        }

        return view;
    }

    private boolean isGoogleMapsInstalled() {
        try {
            ApplicationInfo info = getActivity().getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        if (direction) {
            initOritationListener();  //感应监听
        }
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                aMapView = (MapView) mContext.findViewById(R.id.map);
                aMapView.onCreate(savedInstanceState);// 此方法必须重写
                aMapInit();
                if (mYWonCreate != null)
                    mYWonCreate.onCreate();
                initFunctiom();
                break;
            case 2:
                baiduMapInit();
                if (mYWonCreate != null)
                    mYWonCreate.onCreate();
                initFunctiom();
                break;
            case 3:
                setUpMapIfNeeded();
                break;
            case 4:
                aMapView = (MapView) mContext.findViewById(R.id.map);
                aMapView.onCreate(savedInstanceState);// 此方法必须重写
                agMapInit();
                if (mYWonCreate != null)
                    mYWonCreate.onCreate();
                initFunctiom();
                break;
        }

        setLanguage();

    }

    private void initFunctiom() {
        //获取地图中心点功能
        if (CameraListenerEnable)
            CameraChange();
    }


    /*   关于地图         */
    /*   高德 + 谷歌       */
    // 谷歌地图平面图
    String Tile_Google_Standard = "http://mt{$s}.google.cn/vt/lyrs=m&hl=" + Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry().toUpperCase() + "&gl=cn&scale=2&x=%d&y=%d&z=%d";
    // String Tile_Google_Standard2 =
    // "http://mt0.google.cn/vt/lyrs=m&hl=zh-CN&gl=cn&z=%d&x=%d&y=%d";
    // 谷歌地图卫星图
    String Tile_Google_Satellite = "http://mt{$s}.google.cn/vt/lyrs=s,m&hl=" + Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry().toUpperCase() + "&gl=cn&scale=2&x=%d&y=%d&z=%d";
    // String Tile_Google_Satellite2 =
    // "http://mt0.google.cn/vt/lyrs=s,m&hl=zh-CN&gl=cn&z=%d&x=%d&y=%d";
    // 谷歌地图地形图
    String Tile_Google_Terrain = "http://mt0.google.cn/vt/lyrs=p&hl=zh-CN&gl=cn&scale=2&x={x}&y={y}&z={z}";
    // Openstreetmap
    String Tile_OpenCycleMap = "https://b.tile.thunderforest.com/landscape/{z}/{x}/{y}.png";
    // OpenstreetmapLandScape
    String Tile_OpenStreetMap = "http://tile.openstreetmap.org/{z}/{x}/{y}.png";
    // 高德地图平面图
    String Tile_Gaodd_Standard = "http://wprd02.is.autonavi.com/appmaptile?lang=zh_cn&size=1&style=7&x={x}&y={y}&z={z}";
    // 高德地图卫星图(需要两层，第一层做底部，第二层添加标记)
    String Tile_Gaodd_Satellite_Base = "http://wprd02.is.autonavi.com/appmaptile?lang=zh-CN&size=1&scl=1&style=6&x={x}&y={y}&z={z}";
    String Tile_Gaodd_Satellite_Mark = "http://wprd02.is.autonavi.com/appmaptile?lang=zh-CN&size=1&scl=1&style=8&x={x}&y={y}&z={z}";

    private Polygon polygonMapLoad;
    private TileOverlay tileOverlay_Google_Standard;
    private TileOverlay tileOverlay_Google_Satellite;

    /**
     * 初始化AMap对象
     */
    private void agMapInit() {
        AMarkerMap = new HashMap<Integer, Marker>();
        APopMarkerMap = new HashMap<Integer, Marker>();
        APolylines = new ArrayList<Polyline>();
        AMarkers = new ArrayList<Marker>();
        if (aMap == null) {
            aMap = aMapView.getMap();
            setUpAMap();
        }

        if (polygonMapLoad == null) {
            PolygonOptions localPolygonOptions = new PolygonOptions().visible(false).fillColor(-263949).strokeWidth(0.0F).add(new LatLng(-90.0D, -180.0D, false))
                    .add(new LatLng(-90.0D, 180.0D, false)).add(new LatLng(90.0D, 180.0D, false)).add(new LatLng(90.0D, -180.0D, false));
            polygonMapLoad = aMap.addPolygon(localPolygonOptions);
        }
        polygonMapLoad.setVisible(true);

        aMap.getUiSettings().setLogoBottomMargin(-50);
        aMap.setTrafficEnabled(false);
        aMap.showBuildings(false);
        aMap.showIndoorMap(false);
        aMap.showMapText(false);
        aMap.setMapType(AMap.MAP_TYPE_NAVI);

        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
        tileOverlayOptions.zIndex(50);
        tileOverlayOptions.tileProvider(new LocalTileTileProvider(mContext, "googlestandard", Tile_Google_Standard, 4));
        tileOverlay_Google_Standard = aMap.addTileOverlay(tileOverlayOptions);

    }

    /*   高德地图         */

    /**
     * 初始化AMap对象
     */
    private void aMapInit() {
        AMarkerMap = new HashMap<Integer, Marker>();
        APopMarkerMap = new HashMap<Integer, Marker>();
        APolylines = new ArrayList<Polyline>();
        AMarkers = new ArrayList<Marker>();

        if (aMap == null) {
            aMap = aMapView.getMap();
            setUpAMap();
        }

    }

    /**
     * 设置一些amap的属性
     */
    private void setUpAMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle().equals(getResources().getString(R.string.my_location))) {
                    return true;
                }
                if (mYWMarkerClickListener.change(marker.getTitle(), marker.isInfoWindowShown())) {
                    marker.showInfoWindow();
                } else {
                    marker.hideInfoWindow();
                }
                return true;
            }
        });

        aMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng mLatLng) {
                if (mYWMapOnClickListener != null)
                    mYWMapOnClickListener.change(mLatLng.latitude, mLatLng.longitude);
            }
        });
        aMap.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker mMarker) {
                View v = mYWInfoWindowAdapter.change(mMarker.getTitle(), mMarker.getPosition().latitude, mMarker.getPosition().longitude);
                return v;
            }

            @Override
            public View getInfoContents(Marker mMarker) {
                TextView tv = new TextView(mContext);
                tv.setText(mMarker.getTitle());
                return tv;
            }
        });
        // aMap.setMyLocationType()
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                //mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                sp.put(SharedPreferencesNameFile.City,amapLocation.getCity());
                sp.put(SharedPreferencesNameFile.Latitude,amapLocation.getLatitude());
                sp.put(SharedPreferencesNameFile.Longitude,amapLocation.getLongitude());
                mYWLocationListener.change(amapLocation.getLatitude(), amapLocation.getLongitude());
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(mContext);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /*   百度地图         */
    private void baiduMapInit() {
        BaiduMarkerMap = new HashMap<Integer, com.baidu.mapapi.map.Marker>();
        BaiduPopMarkerMap = new HashMap<Integer, com.baidu.mapapi.map.Marker>();
        BPolylines = new ArrayList<com.baidu.mapapi.map.Polyline>();
        if (baiduMap == null) {
            baiduMapView = (com.baidu.mapapi.map.MapView) mContext.findViewById(R.id.map);
            baiduMap = baiduMapView.getMap();
            setUpBaiduMap();
        }
    }

    private Marker markerBaiDu;  //百度maker
    private boolean bIsShowInfoWindow = false;

    boolean isFirLoc = true;

    private void setUpBaiduMap() {
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(mContext);
        //是否关不关闭指南针
        baiduMap.getUiSettings().setCompassEnabled(isCompass);
        //设置百度地图的最大和最小缩放
        baiduMap.setMaxAndMinZoomLevel(21, 6);
        //添加感应监听
        if (direction) {
            mMyLocationListener = new MyLocationListener();
            mLocClient.registerLocationListener(mMyLocationListener);
        } else {
            mLocClient.registerLocationListener(new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation mBDLocation) {
                    sp.put(SharedPreferencesNameFile.City,mBDLocation.getCity());
                    sp.put(SharedPreferencesNameFile.Latitude,mBDLocation.getLatitude());
                    sp.put(SharedPreferencesNameFile.Longitude,mBDLocation.getLongitude());
                    if (mYWLocationListener != null) {
                        mYWLocationListener.change(mBDLocation.getLatitude(), mBDLocation.getLongitude());
                    }
                }
            });
        }
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);  // 打开gps
        option.setCoorType("bd09ll");  // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();

        MyLocationConfiguration.LocationMode locationMode = MyLocationConfiguration.LocationMode.NORMAL;     //普通
        //        MyLocationConfiguration.LocationMode locationMode = MyLocationConfiguration.LocationMode.FOLLOWING;     //跟随
        //        MyLocationConfiguration.LocationMode locationMode = MyLocationConfiguration.LocationMode.COMPASS;     //罗盘
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));

        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final com.baidu.mapapi.map.Marker marker) {
                if (marker.getTitle() == null || marker.getTitle().equals(getResources().getString(R.string.my_location))) {
                    return true;
                }
                if (mYWMarkerClickListener.change(marker.getTitle(), bIsShowInfoWindow)) {
                    View v = mYWInfoWindowAdapter.change(marker.getTitle(), marker.getPosition().latitude, marker.getPosition().longitude);
					/*OnInfoWindowClickListener l = new OnInfoWindowClickListener(){

						@Override
						public void onInfoWindowClick() {
							// TODO Auto-generated method stub
							if(mYWInfoWindowClick != null && Integer.valueOf(marker.getTitle()) != null){
								mYWInfoWindowClick.click(Integer.valueOf(marker.getTitle()));
							}
						}

					};
					mInfoWindow = new InfoWindow(com.baidu.mapapi.map.BitmapDescriptorFactory.fromView(v), marker.getPosition(), -47, l);*/
                    if (v != null) {
                        mInfoWindow = new InfoWindow(v, marker.getPosition(), -47);
                        baiduMap.showInfoWindow(mInfoWindow);
                        bIsShowInfoWindow = true;
                    }
                } else {
                    baiduMap.hideInfoWindow();
                    bIsShowInfoWindow = false;
                }
                return true;
            }
        });
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }

            @Override
            public void onMapClick(com.baidu.mapapi.model.LatLng mLatLng) {
                // TODO Auto-generated method stub
                if (mYWMapOnClickListener != null)
                    mYWMapOnClickListener.change(mLatLng.latitude, mLatLng.longitude);
            }
        });
        //长按
        baiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(com.baidu.mapapi.model.LatLng latLng) {
                try {
                    mYWMapLongClickListener.change(latLng.latitude, latLng.longitude);
                } catch (Exception e) {
                }
            }
        });
    }

    /*   谷歌地图         */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        GoogleMarkerMap = new HashMap<Integer, com.google.android.gms.maps.model.Marker>();
        GooglePopMarkerMap = new HashMap<Integer, com.google.android.gms.maps.model.Marker>();
        GPolylines = new ArrayList<>();
        if (googleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
			/*SupportMapFragment mapFragment = (SupportMapFragment) mContext.getSupportFragmentManager()
					.findFragmentById(R.id.map);*/
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap gMap) {
                    googleMap = gMap;
                    setUpGoogleMap();
                    if (mYWonCreate != null) {
                        mYWonCreate.onCreate();
                    }
                    initFunctiom();
                }
            });
			/*googleMap = ((MapFragment) mContext.getFragmentManager().findFragmentById(
					R.id.map)).getMap();//((SupportMapFragment) mContext.getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			if (googleMap != null) {
				setUpGoogleMap();
			}*/
        }
    }

    private void setUpGoogleMap() {
        //是否关闭指南针(默认为打开)
        googleMap.getUiSettings().setCompassEnabled(isCompass);
        //        googleMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions().position(new com.google.android.gms.maps.model.LatLng(0, 0)).title("Marker"));
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);   //类型
        //如果取得了权限,显示地图定位层
        if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 开启定位图层
                googleMap.setMyLocationEnabled(true);       //右上角是否显示点击定位自己的button
        }
        googleMap.setOnMyLocationChangeListener(this);

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(com.google.android.gms.maps.model.Marker mMarker) {
                if (mYWInfoWindowClick != null && Integer.valueOf(mMarker.getTitle()) != null) {
                    mYWInfoWindowClick.click(Integer.valueOf(mMarker.getTitle()));
                }
            }
        });
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(com.google.android.gms.maps.model.Marker mMarker) {
                View v = mYWInfoWindowAdapter.change(mMarker.getTitle(), mMarker.getPosition().latitude, mMarker.getPosition().longitude);
                return v;
            }

            @Override
            public View getInfoContents(com.google.android.gms.maps.model.Marker mMarker) {
                return null;
            }
        });
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker mMarker) {
                if (mMarker.getTitle() == null || mMarker.getTitle().equals(getResources().getString(R.string.my_location))) {
                    return true;
                }
                if (mYWMarkerClickListener.change(mMarker.getTitle(), mMarker.isInfoWindowShown())) {
                    mMarker.showInfoWindow();
                } else {
                    mMarker.hideInfoWindow();
                }
                return true;
            }
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(com.google.android.gms.maps.model.LatLng mLatLng) {
                if (mYWMapOnClickListener != null)
                    mYWMapOnClickListener.change(mLatLng.latitude, mLatLng.longitude);
            }
        });
        //长按事件
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(com.google.android.gms.maps.model.LatLng latLng) {
                mYWMapLongClickListener.change(latLng.latitude, latLng.longitude);
            }
        });
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (location != null) {
            sp.put(SharedPreferencesNameFile.City,location.getProvider());
            sp.put(SharedPreferencesNameFile.Latitude,location.getLatitude());
            sp.put(SharedPreferencesNameFile.Longitude,location.getLongitude());

            if (mYWLocationListener != null) {
                if (direction) {
//                    this.addPhoneMarker(location.getLatitude(), location.getLongitude(), R.drawable.m_static_n, getResources().getString(R.string.my_location), false);
//                    phoneGoogleMarker.setRotation(mXDirection);
//                    if (GoogleMarkerMap != null && mXDirection != 0.0f) {
//                        GoogleMarkerMap.get(0).setRotation(mXDirection);
//                    }
                }
                mYWLocationListener.change(location.getLatitude(), location.getLongitude());
            }
        }
    }

    /**
     * 添加mMarker
     */
    public void addmMarker(double lat, double lng, int MarkerIndex, String title, boolean isJump) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions amapMarkerOption = new MarkerOptions();
                //markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
                amapMarkerOption.draggable(true);
				/*	markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
					.decodeResource(getResources(),
							MarkerIndex)));*/
                amapMarkerOption.anchor(0.5f, 0.5f);
                amapMarkerOption.icon(BitmapDescriptorFactory.fromResource(MarkerIndex));
                Marker mMarker = aMap.addMarker(amapMarkerOption);
                mMarker.setPosition(latLng);
                if (!TextUtils.isEmpty(title)) {
                    mMarker.setTitle(title);
                    AMarkerMap.put(Integer.valueOf(title), mMarker);
                }
                if (isJump)
                    jumpPoint(mMarker, latLng);
                AMarkers.add(mMarker);
                break;
            case 2:
                com.baidu.mapapi.map.MarkerOptions baiduMarkerOptions = new com.baidu.mapapi.map.MarkerOptions()
                        .position(new com.baidu.mapapi.model.LatLng(lat, lng))
                        .flat(true)
                        .perspective(true)
                        .icon(com.baidu.mapapi.map.BitmapDescriptorFactory.fromResource(MarkerIndex))
                        .anchor(0.5f, 0.5f);
                com.baidu.mapapi.map.Marker mBaiduMarker = (com.baidu.mapapi.map.Marker) (baiduMap.addOverlay(baiduMarkerOptions));
                if (!TextUtils.isEmpty(title)) {
                    mBaiduMarker.setTitle(title);
                    BaiduMarkerMap.put(Integer.valueOf(title), mBaiduMarker);
                }
                break;
            case 3:
                com.google.android.gms.maps.model.MarkerOptions googleMarkerOptions = new com.google.android.gms.maps.model.MarkerOptions();
                googleMarkerOptions.position(new com.google.android.gms.maps.model.LatLng(lat, lng));
                googleMarkerOptions.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(MarkerIndex));
                googleMarkerOptions.anchor(0.5f, 0.5f);
                googleMarkerOptions.flat(true);
                com.google.android.gms.maps.model.Marker mGoogleMarker = googleMap.addMarker(googleMarkerOptions);
                if (!TextUtils.isEmpty(title)) {
                    mGoogleMarker.setTitle(title);
                    GoogleMarkerMap.put(Integer.valueOf(title), mGoogleMarker);
                }
                break;
            case 4:
                LatLng AGLatLng = new LatLng(lat, lng);
                MarkerOptions AGMarkerOption = new MarkerOptions();
                //markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
                AGMarkerOption.draggable(true);
				/*	markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
					.decodeResource(getResources(),
							MarkerIndex)));*/
                AGMarkerOption.anchor(0.5f, 0.5f);
                AGMarkerOption.icon(BitmapDescriptorFactory.fromResource(MarkerIndex));
                Marker mAGMarker = aMap.addMarker(AGMarkerOption);
                mAGMarker.setPosition(AGLatLng);
                if (!TextUtils.isEmpty(title)) {
                    mAGMarker.setTitle(title);
                    AMarkerMap.put(Integer.valueOf(title), mAGMarker);
                }
                if (isJump)
                    jumpPoint(mAGMarker, AGLatLng);
                AMarkers.add(mAGMarker);
                break;
        }
    }

    /**
     * 添加PopMarker
     */
    public void addPopMarker(double lat, double lng, View MarkerView, String title, boolean isJump) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions amapMarkerOption = new MarkerOptions();
                //markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
                amapMarkerOption.draggable(true);
        	/*markerOption.icon(BitmapDescriptorFactory
        			.fromResource(MarkerIndex));*/
                amapMarkerOption.icon(BitmapDescriptorFactory.fromView(MarkerView));
                amapMarkerOption.anchor(0.1f, -0.3f);
                Marker newMarker = aMap.addMarker(amapMarkerOption);
                newMarker.setPosition(latLng);
        	/*if(!TextUtils.isEmpty(title))
        		newMarker.setTitle(title);*/
                if (isJump)
                    jumpPoint(newMarker, latLng);
                APopMarkerMap.put(Integer.valueOf(title), newMarker);
                AMarkers.add(newMarker);
                break;
            case 2:
                com.baidu.mapapi.map.MarkerOptions baiduMarkerOptions = new com.baidu.mapapi.map.MarkerOptions()
                        .position(new com.baidu.mapapi.model.LatLng(lat, lng))
                        .icon(com.baidu.mapapi.map.BitmapDescriptorFactory.fromView(MarkerView))
                        //.icon(com.baidu.mapapi.map.BitmapDescriptorFactory.fromResource(R.drawable.pop_offline))
                        .flat(true)
                        //.anchor(1f, 0f)
                        .anchor(0f, 0f);
                //.zIndex(6);
                com.baidu.mapapi.map.Marker mBaiduMarker = (com.baidu.mapapi.map.Marker) (baiduMap.addOverlay(baiduMarkerOptions));
        	/*if(!TextUtils.isEmpty(title)){
        		mBaiduMarker.setTitle(title);
        	}*/
                BaiduPopMarkerMap.put(Integer.valueOf(title), mBaiduMarker);
                break;
            case 3:
                com.google.android.gms.maps.model.MarkerOptions googleMarkerOptions = new com.google.android.gms.maps.model.MarkerOptions();
                googleMarkerOptions.position(new com.google.android.gms.maps.model.LatLng(lat, lng));
                googleMarkerOptions.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(MarkerView)));
                //googleMarkerOptions.anchor(1.1f, -0.4f);
                googleMarkerOptions.anchor(0.1f, -0.3f);
                googleMarkerOptions.flat(true);
                com.google.android.gms.maps.model.Marker mGoogleMarker = googleMap.addMarker(googleMarkerOptions);
    		/*if(!TextUtils.isEmpty(title)){
    			mGoogleMarker.setTitle(title);
        	}*/
                GooglePopMarkerMap.put(Integer.valueOf(title), mGoogleMarker);
                break;
            case 4:
                LatLng AGLatLng = new LatLng(lat, lng);
                MarkerOptions AGMarkerOption = new MarkerOptions();
                //markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
                AGMarkerOption.draggable(true);
        	/*markerOption.icon(BitmapDescriptorFactory
        			.fromResource(MarkerIndex));*/
                AGMarkerOption.icon(BitmapDescriptorFactory.fromView(MarkerView));
                AGMarkerOption.anchor(0.1f, -0.3f);
                Marker AGMarker = aMap.addMarker(AGMarkerOption);
                AGMarker.setPosition(AGLatLng);
        	/*if(!TextUtils.isEmpty(title))
        		newMarker.setTitle(title);*/
                if (isJump)
                    jumpPoint(AGMarker, AGLatLng);
                APopMarkerMap.put(Integer.valueOf(title), AGMarker);
                AMarkers.add(AGMarker);
                break;
        }
    }

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }

    /**
     * 删除mMarker
     */
    public void removemMarker(int DeviceID) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                if (AMarkerMap.containsKey(DeviceID)) {
                    AMarkerMap.get(DeviceID).remove();
                    AMarkerMap.remove(DeviceID);
                }
                break;
            case 2:
                if (BaiduMarkerMap.containsKey(DeviceID)) {
                    BaiduMarkerMap.get(DeviceID).remove();
                    BaiduMarkerMap.remove(DeviceID);
                }
                break;
            case 3:
                if (GoogleMarkerMap.containsKey(DeviceID)) {
                    GoogleMarkerMap.get(DeviceID).remove();
                    GoogleMarkerMap.remove(DeviceID);
                }
                break;
            case 4:
                if (AMarkerMap.containsKey(DeviceID)) {
                    AMarkerMap.get(DeviceID).remove();
                    AMarkerMap.remove(DeviceID);
                }
                break;
        }
    }

    /**
     * 删除mMarker
     */
    public void removePopMarker(int DeviceID) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                APopMarkerMap.get(DeviceID).remove();
                APopMarkerMap.remove(DeviceID);
                break;
            case 2:
                BaiduPopMarkerMap.get(DeviceID).remove();
                BaiduPopMarkerMap.remove(DeviceID);
                break;
            case 3:
                GooglePopMarkerMap.get(DeviceID).remove();
                GooglePopMarkerMap.remove(DeviceID);
                break;
            case 4:
                APopMarkerMap.get(DeviceID).remove();
                APopMarkerMap.remove(DeviceID);
                break;
        }
    }

    /**
     * 添加mMarker
     */
    public void addPhoneMarker(double lat, double lng, int MarkerIndex, String title, boolean isJump) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                if (phoneAMarker != null)
                    phoneAMarker.remove();
                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions amapMarkerOption = new MarkerOptions();
                //markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
                amapMarkerOption.draggable(true);
                amapMarkerOption.icon(BitmapDescriptorFactory
                        .fromResource(MarkerIndex));
                amapMarkerOption.anchor(0.5f, 0.5f);
                phoneAMarker = aMap.addMarker(amapMarkerOption);
                phoneAMarker.setPosition(latLng);
                if (!TextUtils.isEmpty(title))
                    phoneAMarker.setTitle(title);
                if (isJump)
                    jumpPoint(phoneAMarker, latLng);
                AMarkers.add(phoneAMarker);
                break;
            case 2:
                if (phoneBaiduMarker != null)
                    phoneBaiduMarker.remove();
                com.baidu.mapapi.map.MarkerOptions baiduMarkerOptions = new com.baidu.mapapi.map.MarkerOptions()
                        .position(new com.baidu.mapapi.model.LatLng(lat, lng))
                        .icon(com.baidu.mapapi.map.BitmapDescriptorFactory.fromResource(MarkerIndex))
                        .flat(true)
                        .anchor(0.5f, 0.5f).zIndex(7);
                phoneBaiduMarker = (com.baidu.mapapi.map.Marker) (baiduMap.addOverlay(baiduMarkerOptions));
                if (!TextUtils.isEmpty(title)) {
                    phoneBaiduMarker.setTitle(title);
                }
                break;
            case 3:
                if (phoneGoogleMarker != null)
                    phoneGoogleMarker.remove();
                com.google.android.gms.maps.model.MarkerOptions googleMarkerOptions = new com.google.android.gms.maps.model.MarkerOptions();
                googleMarkerOptions.position(new com.google.android.gms.maps.model.LatLng(lat, lng));
                googleMarkerOptions.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(MarkerIndex));
                googleMarkerOptions.anchor(0.5f, 0.5f);
                googleMarkerOptions.flat(true);
                phoneGoogleMarker = googleMap.addMarker(googleMarkerOptions);
                if (!TextUtils.isEmpty(title)) {
                    phoneGoogleMarker.setTitle(title);
                }
                break;
            case 4:
                if (phoneAMarker != null)
                    phoneAMarker.remove();
                LatLng AGLatLng = new LatLng(lat, lng);
                MarkerOptions AGMarkerOption = new MarkerOptions();
                //markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
                AGMarkerOption.draggable(true);
                AGMarkerOption.icon(BitmapDescriptorFactory
                        .fromResource(MarkerIndex));
                AGMarkerOption.anchor(0.5f, 0.5f);
                phoneAMarker = aMap.addMarker(AGMarkerOption);
                phoneAMarker.setPosition(AGLatLng);
                if (!TextUtils.isEmpty(title))
                    phoneAMarker.setTitle(title);
                if (isJump)
                    jumpPoint(phoneAMarker, AGLatLng);
                AMarkers.add(phoneAMarker);
                break;
        }
    }

    /**
     * 删除手机mMarker
     */
    public void removePhoneMarker() {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                if (phoneAMarker != null)
                    phoneAMarker.remove();
                break;
            case 2:
                if (phoneBaiduMarker != null)
                    phoneBaiduMarker.remove();
                break;
            case 3:
                if (phoneGoogleMarker != null)
                    phoneGoogleMarker.remove();
                break;
            case 4:
                if (phoneAMarker != null)
                    phoneAMarker.remove();
                break;
        }
    }

    /**
     * 添加New Marker
     */
    public void addNewMarker(double lat, double lng, int MarkerIndex, String title, boolean isAnchor) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions amapMarkerOption = new MarkerOptions();
                //markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
                amapMarkerOption.draggable(true);
                amapMarkerOption.icon(BitmapDescriptorFactory
                        .fromResource(MarkerIndex));
                if (isAnchor)
                    amapMarkerOption.anchor(0.5f, 0.5f);
        	/*markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
					.decodeResource(getResources(),
							MarkerIndex)));*/

                if (!TextUtils.isEmpty(title))
                    amapMarkerOption.title(title);
                amapMarkerOption.position(latLng);
                Marker newMarker = aMap.addMarker(amapMarkerOption);
                AMarkers.add(newMarker);
                break;
            case 2:
                com.baidu.mapapi.map.MarkerOptions baiduMarkerOptions = new com.baidu.mapapi.map.MarkerOptions()
                        .position(new com.baidu.mapapi.model.LatLng(lat, lng))
                        .icon(com.baidu.mapapi.map.BitmapDescriptorFactory.fromResource(MarkerIndex))
                        .flat(true);

                if (isAnchor)
                    baiduMarkerOptions.anchor(0.5f, 0.5f);
                com.baidu.mapapi.map.Marker mBaiduMarker = (com.baidu.mapapi.map.Marker) (baiduMap.addOverlay(baiduMarkerOptions));
                if (!TextUtils.isEmpty(title)) {
                    mBaiduMarker.setTitle(title);
                }
                //设置地图新中心点
                //                baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new com.baidu.mapapi.model.LatLng(centerLng,centerLng)));
                break;
            case 3:
                com.google.android.gms.maps.model.MarkerOptions googleMarkerOptions = new com.google.android.gms.maps.model.MarkerOptions();
                googleMarkerOptions.position(new com.google.android.gms.maps.model.LatLng(lat, lng));
                googleMarkerOptions.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(MarkerIndex));
                if (isAnchor)
                    googleMarkerOptions.anchor(0.5f, 0.5f);
                googleMarkerOptions.flat(true);
                com.google.android.gms.maps.model.Marker mGoogleMarker = googleMap.addMarker(googleMarkerOptions);
                if (!TextUtils.isEmpty(title)) {
                    mGoogleMarker.setTitle(title);
                }
                break;
            case 4:
                LatLng AGLatLng = new LatLng(lat, lng);
                MarkerOptions AGMarkerOption = new MarkerOptions();
                //markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
                AGMarkerOption.draggable(true);
                AGMarkerOption.icon(BitmapDescriptorFactory
                        .fromResource(MarkerIndex));
                if (isAnchor)
                    AGMarkerOption.anchor(0.5f, 0.5f);
        	/*markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
					.decodeResource(getResources(),
							MarkerIndex)));*/

                if (!TextUtils.isEmpty(title))
                    AGMarkerOption.title(title);
                AGMarkerOption.position(AGLatLng);
                Marker newAGMarker = aMap.addMarker(AGMarkerOption);
                AMarkers.add(newAGMarker);
                break;
        }
    }

    /**
     * 移动到点
     */
    public void movePoint(double lat, double lng, boolean zoomIn) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                LatLng latLng = new LatLng(lat, lng);
                aMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(new CameraPosition(latLng, zoomIn ? 18 : aMap.getCameraPosition().zoom, 30, 0)), 1000,
                        null);
                break;
            case 2:
                baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(new com.baidu.mapapi.model.LatLng(lat, lng),
                        zoomIn ? 18 : baiduMap.getMapStatus().zoom));
        	/*baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(new com.baidu.mapapi.model.LatLng(lat, lng),
        			zoomIn?18:baiduMap.getMapStatus().zoom) , 1000);*/
                break;
            case 3:
                googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition(
                        new com.google.android.gms.maps.model.CameraPosition(
                                new com.google.android.gms.maps.model.LatLng(lat, lng), zoomIn ? 18 : googleMap.getCameraPosition().zoom, 0, 30)), 1000,
                        null);
                break;
            case 4:
                LatLng AGLatLng = new LatLng(lat, lng);
                aMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(new CameraPosition(AGLatLng, zoomIn ? 18 : aMap.getCameraPosition().zoom, 30, 0)), 1000,
                        null);
                break;
        }
    }

    /**
     * 设置地图语言
     */
    public void setLanguage() {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                aMap.setMapLanguage(getResources().getConfiguration().locale.getLanguage().equals("zh") ? AMap.CHINESE : AMap.ENGLISH);
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                aMap.setMapLanguage(getResources().getConfiguration().locale.getLanguage().equals("zh") ? AMap.CHINESE : AMap.ENGLISH);
                break;
        }
    }

    /**
     * 设置地图zoom
     */
    public void setZoom(float zoom) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
                break;
            case 2:
                baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(zoom));
                break;
            case 3:
                googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.zoomTo(zoom));
                break;
            case 4:
                aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
                break;
        }
    }

    /**
     * 设置地图zoom
     */
    public float getZoom() {
        float zoom = 0;
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                zoom = aMap.getCameraPosition().zoom;
                break;
            case 2:
                zoom = baiduMap.getMapStatus().zoom;
                break;
            case 3:
                zoom = googleMap.getCameraPosition().zoom;
                break;
            case 4:
                zoom = aMap.getCameraPosition().zoom;
                break;
        }
        return zoom;
    }

    /**
     * 通过比较判定zoom等级
     */
    public float getZoomOfRadius(int radius) {
        float zoom = 18f;
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                if (radius <= 5000 && radius > 4000) {
                    zoom = 12.67f;
                } else if (radius <= 4000 && radius > 3000) {
                    zoom = 13.07f;
                } else if (radius <= 3000 && radius > 2000) {
                    zoom = 13.64f;
                } else if (radius <= 2000 && radius > 1000) {
                    zoom = 14.31f;
                } else if (radius <= 1000 && radius > 750) {
                    zoom = 15.05f;
                } else if (radius <= 750 && radius > 500) {
                    zoom = 15.63f;
                } else if (radius <= 500 && radius > 350) {
                    zoom = 16.47f;
                } else if (radius <= 350 && radius > 250) {
                    zoom = 16.70f;
                } else if (radius <= 250 && radius > 150) {
                    zoom = 17.40f;
                } else if (radius <= 150) {
                    zoom = 17.77f;
                }
                break;
            case 2:
                if (radius <= 5000 && radius > 4000) {
                    zoom = 12.67f;
                } else if (radius <= 4000 && radius > 3000) {
                    zoom = 13.07f;
                } else if (radius <= 3000 && radius > 2000) {
                    zoom = 13.64f;
                } else if (radius <= 2000 && radius > 1000) {
                    zoom = 14.31f;
                } else if (radius <= 1000 && radius > 750) {
                    zoom = 15.05f;
                } else if (radius <= 750 && radius > 500) {
                    zoom = 15.63f;
                } else if (radius <= 500 && radius > 350) {
                    zoom = 16.47f;
                } else if (radius <= 350 && radius > 250) {
                    zoom = 16.70f;
                } else if (radius <= 250 && radius > 150) {
                    zoom = 17.40f;
                } else if (radius <= 150) {
                    zoom = 17.77f;
                }
                break;
            case 3:
                if (radius <= 5000 && radius > 4000) {
                    zoom = 12.16f;
                } else if (radius <= 4000 && radius > 3000) {
                    zoom = 12.31f;
                } else if (radius <= 3000 && radius > 2000) {
                    zoom = 12.78f;
                } else if (radius <= 2000 && radius > 1000) {
                    zoom = 13.31f;
                } else if (radius <= 1000 && radius > 750) {
                    zoom = 14.43f;
                } else if (radius <= 750 && radius > 500) {
                    zoom = 14.69f;
                } else if (radius <= 500 && radius > 350) {
                    zoom = 15.29f;
                } else if (radius <= 350 && radius > 250) {
                    zoom = 15.82f;
                } else if (radius <= 250 && radius > 150) {
                    zoom = 16.25f;
                } else if (radius <= 150) {
                    zoom = 16.93f;
                }
                break;
            case 4:
                if (radius <= 5000 && radius > 4000) {
                    zoom = 12.67f;
                } else if (radius <= 4000 && radius > 3000) {
                    zoom = 13.07f;
                } else if (radius <= 3000 && radius > 2000) {
                    zoom = 13.64f;
                } else if (radius <= 2000 && radius > 1000) {
                    zoom = 14.31f;
                } else if (radius <= 1000 && radius > 750) {
                    zoom = 15.05f;
                } else if (radius <= 750 && radius > 500) {
                    zoom = 15.63f;
                } else if (radius <= 500 && radius > 350) {
                    zoom = 16.47f;
                } else if (radius <= 350 && radius > 250) {
                    zoom = 16.70f;
                } else if (radius <= 250 && radius > 150) {
                    zoom = 17.40f;
                } else if (radius <= 150) {
                    zoom = 17.77f;
                }
                break;
        }
        return zoom;
    }

    /**
     * 地图界面显示所有Points
     */
    public void includePoints(List<MyLatLng> latLngList) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                LatLngBounds.Builder ab = new LatLngBounds.Builder();

                for (MyLatLng myLatLng : latLngList) {
                    ab.include(new LatLng(myLatLng.lat, myLatLng.lng));
                }
                LatLngBounds abounds = ab.build();
                aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(abounds, 10));
                break;
            case 2:
                com.baidu.mapapi.model.LatLngBounds.Builder bb = new com.baidu.mapapi.model.LatLngBounds.Builder();
                for (MyLatLng myLatLng : latLngList) {
                    bb.include(new com.baidu.mapapi.model.LatLng(myLatLng.lat, myLatLng.lng));
                }
                com.baidu.mapapi.model.LatLngBounds bbounds = bb.build();
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(bbounds), 1000);
                break;
            case 3:
                com.google.android.gms.maps.model.LatLngBounds.Builder gb = new com.google.android.gms.maps.model.LatLngBounds.Builder();
                for (MyLatLng myLatLng : latLngList) {
                    gb.include(new com.google.android.gms.maps.model.LatLng(myLatLng.lat, myLatLng.lng));
                }
                com.google.android.gms.maps.model.LatLngBounds gbounds = gb.build();
                googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds(gbounds, 10));
                break;
            case 4:
                LatLngBounds.Builder agb = new LatLngBounds.Builder();

                for (MyLatLng myLatLng : latLngList) {
                    agb.include(new LatLng(myLatLng.lat, myLatLng.lng));
                }
                LatLngBounds agbounds = agb.build();
                aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(agbounds, 10));
                break;
        }
    }

    /**
     * 清空地图
     */
    public void clearMap() {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                aMap.clear();
                AMarkers.clear();
                AMarkerMap.clear();
                APopMarkerMap.clear();
                APolylines.clear();
                mAPolyline = null;
                break;
            case 2:
                baiduMap.clear();
                if (BaiduMarkerMap != null)
                    BaiduMarkerMap.clear();
                if (BaiduPopMarkerMap != null)
                    BaiduPopMarkerMap.clear();
                if (BPolylines != null)
                    BPolylines.clear();
                mBPolyline = null;
                break;
            case 3:
                googleMap.clear();
                if (GoogleMarkerMap != null)
                    GoogleMarkerMap.clear();
                if (GooglePopMarkerMap != null)
                    GooglePopMarkerMap.clear();
                if (GPolylines != null)
                    GPolylines.clear();
                mGPolyline = null;
                break;
            case 4:
                for (Marker m : AMarkers) {
                    m.remove();
                }
                if (AMarkers != null)
                    AMarkers.clear();
                if (AMarkerMap != null)
                    AMarkerMap.clear();
                if (APopMarkerMap != null)
                    APopMarkerMap.clear();
                for (Polyline mpl : APolylines) {
                    mpl.remove();
                }
                if (APolylines != null)
                    APolylines.clear();
                mAPolyline = null;
                break;
        }
    }
    /**
     * 添加InfoWindow
     *//*
	public void addInfoWindow(final View v){
		switch(mapType){
        case 1:
        	aMap.setInfoWindowAdapter(new InfoWindowAdapter() {
    			@Override
    			public View getInfoWindow(Marker mMarker) {
    				// TODO Auto-generated method stub
    				return v;
    				if(mMarker.getTitle().equals("DeviceMarker")){
    				}else{
    					return null;
    				}
    			}

    			@Override
    			public View getInfoContents(Marker mMarker) {
    				// TODO Auto-generated method stub
    				return null;
    			}
    		});
        	break;
        case 2:
        	break;
        case 3:
        	break;
        }
	}*/

    /**
     * 显示InfoWindow
     */
    public void showInfoWindow(final int DeviceID) {
        try {
            switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
                case 1:
                    AMarkerMap.get(DeviceID).showInfoWindow();
                    break;
                case 2:
                    View v = mYWInfoWindowAdapter.change(String.valueOf(DeviceID), 0.0, 0.0);
	        	/*OnInfoWindowClickListener l = new OnInfoWindowClickListener(){

	        		@Override
	        		public void onInfoWindowClick() {
	        			// TODO Auto-generated method stub
	        			if(mYWInfoWindowClick != null && Integer.valueOf(BaiduMarkerMap.get(DeviceID).getTitle()) != null){

	        				mYWInfoWindowClick.click(Integer.valueOf(BaiduMarkerMap.get(DeviceID).getTitle()));
	        			}
	        		}

	        	};
	        	mInfoWindow = new InfoWindow(com.baidu.mapapi.map.BitmapDescriptorFactory.fromView(v), BaiduMarkerMap.get(DeviceID).getPosition(), -47, l);*/
                    mInfoWindow = new InfoWindow(v, BaiduMarkerMap.get(DeviceID).getPosition(), -47);
                    baiduMap.showInfoWindow(mInfoWindow);
                    bIsShowInfoWindow = true;
                    break;
                case 3:
                    GoogleMarkerMap.get(DeviceID).showInfoWindow();
                    break;
                case 4:
                    AMarkerMap.get(DeviceID).showInfoWindow();
                    break;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏InfoWindow
     */
    public void hideInfoWindow(int DeviceID) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                AMarkerMap.get(DeviceID).hideInfoWindow();
                break;
            case 2:
                baiduMap.hideInfoWindow();
                bIsShowInfoWindow = false;
                break;
            case 3:
                GoogleMarkerMap.get(DeviceID).hideInfoWindow();
                break;
            case 4:
                AMarkerMap.get(DeviceID).hideInfoWindow();
                break;
        }
    }

    /**
     * 画线
     */
    public void drawLine(double lat_a, double lng_a, double lat_b, double lng_b) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                LatLng latLng_a, latLng_b;
                latLng_a = new LatLng(lat_a, lng_a);
                latLng_b = new LatLng(lat_b, lng_b);
                Polyline mAPolyline;
                mAPolyline = aMap.addPolyline((new PolylineOptions()).width(10).add(latLng_a, latLng_b).color(
                        Color.GREEN).zIndex(51));
                mAPolyline.setGeodesic(true);
                APolylines.add(mAPolyline);
                break;
            case 2:
                com.baidu.mapapi.model.LatLng blatLng_a, blatLng_b;
                blatLng_a = new com.baidu.mapapi.model.LatLng(lat_a, lng_a);
                blatLng_b = new com.baidu.mapapi.model.LatLng(lat_b, lng_b);
                List<com.baidu.mapapi.model.LatLng> points = new ArrayList<com.baidu.mapapi.model.LatLng>();
                points.add(blatLng_a);
                points.add(blatLng_b);
                OverlayOptions ooPolyline = new com.baidu.mapapi.map.PolylineOptions().width(10)
                        .color(Color.GREEN).points(points);
                BPolylines.add((com.baidu.mapapi.map.Polyline) baiduMap.addOverlay(ooPolyline));
                break;
            case 3:
                com.google.android.gms.maps.model.LatLng glatLng_a, glatLng_b;
                glatLng_a = new com.google.android.gms.maps.model.LatLng(lat_a, lng_a);
                glatLng_b = new com.google.android.gms.maps.model.LatLng(lat_b, lng_b);
                com.google.android.gms.maps.model.Polyline mGPolyline;
                mGPolyline = googleMap.addPolyline((new com.google.android.gms.maps.model.PolylineOptions()).width(10).add(glatLng_a, glatLng_b).color(
                        Color.GREEN));
                mGPolyline.setGeodesic(true);
                GPolylines.add(mGPolyline);
                break;
            case 4:
                LatLng aglatLng_a, aglatLng_b;
                aglatLng_a = new LatLng(lat_a, lng_a);
                aglatLng_b = new LatLng(lat_b, lng_b);
                Polyline mAGPolyline;
                mAGPolyline = aMap.addPolyline((new PolylineOptions()).width(10).add(aglatLng_a, aglatLng_b).color(
                        Color.GREEN).zIndex(51));
                mAGPolyline.setGeodesic(true);
                APolylines.add(mAGPolyline);
                break;
        }
    }

    /**
     * 画线s
     */
    public void drawLines(List<MyLatLng> latLngList, List<Integer> colorList) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {    //颜色改成和单条颜色一致了
            case 1:
                PolylineOptions aOptions = new PolylineOptions();
                aOptions.width(15);//设置宽度
                aOptions.color(Color.GREEN);
                List<LatLng> al = new ArrayList<LatLng>();
                for (MyLatLng myLatLng : latLngList) {
                    LatLng latLng = new LatLng(myLatLng.lat, myLatLng.lng);
                    al.add(latLng);
                }
                aOptions.addAll(al);
                APolylines.add(aMap.addPolyline(aOptions));
                break;
            case 2:
                List<com.baidu.mapapi.model.LatLng> bl = new ArrayList<com.baidu.mapapi.model.LatLng>();
                for (MyLatLng myLatLng : latLngList) {
                    com.baidu.mapapi.model.LatLng latLng = new com.baidu.mapapi.model.LatLng(myLatLng.lat, myLatLng.lng);
                    bl.add(latLng);
                }
                //                OverlayOptions ooPolyline = new com.baidu.mapapi.map.PolylineOptions().width(15).points(bl).color(Color.BLUE);
                OverlayOptions ooPolyline = new com.baidu.mapapi.map.PolylineOptions().width(12).points(bl).color(Color.GREEN);

                BPolylines.add((com.baidu.mapapi.map.Polyline) baiduMap.addOverlay(ooPolyline));
                break;
            case 3:
                com.google.android.gms.maps.model.PolylineOptions gOptions = new com.google.android.gms.maps.model.PolylineOptions();
                gOptions.width(15);//设置宽度
                gOptions.color(Color.GREEN);
                List<com.google.android.gms.maps.model.LatLng> gl = new ArrayList<com.google.android.gms.maps.model.LatLng>();
                for (MyLatLng myLatLng : latLngList) {
                    com.google.android.gms.maps.model.LatLng latLng = new com.google.android.gms.maps.model.LatLng(myLatLng.lat, myLatLng.lng);
                    gl.add(latLng);
                }
                gOptions.addAll(gl);

                GPolylines.add((com.google.android.gms.maps.model.Polyline) googleMap.addPolyline(gOptions));
                break;
            case 4:
                PolylineOptions agOptions = new PolylineOptions();
                agOptions.width(15);//设置宽度
                agOptions.color(Color.BLUE).zIndex(51);
                List<LatLng> agl = new ArrayList<LatLng>();
                for (MyLatLng myLatLng : latLngList) {
                    LatLng latLng = new LatLng(myLatLng.lat, myLatLng.lng);
                    agl.add(latLng);
                }
                agOptions.addAll(agl);

                APolylines.add(aMap.addPolyline(agOptions));
                break;
        }
    }

    Polyline mAPolyline;
    com.google.android.gms.maps.model.Polyline mGPolyline;
    com.baidu.mapapi.map.Polyline mBPolyline;

    public void drawmLine(MyLatLng myLatLng_a, MyLatLng myLatLng_b) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                if (mAPolyline != null)
                    mAPolyline.remove();
                LatLng latLng_a, latLng_b;
                latLng_a = new LatLng(myLatLng_a.lat, myLatLng_a.lng);
                latLng_b = new LatLng(myLatLng_b.lat, myLatLng_b.lng);

                mAPolyline = aMap.addPolyline((new PolylineOptions()).add(latLng_a, latLng_b).width(12).color(
                        Color.GREEN));
                //mAPolyline.setGeodesic(true);
                APolylines.add(mAPolyline);
                break;
            case 2:
                if (mBPolyline != null)
                    mBPolyline.remove();
                com.baidu.mapapi.model.LatLng blatLng_a, blatLng_b;
                blatLng_a = new com.baidu.mapapi.model.LatLng(myLatLng_a.lat, myLatLng_a.lng);
                blatLng_b = new com.baidu.mapapi.model.LatLng(myLatLng_b.lat, myLatLng_b.lng);
                List<com.baidu.mapapi.model.LatLng> points = new ArrayList<com.baidu.mapapi.model.LatLng>();
                points.add(blatLng_a);
                points.add(blatLng_b);
                OverlayOptions ooPolyline = new com.baidu.mapapi.map.PolylineOptions().width(10)
                        .color(Color.GREEN).points(points);
                mBPolyline = (com.baidu.mapapi.map.Polyline) baiduMap.addOverlay(ooPolyline);
                BPolylines.add(mBPolyline);
                break;
            case 3:
                if (mGPolyline != null)
                    mGPolyline.remove();
                com.google.android.gms.maps.model.LatLng glatLng_a, glatLng_b;
                glatLng_a = new com.google.android.gms.maps.model.LatLng(myLatLng_a.lat, myLatLng_a.lng);
                glatLng_b = new com.google.android.gms.maps.model.LatLng(myLatLng_b.lat, myLatLng_b.lng);

                mGPolyline = googleMap.addPolyline((new com.google.android.gms.maps.model.PolylineOptions()).add(glatLng_a, glatLng_b).width(12).color(
                        Color.GREEN));
                GPolylines.add(mGPolyline);
                //mGPolyline.setGeodesic(true);
                break;
            case 4:
                if (mAPolyline != null)
                    mAPolyline.remove();
                LatLng aglatLng_a, aglatLng_b;
                aglatLng_a = new LatLng(myLatLng_a.lat, myLatLng_a.lng);
                aglatLng_b = new LatLng(myLatLng_b.lat, myLatLng_b.lng);

                mAPolyline = aMap.addPolyline((new PolylineOptions()).add(aglatLng_a, aglatLng_b).width(12).color(
                        Color.GREEN).zIndex(51));
                APolylines.add(mAPolyline);
                //mAPolyline.setGeodesic(true);
                break;
        }
    }

    /**
     * 画线
     */
    public void removeLine() {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                if (mAPolyline != null) {
                    mAPolyline.remove();
                }
                break;
            case 2:
                if (mBPolyline != null) {
                    mBPolyline.remove();
                }
                break;
            case 3:
                if (mGPolyline != null) {
                    mGPolyline.remove();
                }
                break;
            case 4:
                if (mAPolyline != null) {
                    mAPolyline.remove();
                }
                break;
        }
    }

    public double getDistance(MyLatLng myLatLng_a, MyLatLng myLatLng_b) {
        double distance = 0;
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                distance = AMapUtils.calculateLineDistance(new LatLng(myLatLng_a.lat, myLatLng_a.lng), new LatLng(myLatLng_b.lat, myLatLng_b.lng));
                break;
            case 2:
                distance = DistanceUtil.getDistance(new com.baidu.mapapi.model.LatLng(myLatLng_a.lat, myLatLng_a.lng),
                        new com.baidu.mapapi.model.LatLng(myLatLng_b.lat, myLatLng_b.lng));
                break;
            case 3:
                distance = AMapUtils.calculateLineDistance(new LatLng(myLatLng_a.lat, myLatLng_a.lng), new LatLng(myLatLng_b.lat, myLatLng_b.lng));
                break;
            case 4:
                distance = AMapUtils.calculateLineDistance(new LatLng(myLatLng_a.lat, myLatLng_a.lng), new LatLng(myLatLng_b.lat, myLatLng_b.lng));
                break;
        }
        return distance;
    }

    /**
     * 画圆
     */
    public void drawCircle(double lat, double lng, int radius) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                aMap.addCircle(new CircleOptions().center(new LatLng(lat, lng))
                        .radius(radius)
                        .strokeColor(mContext.getResources().getColor(R.color.blue_dark))
                        .fillColor(mContext.getResources().getColor(R.color.blue_dark_t))
                        .strokeWidth(10));
                break;
            case 2:
                OverlayOptions ooCircle = new com.baidu.mapapi.map.CircleOptions()
                        .center(new com.baidu.mapapi.model.LatLng(lat, lng))
                        .radius(radius)
                        .fillColor(mContext.getResources().getColor(R.color.blue_dark_t))
                        .stroke(new Stroke(10, mContext.getResources().getColor(R.color.blue_dark)));
                baiduMap.addOverlay(ooCircle);
                break;
            case 3:
                googleMap.addCircle(new com.google.android.gms.maps.model.CircleOptions().center(new com.google.android.gms.maps.model.LatLng(lat, lng))
                        .radius(radius)
                        .strokeColor(mContext.getResources().getColor(R.color.blue_dark))
                        .fillColor(mContext.getResources().getColor(R.color.blue_dark_t))
                        .strokeWidth(10));
                break;
            case 4:
                aMap.addCircle(new CircleOptions().center(new LatLng(lat, lng))
                        .radius(radius)
                        .strokeColor(mContext.getResources().getColor(R.color.blue_dark))
                        .fillColor(mContext.getResources().getColor(R.color.blue_dark_t))
                        .strokeWidth(10));
                break;
        }
    }

    /**
     * 计算电子围栏  圆直径
     */
    public double getFenceWidth(int radius, RelativeLayout rl) {
        double Width = 0, distance = 0;
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                if (aMap != null) {
                    LatLng alcLatLng = aMap.getProjection().fromScreenLocation(
                            new Point(0, 0));
                    LatLng arcLatLng = aMap.getProjection().fromScreenLocation(
                            new Point(rl.getWidth(), 0));
                    distance = AMapUtils.calculateLineDistance(alcLatLng, arcLatLng);
                }
                break;
            case 2:
                try {
                    if (baiduMap != null) {
                        com.baidu.mapapi.model.LatLng blcLatLng = baiduMap.
                                getProjection().
                                fromScreenLocation(
                                        new Point(0, 0));
                        com.baidu.mapapi.model.LatLng brcLatLng = baiduMap.getProjection().fromScreenLocation(
                                new Point(rl.getWidth(), 0));

                        distance = DistanceUtil.getDistance(blcLatLng, brcLatLng);
                    }
                } catch (NullPointerException e) {
                    distance = 0;
                }
                break;
            case 3:
                if (googleMap != null) {
                    com.google.android.gms.maps.model.LatLng glcLatLng = googleMap.getProjection().fromScreenLocation(
                            new Point(0, 0));
                    com.google.android.gms.maps.model.LatLng grcLatLng = googleMap.getProjection().fromScreenLocation(
                            new Point(rl.getWidth(), 0));
                    distance = AMapUtils.calculateLineDistance(
                            new LatLng(glcLatLng.latitude, glcLatLng.longitude),
                            new LatLng(grcLatLng.latitude, grcLatLng.longitude));
                }
                break;
            case 4:
                if (aMap != null) {
                    LatLng aglcLatLng = aMap.getProjection().fromScreenLocation(
                            new Point(0, 0));
                    LatLng agrcLatLng = aMap.getProjection().fromScreenLocation(
                            new Point(rl.getWidth(), 0));
                    distance = AMapUtils.calculateLineDistance(aglcLatLng, agrcLatLng);
                }
        }
        Width = (radius / distance) * rl.getWidth();
        return Width;
    }

    /**
     * 判断是否显示点
     */
    public boolean isPointInScreen(double lat, double lng, RelativeLayout rl) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                LatLng altLatLng = aMap.getProjection().fromScreenLocation(
                        new Point(0, 0));
                LatLng arbLatLng = aMap.getProjection().fromScreenLocation(
                        new Point(rl.getWidth(), rl.getHeight()));
                if ((lat > arbLatLng.latitude && lat < altLatLng.latitude)) {

                    if (altLatLng.longitude > arbLatLng.longitude) {
                        if ((lng < arbLatLng.longitude && lng > -180) && (lng < 180 && lng > altLatLng.longitude)) {
                            return true;
                        }
                    } else {
                        if (lng < arbLatLng.longitude && lng > altLatLng.longitude)
                            return true;
                    }
                }
                break;
            case 2:
                com.baidu.mapapi.model.LatLng bltLatLng = baiduMap.getProjection().fromScreenLocation(
                        new Point(0, 0));
                com.baidu.mapapi.model.LatLng brbLatLng = baiduMap.getProjection().fromScreenLocation(
                        new Point(rl.getWidth(), rl.getHeight()));
                if ((lat > brbLatLng.latitude && lat < bltLatLng.latitude)) {

                    if (bltLatLng.longitude > brbLatLng.longitude) {
                        if ((lng < brbLatLng.longitude && lng > -180) && (lng < 180 && lng > bltLatLng.longitude)) {
                            return true;
                        }
                    } else {
                        if (lng < brbLatLng.longitude && lng > bltLatLng.longitude)
                            return true;
                    }
                }
                break;
            case 3:
                com.google.android.gms.maps.model.LatLng gltLatLng = googleMap.getProjection().fromScreenLocation(
                        new Point(0, 0));
                com.google.android.gms.maps.model.LatLng grbLatLng = googleMap.getProjection().fromScreenLocation(
                        new Point(rl.getWidth(), rl.getHeight()));
                if ((lat > grbLatLng.latitude && lat < gltLatLng.latitude)) {

                    if (gltLatLng.longitude > grbLatLng.longitude) {
                        if ((lng < grbLatLng.longitude && lng > -180) && (lng < 180 && lng > gltLatLng.longitude)) {
                            return true;
                        }
                    } else {
                        if (lng < grbLatLng.longitude && lng > gltLatLng.longitude)
                            return true;
                    }
                }
                break;
            case 4:
                LatLng agltLatLng = aMap.getProjection().fromScreenLocation(
                        new Point(0, 0));
                LatLng agrbLatLng = aMap.getProjection().fromScreenLocation(
                        new Point(rl.getWidth(), rl.getHeight()));
                if ((lat > agrbLatLng.latitude && lat < agltLatLng.latitude)) {

                    if (agltLatLng.longitude > agrbLatLng.longitude) {
                        if ((lng < agrbLatLng.longitude && lng > -180) && (lng < 180 && lng > agltLatLng.longitude)) {
                            return true;
                        }
                    } else {
                        if (lng < agrbLatLng.longitude && lng > agltLatLng.longitude)
                            return true;
                    }
                }
                break;
        }
        return false;
    }

    /**
     * 开启地图移动监听
     */
    public void CameraChange() {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                aMap.setOnCameraChangeListener(new OnCameraChangeListener() {

                    @Override
                    public void onCameraChangeFinish(CameraPosition position) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onCameraChange(CameraPosition position) {
                        // TODO Auto-generated method stub

                        centerLat = position.target.latitude;
                        centerLng = position.target.longitude;
                        mYWCameraListener.change(position.target.latitude, position.target.longitude);

                    }
                });
                break;
            case 2:
                baiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

                    @Override
                    public void onMapStatusChangeStart(MapStatus mapStatus) {

                    }

                    @Override
                    public void onMapStatusChangeFinish(MapStatus mapStatus) {

                    }

                    @Override
                    public void onMapStatusChange(MapStatus mapStatus) {
                        centerLat = mapStatus.target.latitude;
                        centerLng = mapStatus.target.longitude;
                        mYWCameraListener.change(mapStatus.target.latitude, mapStatus.target.longitude);

                    }

                    @Override
                    public void onMapStatusChangeStart(MapStatus arg0, int arg1) {

                    }
                });
                break;
            case 3:
                if (googleMap != null)
                    googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                        @Override
                        public void onCameraChange(
                                com.google.android.gms.maps.model.CameraPosition position) {
                            // TODO Auto-generated method stub
                            centerLat = position.target.latitude;
                            centerLng = position.target.longitude;
                            mYWCameraListener.change(position.target.latitude, position.target.longitude);
                        }
                    });

                break;
            case 4:
                aMap.setOnCameraChangeListener(new OnCameraChangeListener() {

                    @Override
                    public void onCameraChangeFinish(CameraPosition position) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onCameraChange(CameraPosition position) {
                        // TODO Auto-generated method stub

                        centerLat = position.target.latitude;
                        centerLng = position.target.longitude;
                        mYWCameraListener.change(position.target.latitude, position.target.longitude);

                    }
                });
                break;
        }
    }

    /**
     * 获取中心点Lat
     */
    public double getCenterLat() {
        double lat = 0;
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                lat = aMap.getCameraPosition().target.latitude;
                break;
            case 2:
                lat = baiduMap.getMapStatus().target.latitude;
                break;
            case 3:
                lat = googleMap.getCameraPosition().target.latitude;
                break;
            case 4:
                lat = aMap.getCameraPosition().target.latitude;
                break;
        }
        return lat;
    }

    /**
     * 获取中心点Lng
     */
    public double getCenterLng() {
        double lng = 0;
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                lng = aMap.getCameraPosition().target.longitude;
                break;
            case 2:
                lng = baiduMap.getMapStatus().target.longitude;
                break;
            case 3:
                lng = googleMap.getCameraPosition().target.longitude;
                break;
            case 4:
                lng = aMap.getCameraPosition().target.longitude;
                break;
        }
        return lng;
    }

    /**
     * 获取地址
     */
    public void getAddress(final double lat, final double lng, final YWGetAddress mYWGetAddress) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                GeocodeSearch geocoderSearch = new GeocodeSearch(mContext);
                geocoderSearch.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {

                    @Override
                    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                        // TODO Auto-generated method stub
                        if (rCode == 1000) {
                            if (result != null && result.getRegeocodeAddress() != null
                                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                                if (!TextUtils.isEmpty(result.getRegeocodeAddress().getFormatAddress()))
                                    mYWGetAddress.onResult(result.getRegeocodeAddress().getFormatAddress());
                                else
                                    mYWGetAddress.onResult(getResources().getString(R.string.no_data));
                            } else {
                                mYWGetAddress.onResult(getResources().getString(R.string.no_data));
                            }
                        } else {
                            mYWGetAddress.onResult(getResources().getString(R.string.no_data));
                        }
                    }

                    @Override
                    public void onGeocodeSearched(GeocodeResult result, int rCode) {
                        // TODO Auto-generated method stub

                    }
                });
                RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(lat, lng), 200,
                        GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求

                break;
            case 2:
                // 初始化搜索模块，注册事件监听
                GeoCoder mSearch = GeoCoder.newInstance();
                mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                        // TODO Auto-generated method stub
                        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                            mYWGetAddress.onResult(getResources().getString(R.string.no_data));
                            return;
                        }
                        mYWGetAddress.onResult(result.getAddress());
                    }

                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult arg0) {
                        // TODO Auto-generated method stub

                    }
                });
                break;
            case 3:
                Thread getAddressThread = new Thread(new Runnable() {
                    public void run() {
                        Geocoder geocoder = new Geocoder(mContext, new Locale("en"));
                        try {
                            List<Address> address = geocoder.getFromLocation(lat, lng, 5);
                            if (address.size() > 0) {

                                String addre;
                                addre = address.get(0).getAddressLine(0);
                                if (address.get(0).getMaxAddressLineIndex() > 1)
                                    addre = addre + "," + address.get(0).getAddressLine(1);
                                mYWGetAddress.onResult(addre);
                            } else {
                                mYWGetAddress.onResult(getResources().getString(R.string.no_data));
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            mYWGetAddress.onResult(getResources().getString(R.string.no_data));
                            e.printStackTrace();
                        }
                    }
                });
                getAddressThread.start();
                break;
            case 4:
                GeocodeSearch aggeocoderSearch = new GeocodeSearch(mContext);
                aggeocoderSearch.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {

                    @Override
                    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                        if (rCode == 1000) {
                            if (result != null && result.getRegeocodeAddress() != null
                                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                                if (!TextUtils.isEmpty(result.getRegeocodeAddress().getFormatAddress()))
                                    mYWGetAddress.onResult(result.getRegeocodeAddress().getFormatAddress());
                                else
                                    mYWGetAddress.onResult(getResources().getString(R.string.no_data));
                            } else {
                                mYWGetAddress.onResult(getResources().getString(R.string.no_data));
                            }
                        } else {
                            mYWGetAddress.onResult(getResources().getString(R.string.no_data));
                        }
                    }

                    @Override
                    public void onGeocodeSearched(GeocodeResult result, int rCode) {
                        // TODO Auto-generated method stub

                    }
                });
                RegeocodeQuery agquery = new RegeocodeQuery(new LatLonPoint(lat, lng), 200,
                        GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                aggeocoderSearch.getFromLocationAsyn(agquery);// 设置同步逆地理编码请求

                break;
        }
    }

    /**
     * 获取地址
     */
    public void getAddress(final double lat, final double lng, final String language, final YWGetAddress mYWGetAddress) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
				/*try{
					StringBuilder url = new StringBuilder();
					url.append("http://maps.googleapis.com/maps/api/geocode/json?latlng=");
					url.append(lat).append(",");
					url.append(lng);
					url.append("&sensor=false");
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(url.toString());
					httpGet.addHeader("Accept-Language", language);
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if(httpResponse.getStatusLine().getStatusCode() == 200){
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity,"utf-8");
						JSONObject jsonObject = new JSONObject(response);
						JSONArray resultArray = jsonObject.getJSONArray("results");
						if(resultArray.length() > 0){
							JSONObject subObject = resultArray.getJSONObject(0);
							String address = subObject.getString("formatted_address");
							mYWGetAddress.onResult(address);
						}
					}
				}catch(Exception e){
					mYWGetAddress.onResult("");
					e.printStackTrace();
				}*/
                android.location.Geocoder geocoder = new android.location.Geocoder(mContext);
                try {
                    List<Address> address = geocoder.getFromLocation(lat, lng, 5);
                    if (address.size() > 0) {
                        mYWGetAddress.onResult(address.get(0).getAddressLine(0));
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 设置地图类型
     */
    public void setMapType(boolean isNormal) {
        if (isNormal) {
            switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
                case 1:
                    aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
                    break;
                case 2:
                    baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    break;
                case 3:
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    break;
                case 4:
                    if (tileOverlay_Google_Standard != null)
                        tileOverlay_Google_Standard.setVisible(false);
                    if (tileOverlay_Google_Satellite != null)
                        tileOverlay_Google_Satellite.setVisible(true);
                    else {
                        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
                        tileOverlayOptions.zIndex(50);
                        tileOverlayOptions.tileProvider(new LocalTileTileProvider(mContext, "googlesatelite", Tile_Google_Satellite, 4));
                        tileOverlay_Google_Satellite = aMap.addTileOverlay(tileOverlayOptions);
                    }
                    break;
            }
        } else {
            switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
                case 1:
                    aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
                    break;
                case 2:
                    baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    break;
                case 3:
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;
                case 4:
                    if (tileOverlay_Google_Satellite != null)
                        tileOverlay_Google_Satellite.setVisible(false);
                    if (tileOverlay_Google_Standard != null)
                        tileOverlay_Google_Standard.setVisible(true);
                    else {
                        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
                        tileOverlayOptions.zIndex(50);
                        tileOverlayOptions.tileProvider(new LocalTileTileProvider(mContext, "googlestandard", Tile_Google_Standard, 4));
                        tileOverlay_Google_Standard = aMap.addTileOverlay(tileOverlayOptions);
                    }
                    break;
            }
        }
    }

    /**
     * 是否显示地图缩放控件
     */
    public void setZoomControlsEnabled(boolean enable) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                aMap.getUiSettings().setZoomControlsEnabled(enable);
                break;
            case 2:
                baiduMapView.showZoomControls(false);
                break;
            case 3:
                googleMap.getUiSettings().setZoomControlsEnabled(enable);
                break;
            case 4:
                aMap.getUiSettings().setZoomControlsEnabled(enable);
                break;
        }
    }

    /**
     * ZoomIn
     */
    public void ZoomIn() {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                aMap.animateCamera(CameraUpdateFactory.zoomIn(), 1000, null);
                break;
            case 2:
                baiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
            case 3:
                googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.zoomIn(), 1000, null);
                break;
            case 4:
                aMap.animateCamera(CameraUpdateFactory.zoomIn(), 1000, null);
                break;
        }
    }

    /**
     * ZoomOut
     */
    public void ZoomOut() {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                aMap.animateCamera(CameraUpdateFactory.zoomOut(), 1000, null);
                break;
            case 2:
                baiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
            case 3:
                googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.zoomOut(), 1000, null);
                break;
            case 4:
                aMap.animateCamera(CameraUpdateFactory.zoomOut(), 1000, null);
                break;
        }
    }

    /**
     * 屏幕滚动功能关闭与打开
     */
    public void scrollGesturesEnabled(boolean enable) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                aMap.getUiSettings().setScrollGesturesEnabled(enable);
                break;
            case 2:
                baiduMap.getUiSettings().setScrollGesturesEnabled(enable);
                break;
            case 3:
                googleMap.getUiSettings().setScrollGesturesEnabled(enable);
                break;
            case 4:
                aMap.getUiSettings().setScrollGesturesEnabled(enable);
                break;
        }
    }

    /**
     * 地图移动监听功能
     */
    public void CameraListenerEnable(boolean bl) {
        CameraListenerEnable = bl;
    }

    /**
     * marker点击时跳动一下
     */
    public void jumpPoint(final Marker marker, final LatLng latLng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        Point startPoint = proj.toScreenLocation(latLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * latLng.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * latLng.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    public interface YWonCreate {
        void onCreate();
    }

    public void setYWonCreate(YWonCreate mYWonCreate) {
        this.mYWonCreate = mYWonCreate;
    }

    public interface YWLocationListener {
        void change(double lat, double lng);
    }

    public void setYWLocationListener(YWLocationListener mYWLocationListener) {
        this.mYWLocationListener = mYWLocationListener;
    }

    public interface YWCameraListener {
        void change(double lat, double lng);
    }

    public void setCameraListener(YWCameraListener mYWCameraListener) {
        this.mYWCameraListener = mYWCameraListener;
    }

    public interface YWZoomListener {
        void change();
    }

    public void setZoomListener(YWZoomListener mYWZoomListener) {
        this.mYWZoomListener = mYWZoomListener;
    }

    public interface YWMarkerClickListener {
        boolean change(String title, boolean isShowInfoWindow);
    }

    public void setYWMarkerClickListener(YWMarkerClickListener mYWMarkerClickListener) {
        this.mYWMarkerClickListener = mYWMarkerClickListener;
    }

    public interface YWMapOnClickListener {
        void change(double lat, double lng);
    }

    public void setYWMapOnClickListener(YWMapOnClickListener mYWMapOnClickListener) {
        this.mYWMapOnClickListener = mYWMapOnClickListener;
    }

    public interface YWInfoWindowAdapter {
        View change(String title, double lat, double lng);
    }

    public void setYWInfoWindowAdapter(YWInfoWindowAdapter mYWInfoWindowAdapter) {
        this.mYWInfoWindowAdapter = mYWInfoWindowAdapter;
    }

    public interface YWGetAddress {
        void onResult(String address);
    }

    public void setYWInfoWindowClick(YWInfoWindowClick mYWInfoWindowClick) {
        this.mYWInfoWindowClick = mYWInfoWindowClick;
    }

    public interface YWInfoWindowClick {
        void click(int DeviceID);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                aMapView.onResume();
                aMap.setMyLocationEnabled(true);
                break;
            case 2:
                baiduMapView.onResume();
                baiduMap.setMyLocationEnabled(true);
                break;
            case 3:
                if (googleMap != null) {
                    if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    }
                }
                break;
            case 4:
                aMapView.onResume();
                aMap.setMyLocationEnabled(true);
                break;
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                aMap.setMyLocationEnabled(false);
                aMapView.onPause();
                deactivate();
                break;
            case 2:
                baiduMap.setMyLocationEnabled(false);
                baiduMapView.onPause();
                break;
            case 3:
                if (googleMap != null)
                    if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(false);
                    }
                break;
            case 4:
                aMap.setMyLocationEnabled(false);
                aMapView.onPause();
                deactivate();
                break;
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                aMapView.onSaveInstanceState(outState);
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                aMapView.onSaveInstanceState(outState);
                break;
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                aMapView.onDestroy();
                break;
            case 2:
                // 退出时销毁定位
                mLocClient.stop();
                baiduMapView.onDestroy();
                baiduMapView = null;
                break;
            case 3:
                break;
            case 4:
                aMapView.onDestroy();
                break;
        }
    }


    //长按监听
    public interface YWMapLongClickListener {
        void change(double lat, double lng);
    }

    public void setYWMapLongClickListener(YWMapLongClickListener mYWMapLongClickListener) {
        this.mYWMapLongClickListener = mYWMapLongClickListener;
    }

    @Override
    public void onStart() {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                break;
            case 2:
                if (direction) {
                    // 开启方向传感器
                    if (myOrientationListener != null) {
                        myOrientationListener.start();
                    }
                }
                break;
            case 3:
                if (direction) {
                    // 开启方向传感器
                    if (myOrientationListener != null) {
                        myOrientationListener.start();
                    }
                }
                break;
            case 4:
                break;
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                break;
            case 2:
                if (direction) {
                    //关闭方向传感器
                    if (myOrientationListener != null) {
                        myOrientationListener.stop();
                    }
                }
                break;
            case 3:
                if (direction) {
                    //关闭方向传感器
                    if (myOrientationListener != null) {
                        myOrientationListener.stop();
                    }
                }
                break;
            case 4:
                break;
        }
        super.onStop();
    }

    /**
     * 删除折线,
     */
    public void deleteBrokenLines(int index) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                break;
            case 2:
                BPolylines.get(index).remove();
                break;
            case 3:
                // BPolylines.get(index).remove();
                break;
            case 4:
                break;
        }
    }

    /**
     * 开关指南针
     */
    public void switchingCompass(boolean compass) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                break;
            case 2:
                isCompass = compass;
                break;
            case 3:
                isCompass = compass;
                break;
            case 4:
                break;
        }
    }

    /**
     * 是否开启方向感应,传感器
     */
    public void sensorEventListener(boolean sensorEvent) {
        switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
            case 1:
                break;
            case 2:
                direction = sensorEvent;
                break;
            case 3:
                direction = sensorEvent;
                break;
            case 4:
                break;
        }
    }


    /*** 初始化方向传感器*/
    private void initOritationListener() {
        myOrientationListener = new MyOrientationListener(getContext().getApplicationContext());
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mXDirection = (int) x;
                //                Log.e("aaa", "传感器x方向的值:" + x);
                switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
                    case 1:
                        break;
                    case 2:
                        // 构造定位数据
                        MyLocationData locData = new MyLocationData.Builder()
                                .accuracy(mCurrentAccracy)
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                                .direction(mXDirection)
                                .latitude(mCurrentLantitude)
                                .longitude(mCurrentLongitude).build();
                        // 设置定位数据
                        baiduMap.setMyLocationData(locData);
                        // 设置自定义图标
                        //                com.baidu.mapapi.map.BitmapDescriptor mCurrentMarker = com.baidu.mapapi.map.BitmapDescriptorFactory.fromResource(R.drawable.m_static_n);
                        //                MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
                        //                baiduMap.setMyLocationConfigeration(config);
                        break;
                    case 3:

                        break;
                    case 4:
                        break;
                }

            }
        });
    }

    /**百度地图*/
    /*** 当前的精度*/
    private float mCurrentAccracy;
    /*** 最新一次的经纬度*/
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    /**** 是否是第一次定位*/
    private volatile boolean isFristLocation = true;
    /*** 当前定位的模式*/
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;  //普通模式: 更新定位数据时不对地图做任何操作
    //    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;   //罗盘模式: 显示定位方向圈，保持定位图标在地图中心
    //    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;   //跟随模式: 保持定位图标在地图中心
    /*** 定位的监听器*/
    public MyLocationListener mMyLocationListener;

    /*** 实现实位回调监听*/
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || baiduMapView == null) {
                return;
            }
            sp.put(SharedPreferencesNameFile.City,location.getCity());
            sp.put(SharedPreferencesNameFile.Latitude,location.getLatitude());
            sp.put(SharedPreferencesNameFile.Longitude,location.getLongitude());

            if (mYWLocationListener != null) {
                mYWLocationListener.change(location.getLatitude(), location.getLongitude());
            }
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，延X轴方向顺时针0-360
                    .direction(mXDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            mCurrentAccracy = location.getRadius();
            //            Log.e("aaa", "当前的精度值:" + mCurrentAccracy);
            // 设置定位数据
            baiduMap.setMyLocationData(locData);
            mCurrentLantitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();
            // 设置自定义图标
            //            com.baidu.mapapi.map.BitmapDescriptor mCurrentMarker = com.baidu.mapapi.map.BitmapDescriptorFactory.fromResource(R.drawable.m_static_n);
            //            MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
            //            baiduMap.setMyLocationConfigeration(config);
            // 第一次定位时，将地图位置移动到当前位置
            if (isFristLocation) {
                isFristLocation = false;
                com.baidu.mapapi.model.LatLng ll = new com.baidu.mapapi.model.LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(u);
            }
        }
    }
}
