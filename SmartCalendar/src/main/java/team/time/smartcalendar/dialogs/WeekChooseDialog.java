package team.time.smartcalendar.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.DialogWeekChooseBinding;
import team.time.smartcalendar.viewmodels.RepeatScheduleViewModel;

import java.util.ArrayList;
import java.util.List;


public class WeekChooseDialog extends DialogFragment {
    Activity parentActivity;
    DialogWeekChooseBinding binding;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private NavController controller;
    private RepeatScheduleViewModel viewModel;
    private List<CheckBox> checkBoxes;
    private Boolean[] booleans;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity=requireActivity();

        Bundle bundle=getArguments();
        if(bundle!=null){
            viewModel = (RepeatScheduleViewModel) bundle.getSerializable("viewModel");
            booleans=viewModel.getRepeat().getValue();
        }
        checkBoxes=new ArrayList<>();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.dialog_week_choose,
                null,
                false);

        setCheckBoxes();
        setCheckBoxesState();

        controller=Navigation.findNavController(parentActivity,R.id.firstNavHostFragment);

        binding.btnCancel.setOnClickListener(v -> {
            controller.popBackStack();
        });

        binding.btnFinish.setOnClickListener(v -> {
            getCheckBoxesState();
            viewModel.getRepeat().setValue(booleans);
            controller.popBackStack();
        });

        builder = new AlertDialog.Builder(getContext());
        builder.setView(binding.getRoot());

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    private void setCheckBoxes() {
        checkBoxes.add(binding.checkBox1);
        checkBoxes.add(binding.checkBox2);
        checkBoxes.add(binding.checkBox3);
        checkBoxes.add(binding.checkBox4);
        checkBoxes.add(binding.checkBox5);
        checkBoxes.add(binding.checkBox6);
        checkBoxes.add(binding.checkBox7);
    }

    private void setCheckBoxesState() {
        for(int i=0;i<7;i++){
            checkBoxes.get(i).setChecked(booleans[i]);
        }
    }

    private void getCheckBoxesState() {
        for(int i=0;i<7;i++){
            booleans[i]=checkBoxes.get(i).isChecked();
        }
    }
}