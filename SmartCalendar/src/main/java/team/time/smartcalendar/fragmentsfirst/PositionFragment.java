package team.time.smartcalendar.fragmentsfirst;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amap.api.maps.AMap;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.*;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import dagger.hilt.android.AndroidEntryPoint;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.adapters.POIRecyclerViewAdapter;
import team.time.smartcalendar.dataBeans.POIItem;
import team.time.smartcalendar.databinding.FragmentPositionBinding;
import team.time.smartcalendar.utils.LocationUtils;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.utils.UserUtils;
import team.time.smartcalendar.viewmodels.PositionViewModel;
import team.time.smartcalendar.viewmodels.ScheduleViewModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@AndroidEntryPoint
public class PositionFragment extends Fragment {
    private FragmentPositionBinding binding;
    private NavController controller;
    private Activity parentActivity;
    public AMap map;
    private UiSettings uiSettings;
    private MyLocationStyle locationStyle;
    private PositionViewModel viewModel;
    private RecyclerView.LayoutManager manager;
    private POIRecyclerViewAdapter adapter;

    private int lastLength=0;
    public boolean[] canFinish;
    public String[] positionName;
    public LatLonPoint point;

    private ScheduleViewModel scheduleViewModel;

    @Inject
    SharedPreferences sp;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity=requireActivity();

        canFinish=new boolean[1];
        positionName=new String[1];
        positionName[0]="";
        point=new LatLonPoint(0.0,0.0);

        Bundle bundle=getArguments();
        if(bundle!=null){
            scheduleViewModel= (ScheduleViewModel) bundle.getSerializable("viewModel");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 重写回退键
        SystemUtils.setBack(this);

        viewModel = new ViewModelProvider(this).get(PositionViewModel.class);
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_position,container,false);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"位置",R.drawable.ic_baseline_close_24,R.drawable.ic_baseline_check_24);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        setMapView(savedInstanceState);
        setMap();

        if(!scheduleViewModel.getPosition().getValue().equals("")){
            LocationUtils.addMarker(map,scheduleViewModel.latitude,scheduleViewModel.longitude);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        manager = new LinearLayoutManager(parentActivity);
        binding.viewNeighbors.setLayoutManager(manager);
        adapter = new POIRecyclerViewAdapter(this,binding.viewNeighbors,viewModel.getPoiItems().getValue());
        binding.viewNeighbors.setAdapter(adapter);

        viewModel.getPoiItems().observe(this, poiItems -> {
            adapter.setItems(poiItems);
            adapter.notifyDataSetChanged();
        });
        viewModel.getSearchInfo().observe(this, s -> {
            if(s.equals("")){
                searchAroundPOI(UserUtils.getMyLocation());
                binding.textInfo.setText("附 近");
            }else{
                if(s.length()>=2 || lastLength<s.length()){
                    searchPOI(s);
                    binding.textInfo.setText("搜 索 结 果");
                }
            }
            lastLength=s.length();
        });

        viewModel.getCity().observe(this, s -> {
            if(!viewModel.getSearchInfo().getValue().equals("")){
                if(s.equals("") || s.length()>=2){
                    searchPOI(viewModel.getSearchInfo().getValue());
                }
            }
        });

        // 获取当前城市
        getCIty();

        controller = Navigation.findNavController(view);

        binding.imageLocate.setOnClickListener(v -> {
            LocationUtils.animateCamera(map,UserUtils.USER_LATITUDE,UserUtils.USER_LONGITUDE);
        });

        binding.action.imageLeft.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(parentActivity,getEditTextList());
            controller.popBackStack();
        });

        binding.action.imageRight.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(parentActivity,getEditTextList());
            if(canFinish[0] && !positionName[0].equals("")){
                scheduleViewModel.getPosition().setValue(positionName[0]);
                scheduleViewModel.latitude=point.getLatitude();
                scheduleViewModel.longitude=point.getLongitude();

                controller.popBackStack();
            }else {
                Toast.makeText(parentActivity, "请选择地点", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

        binding.mapPosition.onSaveInstanceState(outState);
    }

    private List<View> getEditTextList(){
        List<View>views=new ArrayList<>();
        views.add(binding.editTextSearch);
        return views;
    }

    private void setMapView(Bundle savedInstanceState) {
        binding.mapPosition.onCreate(savedInstanceState);
        getLifecycle().addObserver(binding.mapPosition);
    }

    private void setMap() {
        map=binding.mapPosition.getMap();
        uiSettings = map.getUiSettings();
        locationStyle = new MyLocationStyle();
        // 单次定位
        locationStyle.myLocationType((MyLocationStyle.LOCATION_TYPE_SHOW));
        // 位置图标
        locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
        // 边框颜色
        locationStyle.strokeWidth(0);
        // 精度圈颜色
        locationStyle.radiusFillColor(Color.argb(20, 0, 0, 180));
        // 应用样式
        map.setMyLocationStyle(locationStyle);
        // 开启定位
        map.setMyLocationEnabled(true);
        LocationUtils.moveCamera(map,UserUtils.USER_LATITUDE,UserUtils.USER_LONGITUDE);
        // 添加位置改变监听
        map.setOnMyLocationChangeListener(location -> {
            if(location!=null){
                // 更新本地位置
                UserUtils.USER_LATITUDE=location.getLatitude();
                UserUtils.USER_LONGITUDE=location.getLongitude();
                // 移动地图中心
                if(!scheduleViewModel.getPosition().getValue().equals("")){
                    LocationUtils.animateCamera(map,scheduleViewModel.latitude,scheduleViewModel.longitude);
                }else {
                    LocationUtils.animateCamera(map,location.getLatitude(),location.getLongitude());
                }
                // 本地永久储存
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("latitude",String.valueOf(location.getLatitude()));
                editor.putString("longitude",String.valueOf(location.getLongitude()));
                editor.commit();
            }
        });
        map.showIndoorMap(true);
        // 禁止倾斜手势
        uiSettings.setTiltGesturesEnabled(false);
        // 不显示缩放按钮
        uiSettings.setZoomControlsEnabled(false);
    }

    private void getCIty() {
        GeocodeSearch search=new GeocodeSearch(parentActivity);
        search.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                if(i==1000){
                   RegeocodeAddress address=regeocodeResult.getRegeocodeAddress();
                    viewModel.getCity().setValue(address.getCity());
                    // 可编辑
                    binding.editTextSearch.setEnabled(true);
                    binding.editTextCity.setEnabled(true);
                }else {
                    Toast.makeText(parentActivity, "获取位置失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {}
        });
        LatLonPoint point=new LatLonPoint(UserUtils.USER_LATITUDE,UserUtils.USER_LONGITUDE);
        RegeocodeQuery query=new RegeocodeQuery(point,200,GeocodeSearch.AMAP);
        search.getFromLocationAsyn(query);
    }

    private void searchPOI(String s){
        PoiSearch.Query query=new PoiSearch.Query(s,"",viewModel.getCity().getValue());
        query.setPageSize(100);
        query.setDistanceSort(true);
        PoiSearch poiSearch=new PoiSearch(parentActivity,query);
        poiSearch.setOnPoiSearchListener(new PoiSearchListener(viewModel.getPoiItems()));
        poiSearch.searchPOIAsyn();
    }

    private void searchAroundPOI(Location location) {
        PoiSearch.Query query=new PoiSearch.Query("",LocationUtils.categories,"");
        query.setPageSize(100);
        query.setDistanceSort(true);
        PoiSearch poiSearch=new PoiSearch(parentActivity,query);
        poiSearch.setOnPoiSearchListener(new PoiSearchListener(viewModel.getPoiItems()));
        poiSearch.setBound(new PoiSearch.SearchBound(
                new LatLonPoint(
                        UserUtils.USER_LATITUDE,
                        UserUtils.USER_LONGITUDE),
                3000));
        poiSearch.searchPOIAsyn();
    }

    class PoiSearchListener implements PoiSearch.OnPoiSearchListener{
        MutableLiveData<List<POIItem>> itemsLive;

        public PoiSearchListener(MutableLiveData<List<POIItem>> itemsLive) {
            this.itemsLive = itemsLive;
        }

        @Override
        public void onPoiSearched(PoiResult poiResult, int i) {
            List<POIItem>items=new ArrayList<>();
            if(i==1000){
                List<PoiItem>poiItems=poiResult.getPois();
                for (PoiItem poiItem:poiItems){
                    POIItem item=new POIItem(poiItem);
                    items.add(item);
                }
            }
            itemsLive.setValue(items);
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {}
    }
}