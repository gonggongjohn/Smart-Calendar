package team.time.smartcalendar.fragmentssecond;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.HeatmapTileProvider;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.TileOverlayOptions;
import dagger.hilt.android.AndroidEntryPoint;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.databinding.FragmentHeatMapBinding;
import team.time.smartcalendar.service.MyLocationService;
import team.time.smartcalendar.utils.DateUtils;
import team.time.smartcalendar.utils.LocationUtils;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.utils.UserUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AndroidEntryPoint
public class HeatMapFragment extends Fragment {
    private Activity parentActivity;
    private FragmentHeatMapBinding binding;
    private NavController controller;
    private AMap map;
    private UiSettings uiSettings;
    private List<LatLng>latLngs;

    @Named("all")
    @Inject
    public List<CalendarItem> calendarItems;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity=requireActivity();

        SystemUtils.checkLocatePermission(parentActivity);

        // 启动定位服务
        Intent intent=new Intent(parentActivity, MyLocationService.class);
        parentActivity.startService(intent);

        latLngs=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 重写回退键
        SystemUtils.setBack(this);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_heat_map,container,false);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"热力图",R.drawable.ic_baseline_arrow_back_ios_new_24,-1);

        setMapView(savedInstanceState);
        setMap();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        binding.action.imageLeft.setOnClickListener(v -> {
            controller.popBackStack();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getLatlngs();
        move();
        setHeatMap();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

        binding.mapPosition.onSaveInstanceState(outState);
    }

    private void getLatlngs() {
        latLngs.clear();
        long now=new Date().getTime();
        long oneMonthAgo=DateUtils.getDayDate(new Date()).getTime() - 30 * DateUtils.A_DAY_MILLISECOND;
        for(CalendarItem item:calendarItems){
            if(!item.position.equals("") && item.startTime<=now && item.startTime>=oneMonthAgo){
                latLngs.add(new LatLng(item.latitude,item.longitude));
            }
        }
        Log.d("lmx", "getLatlngs: "+latLngs);
    }

    private void setMapView(Bundle savedInstanceState) {
        binding.mapPosition.onCreate(savedInstanceState);
        getLifecycle().addObserver(binding.mapPosition);
    }

    private void setMap() {
        map=binding.mapPosition.getMap();
        uiSettings=map.getUiSettings();
        // 夜间模式
        map.setMapType(AMap.MAP_TYPE_NIGHT);
        // 缩放限制
        map.setMaxZoomLevel(16);
        // 禁止倾斜手势
        uiSettings.setTiltGesturesEnabled(false);
    }

    private void move() {
        // 移动地图
        if(latLngs.isEmpty()){
            LocationUtils.moveCamera(map, UserUtils.USER_LATITUDE,UserUtils.USER_LONGITUDE);
        }else {
            int last= latLngs.size()-1;
            LocationUtils.moveCamera(map, latLngs.get(last).latitude,latLngs.get(last).longitude);
        }
    }

    private void setHeatMap() {
        map.clear();
        // 构建热力图 HeatmapTileProvider
        HeatmapTileProvider.Builder builder = new HeatmapTileProvider.Builder();
        builder.data(latLngs)
                .radius(25);
        // 构造热力图对象
        HeatmapTileProvider heatmapTileProvider = builder.build();

        // 初始化 TileOverlayOptions
        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
        tileOverlayOptions.tileProvider(heatmapTileProvider); // 设置瓦片图层的提供者
        // 向地图上添加 TileOverlayOptions 类对象
        map.addTileOverlay(tileOverlayOptions);
    }
}