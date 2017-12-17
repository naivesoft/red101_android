package com.naivesoft.android.see;

import android.Manifest;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.VisibleRegion;
import com.naivesoft.android.see.amap.AMapHelpers;
import com.naivesoft.android.see.mapblock.MapBlockHelper;
import com.naivesoft.android.see.model.UserPositionInfo;
import com.naivesoft.android.see.socket.RealTimePositionSocket;
import com.naivesoft.android.see.socket.SocketManager;
import com.naivesoft.android.see.socket.UserInfoSocket;
import com.naivesoft.android.see.util.AndroidUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class HomeActivity extends FragmentActivity implements LocationSource, AMapLocationListener, RealTimePositionSocket.RealTimePositionChangeListener,
        AMap.OnCameraChangeListener, AMap.OnMapLoadedListener, UserInfoSocket.ActiveACKListener,
        View.OnClickListener {

    private final static String TAG = HomeActivity.class.getSimpleName();

    private MapView mMapView = null;
    private AMap aMap;
    private UiSettings mUiSettings;

    private View mLocationButton;

    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private SocketManager mSocketManager;
    private RealTimePositionSocket mRealTimePositionSocket;
    private UserInfoSocket mUserInfoSocket;

    private OnLocationChangedListener mOnLocationChangedListener;

    private Map<String, Marker> mKissersMap = new HashMap<>();

    private LatLng mCurrentPosition = null;
    private Set<String> mCurrentLookBlockList = new HashSet<>();

    private boolean userWantToMoveToCurrentPosition = false;
    private float mCurrentZoom = 10;
    private long mLastActiveTime = 0;

    private Timer timer;
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mLastActiveTime + 10 * 60 * 1000L < System.currentTimeMillis()) {
                reportActive();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //获取地图控件引用
        mMapView = findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        mLocationButton = findViewById(R.id.location_bt);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mLocationButton.getLayoutParams();
        layoutParams.bottomMargin = AndroidUtils.dip2px(this, 20) + AndroidUtils.getNavigationBarHeight(this);
        layoutParams.rightMargin = AndroidUtils.dip2px(this, 20);
        mLocationButton.setLayoutParams(layoutParams);
        mLocationButton.setOnClickListener(this);

        HomeActivityPermissionsDispatcher.initWithPermissionCheck(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE})
    public void init() {
        initSocket();
        initAmap();
    }

    private void initSocket() {
        mSocketManager = SocketManager.getInstance();
        mUserInfoSocket = new UserInfoSocket(this, mSocketManager);
        mUserInfoSocket.setOnActiveACKListener(this);
        mSocketManager.connect();
        mRealTimePositionSocket = new RealTimePositionSocket(this, mSocketManager, this);
        reportActive();
    }

    private void initAmap() {
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();
            mUiSettings = aMap.getUiSettings();
        }

        setMapUI();
        setLocationStyle();
        setMapTheme();

        aMap.setOnCameraChangeListener(this);
        aMap.setOnMapLoadedListener(this);

//        aMap.moveCamera(CameraUpdateFactory.zoomTo(Float.valueOf(5)));
    }

    // 设置地图UI样式
    private void setMapUI() {
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);// 设置地图logo显示在左下方
        mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        mUiSettings.setScaleControlsEnabled(true);
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setCompassEnabled(false);
    }

    private void setLocationStyle() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.setMyLocationEnabled(true);// 是否可触发定位并显示定位层
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(20000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
        myLocationStyle.showMyLocation(true);
        myLocationStyle.strokeColor(android.R.color.transparent);
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    private void setMapTheme() {
        //        aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        AMapHelpers.setMapCustomStyleFile(aMap, this);
    }

    private void reportActive() {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(mTimerTask, 0, 1000);
        }
        if (mUserInfoSocket != null) {
            mUserInfoSocket.reportUserActive();
        }
    }

    @Override
    public void onActive() {
        mLastActiveTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
//        mSocketManager.disConnect();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        HomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mOnLocationChangedListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
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
        mOnLocationChangedListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mOnLocationChangedListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                if (userWantToMoveToCurrentPosition) {
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(amapLocation.getLatitude(),
                            amapLocation.getLongitude()), 12));
                    userWantToMoveToCurrentPosition = false;
                }
                mOnLocationChangedListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                sendLocationInfoToServer(amapLocation.getLongitude(), amapLocation.getLatitude());
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
            }
        }
    }

    @Override
    public void onKissersPositionChange(List<UserPositionInfo> userPositionInfos) {
        for (UserPositionInfo info : userPositionInfos) {
            LatLng latLng = new LatLng(info.latitude, info.longtitude);
            if (mKissersMap.containsKey(info.userId)) {
                mKissersMap.get(info.userId).setPosition(latLng);
            } else {
                final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.icon36))));
                mKissersMap.put(info.userId, marker);
            }
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        // currently do nothing
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        VisibleRegion visibleRegion = aMap.getProjection().getVisibleRegion();
        Log.v("tag_finish", visibleRegion.toString());
        Log.v("tag_finish", cameraPosition.toString());

        displayLookBlockList(visibleRegion, cameraPosition);
    }

    @Override
    public void onMapLoaded() {

        VisibleRegion visibleRegion = aMap.getProjection().getVisibleRegion();
        CameraPosition cameraPosition = aMap.getCameraPosition();
        Log.v("tag_loaded", visibleRegion.toString());
        Log.v("tag_loaded", cameraPosition.toString());
        displayLookBlockList(visibleRegion, cameraPosition);
    }

    private void sendLocationInfoToServer(double lon, double lat) {
        //Log.v(TAG, "onGetLocation:" + new LatLng(lat, lon).toString());
        if (mCurrentPosition != null && AMapUtils.calculateLineDistance(mCurrentPosition, new LatLng(lat, lon)) < 20.0f) {
            //定位距离间隔小于20米，不上报
            return;
        } else {
            Log.v(TAG, "    needToSendPositionToServer:" + new LatLng(lat, lon).toString());
            mCurrentPosition = new LatLng(lat, lon);
            boolean result = mRealTimePositionSocket.sendClientPosition(mCurrentPosition);
            if (!result) {
                mCurrentPosition = null;
            }
        }
    }

    private void displayLookBlockList(VisibleRegion visibleRegion, CameraPosition cameraPosition) {
        if (visibleRegion == null || cameraPosition == null) {
            return;
        }
        mCurrentZoom = cameraPosition.zoom;
        double scale = 25 * Math.pow(2, (18 - cameraPosition.zoom));
        Set<String> lookBlockList = new HashSet<>();
        // center
        lookBlockList.addAll(MapBlockHelper.getLookBlockList(scale, cameraPosition.target));
        // left right top bottom
        lookBlockList.addAll(MapBlockHelper.getLookBlockList(scale, visibleRegion.farLeft));
        lookBlockList.addAll(MapBlockHelper.getLookBlockList(scale, visibleRegion.farRight));
        lookBlockList.addAll(MapBlockHelper.getLookBlockList(scale, visibleRegion.nearLeft));
        lookBlockList.addAll(MapBlockHelper.getLookBlockList(scale, visibleRegion.nearRight));

        Log.v(TAG, "displayLookBlockList:" + lookBlockList.toString());
        if (mCurrentLookBlockList.containsAll(lookBlockList)) {
            // ignore
        } else {
            Log.v(TAG, "needToGetLookBlockListFromServer:" + lookBlockList.toString());
            mCurrentLookBlockList.clear();
            mCurrentLookBlockList.addAll(lookBlockList);
            boolean result = mRealTimePositionSocket.sendLookBlockListAndGetKissers(mCurrentLookBlockList);
            if (!result) {
                mCurrentLookBlockList.clear();
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.location_bt) {
            // move to current location
            if (mCurrentPosition != null) {
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentPosition, mCurrentZoom));
                userWantToMoveToCurrentPosition = false;
            } else {
                userWantToMoveToCurrentPosition = true;
            }

        }
    }
}
