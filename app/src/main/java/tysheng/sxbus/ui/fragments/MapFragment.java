package tysheng.sxbus.ui.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.baidu.mapapi.map.BaiduMap;

import java.util.ArrayList;

import butterknife.BindView;
import tysheng.sxbus.R;
import tysheng.sxbus.base.BaseFragment;
import tysheng.sxbus.presenter.impl.MapPresenterImpl;
import tysheng.sxbus.ui.activities.ToolbarActivity;
import tysheng.sxbus.ui.inter.MapView;

/**
 * Created by tysheng
 * Date: 2017/3/11 15:21.
 * Email: tyshengsx@gmail.com
 */

public class MapFragment extends BaseFragment<MapPresenterImpl> implements MapView {
    @BindView(R.id.mapView)
    com.baidu.mapapi.map.MapView mMapView;

    public static MapFragment newInstance(ArrayList<Parcelable> list, ArrayList<Parcelable> stations, Parcelable parcelable) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("0", list);
        args.putParcelableArrayList("1", stations);
        args.putParcelable("2", parcelable);
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setSubtitle(String subtitle) {
        ((ToolbarActivity) getActivity()).setSubtitle(subtitle);
    }

    @Override
    protected MapPresenterImpl initPresenter() {
        return new MapPresenterImpl(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.setArgs(getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
    }

    @Override
    protected void initData() {
        mPresenter.initData();
    }

    @Override
    public BaiduMap getMap() {
        return mMapView.getMap();
    }

    @Override
    public void refreshLocation() {
        mPresenter.refreshLocation();
    }

    @Override
    public void drawStations() {
        mPresenter.drawStations();
    }
}
