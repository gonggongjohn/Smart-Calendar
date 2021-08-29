package team.time.smartcalendar.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.ItemCommonBinding;

import java.util.List;

public  abstract class CommonRecyclerViewAdapter extends RecyclerView.Adapter<CommonRecyclerViewAdapter.innerHolder> {
    Activity parentActivity;

    List<String>strings;
    List<Integer>icons;

    public CommonRecyclerViewAdapter(Activity parentActivity,List<String> strings, List<Integer> icons) {
        this.parentActivity= parentActivity;
        this.strings = strings;
        this.icons = icons;
    }

    @NonNull
    @NotNull
    @Override
    public innerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemCommonBinding binding= DataBindingUtil.inflate(
                LayoutInflater.from(parentActivity),
                R.layout.item_common,
                parent,
                false
        );
        return new innerHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull innerHolder holder, int position) {
        holder.setData(strings.get(position),icons.get(position));
        setItemView(holder.binding,position);
        holder.binding.viewItem.setOnClickListener(v -> {
            onItemClick(strings,icons,position);
        });
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public class innerHolder extends RecyclerView.ViewHolder {
        private ItemCommonBinding binding;

        public innerHolder(@NotNull ItemCommonBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public void setData(String string, int icon) {
            binding.textItemName.setText(string);
            binding.imageIcon.setImageResource(icon);
        }
    }

    protected abstract void onItemClick(List<String> strings,List<Integer>icons,int position);

    protected void setItemView(ItemCommonBinding binding, int position) {
    }
}
