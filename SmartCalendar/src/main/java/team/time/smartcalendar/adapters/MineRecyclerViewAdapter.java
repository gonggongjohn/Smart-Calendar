package team.time.smartcalendar.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.ItemMineBinding;
import team.time.smartcalendar.fragmentsthird.MineFragment;

import java.util.List;

public class MineRecyclerViewAdapter extends RecyclerView.Adapter<MineRecyclerViewAdapter.innerHolder> {
    Activity parentActivity;
    MineFragment fragment;

    List<String>strings;
    List<Integer>icons;

    public MineRecyclerViewAdapter(MineFragment fragment,List<String> strings, List<Integer> icons) {
        this.parentActivity=fragment.requireActivity();
        this.fragment=fragment;
        this.strings = strings;
        this.icons = icons;
    }

    @NonNull
    @NotNull
    @Override
    public innerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemMineBinding binding= DataBindingUtil.inflate(
                LayoutInflater.from(parentActivity),
                R.layout.item_mine,
                parent,
                false
        );
        return new innerHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull innerHolder holder, int position) {
        holder.setData(strings.get(position),icons.get(position));
        holder.binding.viewItem.setOnClickListener(v -> {
            go(position);
        });
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public class innerHolder extends RecyclerView.ViewHolder {
        private ItemMineBinding binding;

        public innerHolder(@NotNull ItemMineBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public void setData(String string, int icon) {
            binding.textItemName.setText(string);
            binding.imageIcon.setImageResource(icon);
        }
    }

    private void go(int position) {
        switch (position){
            case 0:
                fragment.controller.navigate(R.id.action_mineFragment_to_accountFragment);
                break;
        }
    }
}
