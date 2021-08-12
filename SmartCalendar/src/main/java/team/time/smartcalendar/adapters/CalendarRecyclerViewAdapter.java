package team.time.smartcalendar.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import team.time.smartcalendar.R;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.databinding.ItemCalendarBinding;

import java.util.List;

public class CalendarRecyclerViewAdapter extends RecyclerView.Adapter<CalendarRecyclerViewAdapter.innerHolder> {
    private List<CalendarItem>items;

    public CalendarRecyclerViewAdapter(List<CalendarItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public innerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCalendarBinding binding= DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_calendar,
                parent,
                false
        );
        return new innerHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull innerHolder holder, int position) {
        holder.binding.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class innerHolder extends RecyclerView.ViewHolder {
        private ItemCalendarBinding binding;

        public innerHolder(ItemCalendarBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
