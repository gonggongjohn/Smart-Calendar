package team.time.smartcalendar.adapters;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.daimajia.swipe.SwipeLayout;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.databinding.ItemTimelineBinding;
import team.time.smartcalendar.utils.ColorUtils;

import java.util.List;

public abstract class TimelineRecyclerViewAdapter extends RecyclerView.Adapter<TimelineRecyclerViewAdapter.innerHolder> {
    List<CalendarItem>items;

    public TimelineRecyclerViewAdapter(List<CalendarItem> items) {
        this.items = items;
    }

    @NonNull
    @NotNull
    @Override
    public innerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemTimelineBinding binding= DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_timeline,
                parent,
                false
        );
        return new innerHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull innerHolder holder, int position) {
        holder.binding.setItem(items.get(position));

        ColorDrawable color= (ColorDrawable) holder.binding.itemBackgroundLayout1.getBackground();

        holder.binding.itemSwipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                if(color.getColor()== ColorUtils.OrangeRed) {
                    holder.binding.itemBackgroundLayout1.setBackgroundColor(ColorUtils.DoDodgerBlue);
                    holder.binding.imageItemDelete.setImageResource(R.drawable.ic_baseline_delete_outline_24);
                }
            }

            @Override
            public void onOpen(SwipeLayout layout) {}

            @Override
            public void onStartClose(SwipeLayout layout) {}

            @Override
            public void onClose(SwipeLayout layout) {}

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {}

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {}
        });

        holder.binding.imageItemDelete.setOnClickListener(v -> {
            if(color.getColor()==ColorUtils.OrangeRed){
                onDeleteClick(holder.binding,items,position);
            }else {
                holder.binding.itemBackgroundLayout1.setBackgroundColor(ColorUtils.OrangeRed);
                holder.binding.imageItemDelete.setImageResource(R.drawable.ic_baseline_delete_forever_24);
            }
        });

        holder.binding.imageItemAdd.setOnClickListener(v -> {
            onAddClick(holder.binding,items,position);
        });

    holder.binding.imageItemUpdate.setOnClickListener(v -> {
            onUpdateClick(holder.binding,items,position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class innerHolder extends RecyclerView.ViewHolder {
        private ItemTimelineBinding binding;

        public innerHolder(ItemTimelineBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

    protected abstract void onDeleteClick(ItemTimelineBinding binding, List<CalendarItem> items, int position);

    protected abstract void onAddClick(ItemTimelineBinding binding, List<CalendarItem> items, int position);

    protected abstract void onUpdateClick(ItemTimelineBinding binding, List<CalendarItem> items, int position);
}
