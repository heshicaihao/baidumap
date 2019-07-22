package com.heshicaihao.baidumap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
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
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.sf.recovery.staff.R;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient;
    private MyOrientationListener mOrientationListener;
    public BDLocationListener myListener = new MyLocationListener();
    private float mCurrentX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mMapView = findViewById(R.id.mapview);
        mBaiduMap = mMapView.getMap();
        hideBaidulogo();
        //设置客户家位置
        setUserLocationCenter(22.5362579907, 113.9553466268);

        setLocation();

    }


    //配置定位SDK参数
    private void setLocation() {
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        mMapView.showZoomControls(false);
        
        mLocationClient = new LocationClient(this);     //声明LocationClient类
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mOrientationListener = new MyOrientationListener(this);
        mOrientationListener
                .setmOnOrientationListener(new MyOrientationListener.OnOrientationListener() {

                    @Override
                    public void onOrientationChanged(float x) {
                        mCurrentX = x;
                    }

                });
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        //开启定位
        mLocationClient.start();
        mOrientationListener.start();
        //图片点击事件，回到定位点
        mLocationClient.requestLocation();


        //自定义精度圈填充颜色
        int accuracyCircleFillColor = 0xAAFFFF88;
        //自定义精度圈边框颜色
        int accuracyCircleStrokeColor = 0xAA00FF00;
        mBaiduMap.setMyLocationConfiguration(
                new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.FOLLOWING,
                        false,
                        BitmapDescriptorFactory.fromResource(R.mipmap.location_red),
                        accuracyCircleFillColor,
                        accuracyCircleStrokeColor));


    }

    /**
     * 设置中心点
     */
    private void setUserLocationCenter(double lat, double lon) {
        LatLng cenpt = new LatLng(lat, lon);
        getMarker(cenpt);
        setText(cenpt);
        setCircle(cenpt);
        setStaus(cenpt);

    }


    public class MyLocationListener extends BDAbstractLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
        }
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
        if (zoom!=null){
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
        if (scale!=null){
            scale.setVisibility(View.GONE);
        }
        // 隐藏指南针
        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
        // 删除百度地图logo
        mMapView.removeViewAt(1);
    }

    /**
     * 设置 图标
     *
     * @param cenpt
     * @return
     */
    private void getMarker(LatLng cenpt) {

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
    private void setText(LatLng cenpt) {
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
    private void setCircle(LatLng cenpt) {
        OverlayOptions ooCircle = new CircleOptions().fillColor(0x1A0F81FF)
                .center(cenpt).stroke(new Stroke(0, 0x00000000))
                .radius(1000);
        mBaiduMap.addOverlay(ooCircle);
    }

    /**
     * 定义地图状态
     *
     * @param cenpt
     */
    private void setStaus(LatLng cenpt) {
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(16)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

    }

}
