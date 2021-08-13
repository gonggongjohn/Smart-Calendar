package team.time.smartcalendar.fragmentsthird;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import team.time.smartcalendar.R;


public class MainThirdFragment extends Fragment {
    private String [] data = {"我的账户","系统设置","关于"};
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_main_third, container, false);
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String> (
                getContext(),android.R.layout.simple_list_item_1,data);
        listView = view.findViewById(R.id.lv);
        listView.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Toast.makeText(getContext(), "我的账户", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getContext(), "系统设置", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getContext(), "gonggongjohn@163.com", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}