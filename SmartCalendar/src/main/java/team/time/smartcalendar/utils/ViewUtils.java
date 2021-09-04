package team.time.smartcalendar.utils;

import android.widget.ArrayAdapter;
import androidx.appcompat.widget.AppCompatSpinner;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ViewUtils {
    public static void setSpinnerAdapter(@NotNull AppCompatSpinner spinner, List<String>list){
        ArrayAdapter<String> adapter=new ArrayAdapter<>(
                spinner.getContext(),
                android.R.layout.simple_spinner_item,
                list
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }
}
