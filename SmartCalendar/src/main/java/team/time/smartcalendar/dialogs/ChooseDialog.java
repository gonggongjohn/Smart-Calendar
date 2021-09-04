package team.time.smartcalendar.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.gzuliyujiang.imagepicker.ActivityBuilder;
import com.github.gzuliyujiang.imagepicker.CropImageView;
import com.github.gzuliyujiang.imagepicker.ImagePicker;
import com.github.gzuliyujiang.imagepicker.PickCallback;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.adapters.CommonRecyclerViewAdapter;
import team.time.smartcalendar.databinding.DialogChooseBinding;
import team.time.smartcalendar.databinding.ItemCommonBinding;
import team.time.smartcalendar.fragmentsthird.MineFragment;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

public class ChooseDialog extends DialogFragment {
    DialogChooseBinding binding;
    Activity parentActivity;
    private RecyclerView.LayoutManager manager;
    private CommonRecyclerViewAdapter adapter;

    List<String> strings=new ArrayList<>();
    List<Integer> icons=new ArrayList<>();
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private MineFragment parentFragment;
    private int ratioX;
    private int ratioY;
    private String name;

    private int width;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity=requireActivity();

        strings.add("从相册选择");strings.add("拍一张");

        icons.add(R.drawable.ic_baseline_photo_album_24);icons.add(R.drawable.ic_baseline_photo_camera_24);

        Bundle bundle=getArguments();
        if (bundle != null) {
            parentFragment = (MineFragment) bundle.getSerializable("fragment");
            ratioX = bundle.getInt("ratioX");
            ratioY = bundle.getInt("ratioY");
            name = bundle.getString("name");
        }

        width=Math.max(SystemUtils.WINDOW_WIDTH,600);
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
                        goAlbum();
                        dialog.dismiss();
                        break;
                    case 1:
                        goCamera();
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

    private void goAlbum() {
        ImagePicker.getInstance().startGallery(parentFragment, true, cropCallback);
    }

    private void goCamera() {
        ImagePicker.getInstance().startCamera(parentFragment,true,cropCallback);
    }

    PickCallback cropCallback = new PickCallback() {
        @Override
        public void onPermissionDenied(String[] permissions, String message) {
            Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void cropConfig(ActivityBuilder builder) {
            builder.setMultiTouchEnabled(false)
                    .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setRequestedSize(width, (int)(width*(ratioY/(double)ratioX)))
                    .setOutputCompressQuality(100)
                    .setFixAspectRatio(true)
                    .setAspectRatio(ratioX, ratioY);
            if(name.equals("head")){
                builder.setOutputUri(Uri.parse("file://"+parentActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+UserUtils.USERNAME+"head.jpg"));
            }else if(name.equals("background")){
                builder.setOutputUri(Uri.parse("file://"+parentActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+UserUtils.USERNAME+"background.jpg"));
            }
        }

        @Override
        public void onCropImage(@Nullable Uri imageUri) {
            Log.d("lmx", "onCropImage: "+ imageUri);
            if(name.equals("head")){
                parentFragment.viewModel.getHead().setValue(parentActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+UserUtils.USERNAME+"head.jpg");
            }else if(name.equals("background")){
                parentFragment.viewModel.getBackground().setValue(parentActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+UserUtils.USERNAME+"background.jpg");
            }
        }
    };
}