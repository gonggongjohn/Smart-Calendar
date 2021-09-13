package team.time.smartcalendar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.dataBeans.MeqItem;
import team.time.smartcalendar.databinding.ItemMeqBinding;

import java.util.List;

public class MeqRecyclerViewAdapter extends RecyclerView.Adapter<MeqRecyclerViewAdapter.innerHolder> {
    private List<MeqItem>items;

    public MeqRecyclerViewAdapter(List<MeqItem> items) {
        this.items = items;
    }

    @NonNull
    @NotNull
    @Override
    public innerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
         ItemMeqBinding binding=DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_meq,
                parent,
                false);

        return new innerHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull innerHolder holder, int position) {
        MeqItem item=items.get(position);
        if(item.length>4){
            holder.binding.radio5.setVisibility(View.VISIBLE);
        }else {
            holder.binding.radio5.setVisibility(View.GONE);
        }
        changeView(holder.binding,items,position);
        holder.binding.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class innerHolder extends RecyclerView.ViewHolder {
        ItemMeqBinding binding;

        public innerHolder(ItemMeqBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

    protected void changeView(ItemMeqBinding binding, List<MeqItem> is, int position) { }
}
