package team.time.smartcalendar.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.dataBeans.POIItem;
import team.time.smartcalendar.databinding.ItemPoiBinding;
import team.time.smartcalendar.fragmentsfirst.PositionFragment;
import team.time.smartcalendar.utils.LocationUtils;

import java.util.List;

public class POIRecyclerViewAdapter extends RecyclerView.Adapter<POIRecyclerViewAdapter.innerHolder> {
    Activity parentActivity;
    PositionFragment fragment;
    RecyclerView mainView;
    List<POIItem>items;

    private int checkPosition=-1;
    private Marker marker;
    private boolean[] canFinish;
    private String[] positionName;
    private LatLonPoint point;

    public POIRecyclerViewAdapter(@NotNull PositionFragment fragment, RecyclerView mainView, List<POIItem> items) {
        this.parentActivity=fragment.requireActivity();
        this.fragment=fragment;
        this.mainView=mainView;
        this.items = items;
        this.canFinish=fragment.canFinish;
        this.positionName=fragment.positionName;
        this.point=fragment.point;
    }

    @NonNull
    @NotNull
    @Override
    public innerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemPoiBinding binding= DataBindingUtil.inflate(
                LayoutInflater.from(parentActivity),
                R.layout.item_poi,
                parent,
                false
        );
        return new innerHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull innerHolder holder, int position) {
        holder.binding.setItem(items.get(position));

        if(checkPosition==position){
            holder.binding.imageIcon.setImageResource(R.drawable.ic_baseline_check_24_spring_green);
        }else {
            holder.binding.imageIcon.setImageResource(R.drawable.ic_baseline_location_on_24);
        }

        holder.binding.layoutItem.setOnClickListener(v -> {
            if(checkPosition!=position){
                changeIcons();
                holder.binding.imageIcon.setImageResource(R.drawable.ic_baseline_check_24_spring_green);
                checkPosition=position;
                moveAndMark(items.get(position));

                positionName[0]=items.get(position).name;
                point.setLatitude(items.get(position).latitude);
                point.setLongitude(items.get(position).longitude);

                canFinish[0]=true;
            }else {
                if(marker!=null){
                    marker.destroy();
                }
                holder.binding.imageIcon.setImageResource(R.drawable.ic_baseline_location_on_24);
                checkPosition=-1;

                canFinish[0]=false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class innerHolder extends RecyclerView.ViewHolder {
        private ItemPoiBinding binding;

        public innerHolder(ItemPoiBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

    public void setItems(List<POIItem> items) {
        checkPosition=-1;
        this.items = items;
    }

    private void changeIcons() {
        for(int i=0;i<mainView.getChildCount();i++){
           ImageView image=mainView.getChildAt(i).findViewById(R.id.imageIcon);
           image.setImageResource(R.drawable.ic_baseline_location_on_24);
        }
    }

    private void moveAndMark(@NotNull POIItem item) {
        if(marker!=null){
            marker.destroy();
        }
        marker = LocationUtils.addMarker(fragment.map,item.latitude,item.longitude);
        LocationUtils.animateCamera(fragment.map,item.latitude,item.longitude);
    }
}
