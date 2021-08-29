package team.time.smartcalendar.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.gzuliyujiang.imagepicker.ImagePicker;
import com.github.gzuliyujiang.imagepicker.PickCallback;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.adapters.CommonRecyclerViewAdapter;
import team.time.smartcalendar.databinding.DialogChooseBinding;
import team.time.smartcalendar.databinding.ItemCommonBinding;

import java.util.ArrayList;
import java.util.List;

public class ChooseDialog extends DialogFragment {
    DialogChooseBinding binding;
    Activity parentActivity;
    private RecyclerView.LayoutManager manager;
    private CommonRecyclerViewAdapter adapter;

    List<String> strings=new ArrayList<>();
    List<Integer> icons=new ArrayList<>();
    private NavController controller;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity=requireActivity();

        strings.add("从相册选择");strings.add("拍一张");

        icons.add(R.drawable.ic_baseline_photo_album_24);icons.add(R.drawable.ic_baseline_photo_camera_24);
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parentActivity),
                R.layout.dialog_choose,
                null,
                false);

        controller = Navigation.findNavController(parentActivity,R.id.thirdNavHostFragment);

        manager = new LinearLayoutManager(parentActivity);
        adapter = new CommonRecyclerViewAdapter(parentActivity,strings,icons) {

            @Override
            protected void setItemView(ItemCommonBinding binding, int position) {
                super.setItemView(binding, position);
                binding.imageArrow.setVisibility(View.INVISIBLE);
            }

            @Override
            protected void onItemClick(List<String> strings, List<Integer> icons, int position) {
                switch (position){
                    case 0:
                        goToAlbum();
                        dialog.dismiss();
                        break;
                    case 1:
                        goToCamera();
                        dialog.dismiss();
                        break;
                }
            }
        };
        binding.viewChoose.setLayoutManager(manager);
        binding.viewChoose.setAdapter(adapter);

        builder = new AlertDialog.Builder(getContext());
        builder.setView(binding.getRoot());

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    private void goToAlbum() {
        ImagePicker.getInstance().startGallery(this, true, new PickCallback() {
            @Override
            public void onPermissionDenied(String[] permissions, String message) {
                super.onPermissionDenied(permissions, message);
            }
        });
    }

    private void goToCamera() {
        ImagePicker.getInstance().startCamera(this, true, new PickCallback() {

            @Override
            public void onPermissionDenied(String[] permissions, String message) {
                super.onPermissionDenied(permissions, message);
            }
        });
    }
}