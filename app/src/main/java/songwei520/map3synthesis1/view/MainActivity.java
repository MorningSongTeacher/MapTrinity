package songwei520.map3synthesis1.view;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.sug.SuggestionResult;

import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import songwei520.map3synthesis1.MyMap;
import songwei520.map3synthesis1.MySearch;
import songwei520.map3synthesis1.R;
import songwei520.map3synthesis1.bean.MyLatLng;
import songwei520.map3synthesis1.utils.Gps;
import songwei520.map3synthesis1.utils.PositionUtil;
import songwei520.map3synthesis1.utils.SharedPreferencesHelper;
import songwei520.map3synthesis1.utils.SharedPreferencesNameFile;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CheckBox cb_map_type;
    private ImageButton ib_my_lacation, ib_request_location, ib_location_Route, ib_blt_name;
    private LinearLayout ll_poi;
    private ListView lv_poi;
    private EditText et_search;

    //private double mLat, mLng;  //我的位置
    private MySearch mYWSearch;
    private ArrayAdapter<String> keyWordAdapter;

    private boolean lockM;
    private boolean isRoute, isSearch;

    private View infoWindow;
    //上报设备的经纬度
    private double upLat, upLng;
    private int multipleNumber;     //路线
    private List<MyLatLng> latLngList;

    private MyMap myMap;
    private String position;
    private ArrayList<MyLatLng> searchPoints;   //搜索出来的兴趣点
    private int mapTypeInt;   //地图类型
    private ArrayList<MyLatLng> baiDuPoints;   //百度搜索经纬度
    
    //窗体
    private Button bt_report, bt_delete;
    private TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //高德地图是(1),中文用百度(2),英文用谷歌(3), 高德+谷歌地图(4)
        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        mapTypeInt = (int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2);
        //判断系统是中文还是英文,对应使用不同的地图
//        if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
//            sp.put(SharedPreferencesNameFile.MapTypeInt,2);
//            mapTypeInt = 2;
//        } else {
//            sp.put(SharedPreferencesNameFile.MapTypeInt,3);
//            mapTypeInt = 3;
//        }

        cb_map_type = findViewById(R.id.cb_map_type);
        cb_map_type.setOnClickListener(this);
        ib_my_lacation = findViewById(R.id.ib_my_lacation);
        ib_my_lacation.setOnClickListener(this);
        ib_request_location = findViewById(R.id.ib_request_location);
        ib_request_location.setOnClickListener(this);

        ib_location_Route = findViewById(R.id.ib_location_Route);
        ib_location_Route.setOnClickListener(this);

        ll_poi = findViewById(R.id.ll_poi);
        lv_poi = findViewById(R.id.lv_poi);
        et_search = findViewById(R.id.et_search);

        MainActivityPermissionsDispatcher.initMapWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void initMap() {
        searchPoints = new ArrayList<MyLatLng>();
        baiDuPoints = new ArrayList<MyLatLng>();
        //地图
        setFragment();
        //不显示指南针
        myMap.switchingCompass(false);
        //开启方向传感器
        myMap.sensorEventListener(true);
        mYWSearch = new MySearch(MainActivity.this);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(et_search.getText()) && et_search.getText().toString().trim().length() > 0) {
                    //搜索,只写了百度2和谷歌3的
                    if (mapTypeInt == 2) {
                        mYWSearch.searchKeyWord2(et_search.getText().toString().trim(), new MySearch.YWSearchKeyWordListion2() {
                            @Override
                            public void Result(Object res) {
                                if (res != null) {
                                    //经纬度
                                    if (baiDuPoints != null) {
                                        baiDuPoints.clear();
                                    }
                                    //名称列表
                                    List<String> arr = new ArrayList<String>();
                                    for (SuggestionResult.SuggestionInfo info : ((SuggestionResult) res).getAllSuggestions()) {
                                        if (info.pt == null || info.pt.longitude == 0.0 || info.pt.latitude == 0.0) {
                                            continue;
                                        }
                                        if (info.key != null) {
                                            arr.add(info.key);
                                            baiDuPoints.add(new MyLatLng(info.pt.latitude, info.pt.longitude));
                                        }
                                    }
                                    keyWordAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                            R.layout.search_address_item, R.id.tv, arr);
                                    lv_poi.setAdapter(keyWordAdapter);
                                    keyWordAdapter.notifyDataSetChanged();
                                    lv_poi.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } else if (mapTypeInt == 3) {
                        mYWSearch.searchKeyWord(et_search.getText().toString().trim(), new MySearch.YWSearchKeyWordListion() {
                            @Override
                            public void Result(List<String> list) {
                                if (list != null) {
                                    keyWordAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                            R.layout.search_address_item, R.id.tv, list);
                                    lv_poi.setAdapter(keyWordAdapter);
                                    keyWordAdapter.notifyDataSetChanged();
                                    lv_poi.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                } else {
                    lv_poi.setAdapter(null);
                    lv_poi.setVisibility(View.GONE);
                }
            }
        });
        lv_poi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Log.e("aaa", "联想的名称是:" + keyWordAdapter.getItem(position));
                if (mapTypeInt == 2) {
                    try {
                        if (latLngList.size() > 50) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_more_than_50_locating_points), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (mapTypeInt == 3) {
                            if (!lockM) {
                                return;
                            }
                        }
                        upLat = baiDuPoints.get(position).getLat();
                        upLng = baiDuPoints.get(position).getLng();
                        MyLatLng latLng = new MyLatLng();
                        latLng.lat = baiDuPoints.get(position).getLat();
                        latLng.lng = baiDuPoints.get(position).getLng();
                        latLngList.add(latLng);
                        multipleNumber++;
                        if (multipleNumber == 1) {
                            myMap.drawmLine(new MyLatLng(latLngList.get(0).getLat(), latLngList.get(0).getLng()), new MyLatLng(baiDuPoints.get(position).getLat(), baiDuPoints.get(position).getLng()));
                        } else {
                            //                    myMap.includePoints(latLngList);
                            myMap.drawLines(latLngList, null);
                        }
                        myMap.addmMarker(baiDuPoints.get(position).getLat(), baiDuPoints.get(position).getLng(), R.drawable.phone_point, "" + multipleNumber, false);
                        if (multipleNumber > 1) {
                            if (mapTypeInt == 2) {
                                myMap.hideInfoWindow(multipleNumber - 1);
                            }
                        }
                        myMap.showInfoWindow(multipleNumber);

                        ib_request_location.setImageResource(R.drawable.btn_location_search_normal);
                        //显示地图上所有的点
                        ArrayList<MyLatLng> list = new ArrayList<MyLatLng>();
                        list.addAll(latLngList);
                        list.addAll(searchPoints);
                        myMap.includePoints(list);

                        ll_poi.setVisibility(View.GONE);
                        lv_poi.setVisibility(View.GONE);
                        et_search.setText("");
                        isSearch = !isSearch;
                        //  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        et_search.clearFocus();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (mapTypeInt == 3) {
                    mYWSearch.searchAddress(keyWordAdapter.getItem(position), new MySearch.YWSearchAddressListion() {
                        @Override
                        public void Result(ArrayList<MyLatLng> ymList) {
                            try {
                                searchPoints.clear();
                                //搜索的点
                                searchPoints.addAll(ymList);
                                // 重新画点和线
                                drawDottedLines(true);

                                ib_request_location.setImageResource(R.drawable.btn_location_search_normal);
                                //显示地图上所有的点
                                ArrayList<MyLatLng> list = new ArrayList<MyLatLng>();
                                list.addAll(latLngList);
                                list.addAll(ymList);
                                myMap.includePoints(list);

                                ll_poi.setVisibility(View.GONE);
                                lv_poi.setVisibility(View.GONE);
                                et_search.setText("");
                                isSearch = !isSearch;
                                et_search.clearFocus();
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
        myMap.setYWLocationListener(new MyMap.YWLocationListener() {
            @Override
            public void change(double lat, double lng) {
                //                                Log.e("aaa", "我的位置:纬度" + positionTurn(lat, lng).getWgLat() + ",经度:" + positionTurn(lat, lng).getWgLon());
                if (!lockM) {
                    lockM = true;
                    myMap.setZoom(5f);
                    MyLatLng latLng = new MyLatLng();
                    latLng.lat = lat;
                    latLng.lng = lng;
                    latLngList.add(latLng);
                    //                    myMap.addPhoneMarker(lat, lng, R.drawable.m_static_n, getResources().getString(R.string.my_location), false);
                    myMap.movePoint(latLngList.get(0).getLat(), latLngList.get(0).getLng(), true);
                } else {
                    latLngList.get(0).setLat(lat);
                    latLngList.get(0).setLng(lng);
                    // 删除旧点,线,添加新点线
                    myMap.removePhoneMarker();
                    if (latLngList.size() > 1) {
                        drawDottedLines(false);
                    } else {
                        //                                                myMap.addPhoneMarker(latLngList.get(0).getLat(), latLngList.get(0).getLng(), R.drawable.m_static_n, getResources().getString(R.string.my_location), false);
                    }
                }
            }
        });

        myMap.setYWMarkerClickListener(new MyMap.YWMarkerClickListener() {
            @Override
            public boolean change(String title, boolean isShowInfoWindow) {
                return !isShowInfoWindow;
            }
        });

        myMap.setYWInfoWindowAdapter(new MyMap.YWInfoWindowAdapter() {
            @Override
            public View change(String title, double lat, double lng) {
                Log.e("aaa", "标题是:" + title + ",纬度:" + lat + ",经度:" + lng);
                if (TextUtils.isEmpty(title)) {
                    return null;
                }
                if (latLngList.size() > 50) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.no_more_than_50_locating_points), Toast.LENGTH_SHORT).show();
                    return null;
                }
                if (mapTypeInt == 2) {
                    if (lat != 0.0 || lng != 0.0) {
                        upLat = lat;
                        upLng = lng;
                        MyLatLng latLng = new MyLatLng();
                        latLng.lat = lat;
                        latLng.lng = lng;
                        latLngList.add(latLng);
                        multipleNumber++;
                        if (multipleNumber == 1) {
                            myMap.drawmLine(new MyLatLng(latLngList.get(0).getLat(), latLngList.get(0).getLng()), new MyLatLng(lat, lng));
                        } else {
                            myMap.includePoints(latLngList);     //地图界面显示范围
                            myMap.drawLines(latLngList, null);
                        }
                    }
                }
                initInfoWindow(title, lat, lng);
                return infoWindow;
            }
        });

        //获取地图定位点的经纬度
        latLngList = new ArrayList<MyLatLng>();

        myMap.setYWMapLongClickListener(new MyMap.YWMapLongClickListener() {
            @Override
            public void change(double lat, double lng) {
                Log.e("aaa", "进入长按,监听:lat:" + lat + ", lng:" + lng);
                if (latLngList.size() > 50) {       //限制地图最多50个点
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.no_more_than_50_locating_points), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mapTypeInt == 3) {
                    if (!lockM) {
                        return;
                    }
                }
                upLat = lat;
                upLng = lng;
                MyLatLng latLng = new MyLatLng();
                latLng.lat = lat;
                latLng.lng = lng;
                latLngList.add(latLng);
                multipleNumber++;
                if (multipleNumber == 1) {
                    myMap.drawmLine(new MyLatLng(latLngList.get(0).getLat(), latLngList.get(0).getLng()), new MyLatLng(lat, lng));
                } else {
                    //                    myMap.includePoints(latLngList);
                    myMap.drawLines(latLngList, null);
                }
                myMap.addmMarker(lat, lng, R.drawable.phone_point, "" + multipleNumber, false);
                if (multipleNumber > 1) {
                    if (mapTypeInt == 2) {
                        myMap.hideInfoWindow(multipleNumber - 1);
                    }
                }
                myMap.showInfoWindow(multipleNumber);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void setFragment() {
        myMap = new MyMap(this);
        //        Bundle args = new Bundle();
        //        args.putString("key", "");
        //        myMap.setArguments(args); // 可以携带数据传入地图fragment类中
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, myMap)
                .commit();
    }

    private void initInfoWindow(String tit, final double Lat, final double Lng) {
        infoWindow = MainActivity.this.getLayoutInflater().inflate(R.layout.item_bluetooth_control, null);
        tv_content = (TextView) infoWindow.findViewById(R.id.tv_content);
        View v_shu = (View) infoWindow.findViewById(R.id.v_heng);
        bt_report = (Button) infoWindow.findViewById(R.id.bt_report);
        bt_delete = (Button) infoWindow.findViewById(R.id.bt_delete);
        if (Lat == 0.0 || Lng == 0.0) {     //先判断是地图长按出来的弹窗,还是通过搜索出来的弹窗
            if (mapTypeInt == 2) {
                Gps gps84 = positionTurn(upLat, upLng);
                tv_content.setText(getResources().getString(R.string.destination) + ":" + "\n" + getResources().getString(R.string.latitude) + ":"
                        + gps84.getWgLat() + "\n" + getResources().getString(R.string.longitude) + ":" + gps84.getWgLon());
            } else {
                bt_report.setVisibility(View.GONE);
                bt_delete.setVisibility(View.GONE);
                v_shu.setVisibility(View.GONE);
                tv_content.setText(getResources().getString(R.string.destination) + ":" + "\n" + getResources().getString(R.string.latitude) + ":"
                        + upLat + "\n" + getResources().getString(R.string.longitude) + ":" + upLng);
            }
        } else {
            if (mapTypeInt == 2) {
                Gps gps84 = positionTurn(Lat, Lng);
                tv_content.setText(getResources().getString(R.string.destination) + ":" + tit + "\n" + getResources().getString(R.string.latitude) + ":"
                        + gps84.getWgLat() + "\n" + getResources().getString(R.string.longitude) + ":" + gps84.getWgLon());
            } else {
                bt_report.setVisibility(View.GONE);
                bt_delete.setVisibility(View.GONE);
                v_shu.setVisibility(View.GONE);
                tv_content.setText(getResources().getString(R.string.destination) + ":" + tit + "\n" + getResources().getString(R.string.latitude) + ":"
                        + Lat + "\n" + getResources().getString(R.string.longitude) + ":" + Lng);
            }
        }
        bt_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //上报经纬度到蓝牙
                Toast.makeText(MainActivity.this, "可以将经纬度上报到蓝牙设备", Toast.LENGTH_SHORT).show();
            }
        });

        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //删除定位点
                try {
                    myMap.removemMarker(multipleNumber);
                    myMap.hideInfoWindow(multipleNumber);
                    multipleNumber--;
                    latLngList.remove(latLngList.size() - 1);
                    if (multipleNumber == 0) {
                        myMap.removeLine();
                    } else {
                        //百度地图没法删除多余的连线,只能重新连
                        //myMap.deleteBrokenLines(latLngList.size());     //删除折线,.....不能删除地图上的线段,假地图
                        drawDottedLines(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 重新画点和线
     */
    private void drawDottedLines(boolean isScreen) {
        myMap.clearMap();
        for (int i = 1; i < latLngList.size(); i++) {
            myMap.addmMarker(latLngList.get(i).lat, latLngList.get(i).lng, R.drawable.phone_point, "" + multipleNumber, false);
        }
        //        myMap.addPhoneMarker(latLngList.get(0).getLat(), latLngList.get(0).getLng(), R.drawable.m_static_n, getResources().getString(R.string.my_location), false);
        if (searchPoints.size() > 0) {
            for (int i = 0; i < searchPoints.size(); i++) {
                myMap.addNewMarker(searchPoints.get(i).lat, searchPoints.get(i).lng, R.drawable.poi, searchPoints.get(i).content, false);
            }
            if (isScreen) {
                myMap.includePoints(latLngList);
            }
        }
        if (latLngList.size() > 1) {
            if (multipleNumber == 1) {
                myMap.drawmLine(new MyLatLng(latLngList.get(0).getLat(), latLngList.get(0).getLng()), new MyLatLng(latLngList.get(1).lat, latLngList.get(1).lng));
            } else {
                myMap.drawLines(latLngList, null);
            }
            myMap.showInfoWindow(multipleNumber);
        }
    }

    @Override
    public void onClick(View v) {
        if (mapTypeInt == 3) {
            if (!lockM) {
                return;
            }
        }
        switch (v.getId()) {
            case R.id.cb_map_type:      //地图切换
                myMap.setMapType(cb_map_type.isChecked());
                break;
            case R.id.ib_request_location:      //位置搜索
                isSearch = !isSearch;
                if (isSearch) {
                    ll_poi.setVisibility(View.VISIBLE);
                    ib_request_location.setImageResource(R.drawable.btn_location_search_pressed);
                } else {
                    ll_poi.setVisibility(View.GONE);
                    ib_request_location.setImageResource(R.drawable.btn_location_search_normal);
                }
                lv_poi.setVisibility(View.GONE);
                et_search.setText("");
                break;
            case R.id.ib_my_lacation:   //我的位置
                myMap.setZoom(5f);
                myMap.movePoint(latLngList.get(0).getLat(), latLngList.get(0).getLng(), true);
                break;
            case R.id.ib_location_Route:   //路线
                isRoute = !isRoute;
                if (isRoute) {
                    myMap.scrollGesturesEnabled(true);
                    ib_location_Route.setImageResource(R.drawable.btn_design_route_pressed);
                } else {
                    myMap.scrollGesturesEnabled(false);
                    ib_location_Route.setImageResource(R.drawable.btn_design_route_normal);
                }
                break;
        }
    }

    public Gps positionTurn(double lat, double lng) {
        if (mapTypeInt == 2) {
            //中文转,英文不转
            Gps gps84 = PositionUtil.bd09_To_Gps84(lat, lng);
            return gps84;
        }
        return new Gps(lat, lng);
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(MainActivity.this.getWindow().getDecorView().getWindowToken(), 0);
    }
}
