package timemaster.smart_calendar.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import timemaster.smart_calendar.R;
import timemaster.smart_calendar.activity.SettingsActivity;

import java.util.Objects;

public class ThirdFragment extends Fragment {

    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    Button btn6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(container!=null) {
            container.removeAllViewsInLayout();
        }
        return inflater.inflate(R.layout.fragment_third, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btn1= Objects.requireNonNull(getActivity()).findViewById(R.id.btn_my_1);
        btn2= Objects.requireNonNull(getActivity()).findViewById(R.id.btn_my_2);
        btn3= Objects.requireNonNull(getActivity()).findViewById(R.id.btn_my_3);
        btn4= Objects.requireNonNull(getActivity()).findViewById(R.id.btn_my_4);
        btn5= Objects.requireNonNull(getActivity()).findViewById(R.id.btn_my_5);
        btn6= Objects.requireNonNull(getActivity()).findViewById(R.id.btn_my_6);

        btn6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}