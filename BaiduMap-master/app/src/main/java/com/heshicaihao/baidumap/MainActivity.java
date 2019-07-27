package com.heshicaihao.baidumap;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.sf.recovery.staff.R;


public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;


    //用户位置
    private LatLng userLatLng;
    //用户家名
    private String userMarkName;
    //已到达签到范围
    private String engineerNormalMarkerName;
    //未到达签到范围
    private String engineerAbNormalMarkerName;
    //到达签到范围
    private  String engineerMarkerName;
    //圆圈半径
    private int radius = 1000;
    //地图缩放层级
    private int zoomLevel = 16;
    //用户标签文字大小
    private int userMarkerSize = 16;
    //工程师标签文字大小
    private int engineerMarkerSize = 16;

    //工程师位置
    private LatLng enginnerLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


        new Thread( new Runnable() {
            @Override
            public void run() {
//                try {
//                    Thread.sleep( 5000 );
                    userLatLng = new LatLng(22.5379695320, 113.9431615175);
                    userMarkName = "王二小的家";
                    engineerMarkerName = "已到达签到范围";
                    startLocation();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        } ).start();
    }


    /**
     * 设置用户家的名字
     *
     * @param userMarkName
     */
    public void userMarkName(String userMarkName) {
        this.userMarkName = userMarkName;
    }

    /**
     * 设置用户经纬度 度
     *
     * @param userLatLngStr
     */
    public void userMarkerCorrdinate(String userLatLngStr) {
        Spot spot = JSON.parseObject(userLatLngStr, Spot.class);
        userLatLng = new LatLng(spot.getLatitude(),spot.getLongitude());
    }

    /**
     * 设置
     * 已到达签到范围
     *
     * @param engineerNormalMarkerName
     */
    public void engineerNormalMarkerName(String engineerNormalMarkerName) {
        this.engineerNormalMarkerName = engineerNormalMarkerName;
    }

    /**
     * 设置
     * 未到达签到范围
     *
     * @param engineerAbNormalMarkerName
     */
    public void engineerAbNormalMarkerName(String engineerAbNormalMarkerName) {
        this.engineerAbNormalMarkerName = engineerAbNormalMarkerName;
    }

    /**
     * 设置
     * 圆圈半径
     *
     * @param radius
     */
    public void radius(int radius) {
        this.radius = radius;
    }

    /**
     * 设置
     * 圆圈半径
     *
     * @param zoomLevel
     */
    public void zoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    /**
     * 设置用 户标签文字大小
     *
     * @param userMarkerSize
     */
    public void userMarkerSize(int userMarkerSize) {
        this.userMarkerSize = userMarkerSize;
    }

    /**
     * 设置用 工程师标签文字大小
     *
     * @param engineerMarkerSize
     */
    public void engineerMarkerSize(int engineerMarkerSize) {
        this.engineerMarkerSize = engineerMarkerSize;
    }



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 定位SDK监听函数
     */
    private class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            enginnerLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (radius>0){
                setUserLocationCircle(userLatLng);
            }
            setEnginnerLocationCenter(enginnerLatLng);
            setUserLocationCenter(userLatLng);
            mLocationClient.stop();
        }
    }

    /**
     * 开启定位
     */
    private void startLocation() {
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    /**
     * 设置中心点
     */
    private void setUserLocationCenter(LatLng cenpt) {
        if (TextUtils.isEmpty(userMarkName)){
            getUserLocationMarker(cenpt);
        }else{
            getBitmapMarker(cenpt);
        }
        setUserLocationStaus(cenpt);

    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    /**
     * 隐藏Baidulogo
     */
    private void hideBaidulogo() {
        // 隐藏缩放控件
        int childCount = mMapView.getChildCount();
        View zoom = null;
        for (int i = 0; i < childCount; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ZoomControls) {
                zoom = child;
                break;
            }
        }
        if (zoom != null) {
            zoom.setVisibility(View.GONE);
        }
        // 隐藏比例尺控件
        int count = mMapView.getChildCount();
        View scale = null;
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ZoomControls) {
                scale = child;
                break;
            }
        }
        if (scale != null) {
            scale.setVisibility(View.GONE);
        }
        // 隐藏指南针
        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
        mMapView.showZoomControls(false);
        // 删除百度地图logo
        mMapView.removeViewAt(1);
    }

    /**
     * 设置 图标
     *
     * @param cenpt
     * @return
     */
    private void getBitmapMarker(LatLng cenpt) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.view_map_location_marker, null);
        TextView txt = view.findViewById(R.id.location_map_marker_name);
        txt.setText(userMarkName);
        txt.setTextSize(userMarkerSize);
        Bitmap bitmap = getViewBitmap(view);
        BitmapDescriptor bitmapdescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        OverlayOptions option = new MarkerOptions().position(cenpt).icon(bitmapdescriptor);
        mBaiduMap.addOverlay(option);

    }

    /**
     * 设置 图标
     *
     * @param cenpt
     * @return
     */
    private void getEngineerLocationMarker(LatLng cenpt) {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                .fromResource(R.mipmap.location_red);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(cenpt)
                .icon(bitmapDescriptor);
        //添加覆盖物
        mBaiduMap.addOverlay(option);

    }


    /**
     * 设置文字
     *
     * @param cenpt
     */
    private void setEngineerLocationText(LatLng cenpt) {
        //用来构造InfoWindow的Button
        TextView button = new TextView(getApplicationContext());
        button.setBackgroundResource(R.drawable.bg_location_text);
        button.setTextColor(0xFF333333);
        button.setTextSize(engineerMarkerSize);
        button.setPadding(15, 2, 15, 2);
        Double distance = DistanceUtil.getDistance(userLatLng, enginnerLatLng);
        Log.d("distance:", "" + distance);
        button.setText(engineerMarkerName);
        //-100 InfoWindow相对于point在y轴的偏移量
        InfoWindow mInfoWindow = new InfoWindow(button, cenpt, -50);
        //使InfoWindow生效
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    /**
     * 设置 图标
     *
     * @param cenpt
     * @return
     */
    private void getUserLocationMarker(LatLng cenpt) {

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                .fromResource(R.mipmap.location_home);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(cenpt)
                .icon(bitmapDescriptor);
        //添加覆盖物
        mBaiduMap.addOverlay(option);
    }

    /**
     * 设置文字
     *
     * @param cenpt
     */
    private void setUserLocationText(LatLng cenpt) {
        //用来构造InfoWindow的Button
        TextView button = new TextView(getApplicationContext());
        button.setBackgroundResource(R.drawable.bg_location_text);
        button.setTextColor(0xFF333333);
        button.setTextSize(16);
        button.setPadding(15, 2, 15, 2);
        button.setText("张小白家");
        //-100 InfoWindow相对于point在y轴的偏移量
        InfoWindow mInfoWindow = new InfoWindow(button, cenpt, -60);
        //使InfoWindow生效
        mBaiduMap.showInfoWindow(mInfoWindow);
    }


    /**
     * 画圆圈
     *
     * @param cenpt
     */
    private void setUserLocationCircle(LatLng cenpt) {
        OverlayOptions ooCircle = new CircleOptions().fillColor(0x1A0F81FF)
                .center(cenpt).stroke(new Stroke(0, 0x00000000))
                .radius(radius);
        mBaiduMap.addOverlay(ooCircle);
    }

    /**
     * 定义地图状态
     *
     * @param cenpt
     */
    private void setUserLocationStaus(LatLng cenpt) {
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(zoomLevel)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

    }


    private void initLocation() {
        //初始化LocationClient类
        mLocationListener = new MyLocationListener();
        mLocationClient = new LocationClient(this);     //声明LocationClient类
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        //开启定位
        mLocationClient.start();
        mLocationClient.registerLocationListener(mLocationListener);    //注册监听函数
    }


    /**
     * 设置中心点
     */
    private void setEnginnerLocationCenter(LatLng cenpt) {
        getEngineerLocationMarker(cenpt);
        if (!TextUtils.isEmpty(engineerMarkerName)){
            setEngineerLocationText(cenpt);
        }


    }
    /**
     * 计算两点之间距离
     * @param start
     * @param end
     * @return 米
     *
     */
    /**
     * @param p1
     * @param p2
     * @return
     */
    public Double getDistance(LatLng p1, LatLng p2) {
        double distance = DistanceUtil.getDistance(p1, p2);
        return distance;
    }

    //把布局变成Bitmap
    private Bitmap getViewBitmap(View addViewContent) {

        addViewContent.setDrawingCacheEnabled(true);

        addViewContent.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0,
                addViewContent.getMeasuredWidth(),
                addViewContent.getMeasuredHeight());

        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        return bitmap;
    }


    private void initView() {
        mMapView = findViewById(R.id.mapview);
        mBaiduMap = mMapView.getMap();
        hideBaidulogo();
        //设置客户家位置
//        userLatLng = new LatLng(22.5362579907,113.9553466268);
//        userLatLng = new LatLng(22.5379116697, 113.9313512825);
//        userLatLng = new LatLng(22.5379695320,113.9431615175);
        initLocation();
        startLocation();

    }


}
