package songwei520.map3synthesis1;

import android.app.Activity;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.WindowManager.BadTokenException;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

import songwei520.map3synthesis1.bean.MyLatLng;
import songwei520.map3synthesis1.customView.MProgressDialog;
import songwei520.map3synthesis1.utils.SharedPreferencesHelper;
import songwei520.map3synthesis1.utils.SharedPreferencesNameFile;

public class MySearch implements GoogleApiClient.OnConnectionFailedListener {
    private Activity mContext;
    private static final String TAG = "MySearch";
    private PlacesClient placesClient;
    YWSearchKeyWordListion mYWSearchKeyWordListion;
    YWSearchKeyWordListion2 mYWSearchKeyWordListion2;
    private final SharedPreferencesHelper sp;

    public MySearch(Activity mContext) {
        this.mContext = mContext;
        sp = new SharedPreferencesHelper(mContext);
        Places.initialize(mContext, "AIzaSyACT0JRj7E5aS2_p9Qx4WfbrmscIGYt9d8");
        placesClient = Places.createClient(mContext);
    }

    public void searchKeyWord(String keyword, YWSearchKeyWordListion mYWSearchKeyWordListion) {
        this.mYWSearchKeyWordListion = mYWSearchKeyWordListion;
        final String newText = keyword.toString().trim();
        if (!TextUtils.isEmpty(newText)) {
            switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
                case 1:
                    InputtipsQuery inputquery = new InputtipsQuery(newText, "");
                    Inputtips inputTips = new Inputtips(mContext, inputquery);
                    inputTips.setInputtipsListener(new InputtipsListener() {
                        @Override
                        public void onGetInputtips(List<Tip> tipList, int rCode) {
                            if (rCode == 1000) {// 正确返回
                                List<String> listString = new ArrayList<String>();
                                for (int i = 0; i < tipList.size(); i++) {
                                    listString.add(tipList.get(i).getName());
                                }
                                MySearch.this.mYWSearchKeyWordListion.Result(listString);
                            } else {
                                MySearch.this.mYWSearchKeyWordListion.Result(null);
                            }
                        }
                    });
                    inputTips.requestInputtipsAsyn();
                    break;
                case 2:
                    // 初始化建议搜索模块，注册建议搜索事件监听
                    final SuggestionSearch mSuggestionSearch = SuggestionSearch.newInstance();
                    mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {

                        @Override
                        public void onGetSuggestionResult(SuggestionResult res) {
                            if (res == null || res.getAllSuggestions() == null) {
                                MySearch.this.mYWSearchKeyWordListion.Result(null);
                                mSuggestionSearch.destroy();
                                return;
                            }
                            List<String> suggest = new ArrayList<String>();
                            for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
                                if (info.pt == null || info.pt.longitude == 0.0 || info.pt.latitude == 0.0) {
                                    continue;
                                }
                                if (info.key != null) {
                                    suggest.add(info.key);
                                }
                            }
                            MySearch.this.mYWSearchKeyWordListion.Result(suggest);
                            mSuggestionSearch.destroy();
                        }
                    });
                    if (newText.length() <= 0) {
                        mYWSearchKeyWordListion.Result(null);
                        mSuggestionSearch.destroy();
                        return;
                    }

                    /**
                     * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                     */
                    if (sp.getSharedPreference(SharedPreferencesNameFile.City,"") != null)
                        mSuggestionSearch
                                .requestSuggestion((new SuggestionSearchOption())
                                        .keyword(newText.toString())
                                        .city((String)sp.getSharedPreference(SharedPreferencesNameFile.City,"")));
                    break;
                case 3:
                    FindAutocompletePredictionsRequest currentPlaceRequest = FindAutocompletePredictionsRequest.newInstance(newText);
                    Task<FindAutocompletePredictionsResponse> currentPlaceTask = placesClient.findAutocompletePredictions(currentPlaceRequest);
                    currentPlaceTask.addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {

                        @Override
                        public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
                            if (findAutocompletePredictionsResponse != null && findAutocompletePredictionsResponse.getAutocompletePredictions() != null && findAutocompletePredictionsResponse.getAutocompletePredictions().size() > 0) {
                                List<String> strList = new ArrayList<String>();
                                for (int i = 0; i < findAutocompletePredictionsResponse.getAutocompletePredictions().size(); i++) {
                                    strList.add(findAutocompletePredictionsResponse.getAutocompletePredictions().get(i).getFullText(STYLE_BOLD).toString());
                                }
                                MySearch.this.mYWSearchKeyWordListion.Result(strList);
                            }
                        }
                    });
                    currentPlaceTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });

                    break;
            }

        }
    }


    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }

    public interface YWSearchKeyWordListion {
        void Result(List<String> list);
    }

    public void searchAddress(String keyword, final YWSearchAddressListion mYWSearchAddressListion) {
        final String newText = keyword.toString().trim();
        if (!TextUtils.isEmpty(newText)) {
            switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
                case 1:
                    startProgressDialog(mContext.getResources().getString(R.string.wait));// 显示进度框
                    final PoiSearch.Query query = new PoiSearch.Query(newText, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
                    query.setPageSize(10);// 设置每页最多返回多少条poiitem
                    query.setPageNum(0);// 设置查第一页
                    query.setCityLimit(true);

                    PoiSearch poiSearch;
                    poiSearch = new PoiSearch(mContext, query);
                    poiSearch.setOnPoiSearchListener(new OnPoiSearchListener() {

                        @Override
                        public void onPoiSearched(PoiResult result, int rCode) {
                            stopProgressDialog();// 隐藏对话框
                            if (rCode == 1000) {
                                if (result != null && result.getQuery() != null) {// 搜索poi的结果
                                    if (result.getQuery().equals(query)) {// 是否是同一条
                                        // 取得搜索到的poiitems有多少页
                                        List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                                        List<SuggestionCity> suggestionCities = result
                                                .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                                        ArrayList<MyLatLng> latLngList = new ArrayList<MyLatLng>();
                                        if (poiItems != null && poiItems.size() > 0) {
                                            for (PoiItem mPoiItem : poiItems) {
                                                if (mPoiItem.getLatLonPoint() == null || mPoiItem.getLatLonPoint().getLongitude() == 0.0 ||  mPoiItem.getLatLonPoint().getLatitude() == 0.0) {
                                                    continue;
                                                }
                                                latLngList.add(new MyLatLng(mPoiItem.getLatLonPoint().getLatitude(),
                                                        mPoiItem.getLatLonPoint().getLongitude(),
                                                        mPoiItem.toString()));
                                            }
                                            mYWSearchAddressListion.Result(latLngList);
                                        } else if (suggestionCities != null && suggestionCities.size() > 0) {
                                        } else {
                                        }
                                    }
                                } else {
                                }
                            } else {
                            }
                        }

                        @Override
                        public void onPoiItemSearched(PoiItem item, int rCode) {
                            // TODO Auto-generated method stub

                        }
                    });
                    poiSearch.searchPOIAsyn();
                    break;
                case 2:
                    startProgressDialog(mContext.getResources().getString(R.string.wait));// 显示进度框
                    // 初始化搜索模块，注册搜索事件监听
                    com.baidu.mapapi.search.poi.PoiSearch mPoiSearch = com.baidu.mapapi.search.poi.PoiSearch.newInstance();
                    mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
                        /**
                         * 获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
                         * @param result
                         */
                        @Override
                        public void onGetPoiResult(com.baidu.mapapi.search.poi.PoiResult result) {
                            stopProgressDialog();// 隐藏对话框
                            if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND || result.getAllPoi() == null) {    //没有找到结果
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.no_latitude_and_longitude), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                                if (result == null || result.getAllPoi() == null) {
                                    return;
                                }
                                ArrayList<MyLatLng> latLngList = new ArrayList<MyLatLng>();
                                int markerSize = 0;
                                for (int i = 0; i < result.getAllPoi().size() && markerSize < 10; i++) {
                                    if (result.getAllPoi().get(i).location == null) {
                                        continue;
                                    }
                                    latLngList.add(new MyLatLng(result.getAllPoi().get(i).location.latitude,
                                            result.getAllPoi().get(i).location.longitude, result.getAllPoi().get(i).name));
                                }
                                mYWSearchAddressListion.Result(latLngList);
                            }
                            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
                            }
                        }

                        @Override
                        public void onGetPoiIndoorResult(PoiIndoorResult result) {
                            // TODO Auto-generated method stub

                        }

                        /**
                         * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结果
                         * @param result
                         */
                        @Override
                        public void onGetPoiDetailResult(PoiDetailResult result) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

                        }
                    });
                    PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption().keyword(newText)
                            .sortType(PoiSortType.distance_from_near_to_far)
                            .location(new LatLng(Double.parseDouble((String)sp.getSharedPreference(SharedPreferencesNameFile.Latitude,"")),
                                    Double.parseDouble((String)sp.getSharedPreference(SharedPreferencesNameFile.Longitude,""))))
                            .radius(10000)        //搜索半径10000米
                            .pageNum(0);        //默认每页10条
                    mPoiSearch.searchNearby(nearbySearchOption);

					/*LatLngBounds searchbound = new LatLngBounds.Builder().include(new com.baidu.mapapi.model.LatLng(sp.getSharedPreference(SharedPreferencesNameFile.Latitude,""),
                            sp.getSharedPreference(SharedPreferencesNameFile.Longitude,""))).build();
					mPoiSearch.searchInBound(new PoiBoundSearchOption()
							.keyword(newText).bound(searchbound));*/
                    break;
                case 3:
//                    new Thread(new Runnable() {
////                        @Override
////                        public void run() {
////                            // TODO Auto-generated method stub
////                            try {
////
////                            } catch (IOException e) {
////                                // TODO Auto-generated catch block
////                                e.printStackTrace();
////                            }
////                        }
////                    }).start();
                    try {
                        Geocoder geocoder = new Geocoder(mContext);
                        List<Address> addressList = geocoder.getFromLocationName(newText, 10);
                        if (addressList != null && addressList.size() > 0) {
                            ArrayList<MyLatLng> latLngList = new ArrayList<MyLatLng>();
                            for (Address mAddress : addressList) {
                                latLngList.add(new MyLatLng(mAddress.getLatitude(),
                                        mAddress.getLongitude(),
                                        mAddress.getAddressLine(1) + " " + mAddress.getFeatureName()));
                            }
                            mYWSearchAddressListion.Result(latLngList);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
            }

        }
    }

    /**搜索联想地址*/
    public interface YWSearchKeyWordListion2{
        void Result(Object list);
    }

    /**直接使用关键字的经纬度*/
    public void searchKeyWord2(String keyword, YWSearchKeyWordListion2 mYWSearchKeyWordListion2) {
        this.mYWSearchKeyWordListion2 = mYWSearchKeyWordListion2;
        final String newText = keyword.toString().trim();
        if (!TextUtils.isEmpty(newText)) {
            switch ((int)sp.getSharedPreference(SharedPreferencesNameFile.MapTypeInt,2)) {
                case 1:
                    break;
                case 2:
                    // 初始化建议搜索模块，注册建议搜索事件监听
                    final SuggestionSearch mSuggestionSearch = SuggestionSearch.newInstance();
                    mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {

                        @Override
                        public void onGetSuggestionResult(SuggestionResult res) {
                            if (res == null || res.getAllSuggestions() == null) {
                                MySearch.this.mYWSearchKeyWordListion2.Result(null);
                                mSuggestionSearch.destroy();
                                return;
                            }
                            Object obj;
                            obj = res;
                            MySearch.this.mYWSearchKeyWordListion2.Result(obj);
                            mSuggestionSearch.destroy();
                        }
                    });
                    if (newText.length() <= 0) {
                        mYWSearchKeyWordListion2.Result(null);
                        mSuggestionSearch.destroy();
                        return;
                    }

                    /**
                     * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                     */
                    if (sp.getSharedPreference(SharedPreferencesNameFile.City,"") != null)
                        mSuggestionSearch
                                .requestSuggestion((new SuggestionSearchOption())
                                        .keyword(newText.toString()).city((String)sp.getSharedPreference(SharedPreferencesNameFile.City,"")));
                    break;
                case 3:
                    break;
            }

        }
    }

    public interface YWSearchAddressListion {
        void Result(ArrayList<MyLatLng> list);
    }

    private MProgressDialog mProgressDialog = null;

    private void startProgressDialog(String dialog) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = MProgressDialog.createDialog(mContext);
                mProgressDialog.setMessage(dialog);
                mProgressDialog.setCancelable(false);
            }
            mProgressDialog.show();
        } catch (BadTokenException e) {

        }
    }

    private void stopProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
