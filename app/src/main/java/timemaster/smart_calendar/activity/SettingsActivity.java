package timemaster.smart_calendar.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import timemaster.smart_calendar.R;

public class SettingsActivity extends AppCompatActivity {

    boolean logout=false;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnLogout=findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!logout){
                    Toast.makeText(SettingsActivity.this,"再次点击退出登录",Toast.LENGTH_SHORT).show();
                    logout=true;
                }else {
                    SharedPreferences sp=getSharedPreferences("status",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.remove("status");
                    editor.commit();

                    Toast.makeText(SettingsActivity.this,"已退出登录",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(SettingsActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}