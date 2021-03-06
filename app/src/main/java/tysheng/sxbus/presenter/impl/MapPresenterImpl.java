package tysheng.sxbus.presenter.impl;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.ArrayList;

import javax.inject.Inject;

import tysheng.sxbus.R;
import tysheng.sxbus.bean.MapInfo;
import tysheng.sxbus.bean.Stations;
import tysheng.sxbus.model.impl.DrawModelImpl;
import tysheng.sxbus.presenter.base.AbstractPresenter;
import tysheng.sxbus.presenter.inter.MapPresenter;
import tysheng.sxbus.ui.inter.MapView;
import tysheng.sxbus.utils.LogUtil;
import tysheng.sxbus.utils.MapUtil;
import tysheng.sxbus.utils.SnackBarUtil;
import tysheng.sxbus.utils.TyLocationListener;
import tysheng.sxbus.utils.UiUtil;

/**
 * Created by tysheng
 * Date: 2017/3/11 15:22.
 * Email: tyshengsx@gmail.com
 */

public class MapPresenterImpl extends AbstractPresenter<MapView> implements MapPresenter {

    private BaiduMap mBaiduMap;
    private ArrayList<? extends MapInfo> mResultList;
    private ArrayList<? extends MapInfo> mStationsList;
    private DrawModelImpl mDrawModel;
    private Stations mClickStations;
    private int type;
    private String locating, locateFail;

    @Inject
    public MapPresenterImpl(MapView view) {
        super(view);
        mDrawModel = new DrawModelImpl();
    }

    @Override
    public void setArgs(Bundle bundle) {
        mClickStations = bundle.getParcelable("2");
        mResultList = bundle.getParcelableArrayList("0");
        mStationsList = bundle.getParcelableArrayList("1");
        type = bundle.getInt("-1");
        locating = getContext().getString(R.string.locating);
        locateFail = getContext().getString(R.string.locate_fail);
        if (mClickStations != null) {
            mView.setSubtitle(mClickStations.stationName);
        }
    }

    @Override
    public void initData() {
        mBaiduMap = mView.getMap();
        initMap();
        mDrawModel.drawStations(type, getBaiduMap(), mStationsList);
        mDrawModel.drawBuses(getBaiduMap(), mResultList);
    }

    private void initMap() {
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setIndoorEnable(true);

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        //绍兴市大概区域
        LatLng northeast = new LatLng(29.7, 121.0);
        LatLng southwest = new LatLng(30.33, 120.26);
        mBaiduMap.setMapStatusLimits(new LatLngBounds.Builder().include(northeast).include(southwest).build());
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int position = marker.getZIndex();
                if (position < 1000) {
                    MapInfo item = mStationsList.get(position);
                    TextView tv = new TextView(getContext());
                    int padding = UiUtil.dp2px(5f);
                    tv.setPadding(padding, padding, padding, padding);
                    tv.setBackgroundColor(Color.BLACK);
                    tv.setTextColor(Color.WHITE);
                    tv.setGravity(Gravity.CENTER);
                    tv.setText(item.getName());
                    LatLng ll = marker.getPosition();
                    InfoWindow infoWindow = new InfoWindow(tv, ll, -(UiUtil.dp2px(25)));
                    mBaiduMap.showInfoWindow(infoWindow);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBaiduMap.hideInfoWindow();
                        }
                    });
                }
                return true;
            }
        });
        refreshLocation(true);
    }

    private LatLng findClickLocation() {
        if (mClickStations != null) {
            return new LatLng(mClickStations.lat, mClickStations.lng);
        }
        return null;
    }

    @Override
    public void onDestroy() {
        mBaiduMap.clear();
        mBaiduMap.setMyLocationEnabled(false);
        super.onDestroy();
    }

    private void refreshLocation(final boolean useClickPosition) {
        mView.setSubtitle(locating);
        MapUtil.getInstance().getLocation(getContext().getApplicationContext(), new TyLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                super.onReceiveLocation(location);
                LogUtil.d(getDetail(location));
                int code = location.getLocType();
                if (!(code == 161 || code == 66 || code == 61)) {
                    SnackBarUtil.show(mView.getRootView(), locateFail);
                }
                setLocation(location, useClickPosition);
            }
        });
    }

    private void setLocation(final BDLocation location, boolean useClickPosition) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MyLocationData locData = new MyLocationData.Builder()
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(latLng.latitude)
                .longitude(latLng.longitude).build();
        mBaiduMap.setMyLocationData(locData);

        /**
         * 是否是点击进来的，如果是就跳转过去
         */
        LatLng finalLatLng = null;
        if (useClickPosition) {
            finalLatLng = findClickLocation();
        }
        String subTitle = null;
        if (finalLatLng == null) {
            finalLatLng = latLng;
            Address address = location.getAddress();
            if (!TextUtils.isEmpty(address.district)) {
                subTitle = address.district;
                if (!TextUtils.isEmpty(address.street)) {
                    subTitle += address.street;
                }
            }
        } else {
            mDrawModel.drawSinglePlace(getBaiduMap(), finalLatLng);
            subTitle = mClickStations.stationName;
        }
        setCenterPoint(subTitle, finalLatLng);
    }

    private void setCenterPoint(String subTitle, LatLng latLng) {
        // 设定中心点坐标
        // 定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(latLng)
                .zoom(17f).build();
        // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mMapStatus);
        // 改变地图状态
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
//        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
//        mBaiduMap.animateMapStatus(update);
        if (TextUtils.isEmpty(subTitle)) {
            subTitle = locateFail;
        }
        mView.setSubtitle(subTitle);
    }

    private BaiduMap getBaiduMap() {
        return mBaiduMap;
    }

    @Override
    public void onMenuItemClick(int itemId) {
        switch (itemId) {
            case R.id.action_refresh:
                refreshLocation(true);
                break;
            case R.id.action_draw_station:
                drawStations();
                break;
            case R.id.action_switch_traffic:
                switchTraffic();
                break;
            case R.id.action_switch_heat:
                if (mBaiduMap.isSupportBaiduHeatMap()) {
                    //开启热力图
                    mBaiduMap.setBaiduHeatMapEnabled(!mBaiduMap.isBaiduHeatMapEnabled());
                }
                break;
            default:
                break;
        }
    }

    private void switchTraffic() {
        //开启交通图
        mBaiduMap.setTrafficEnabled(!mBaiduMap.isTrafficEnabled());
    }

    private void drawStations() {
        mDrawModel.drawStationsClick(type, getBaiduMap());
    }

    public void setToMyLocation() {
        refreshLocation(false);
    }
}
