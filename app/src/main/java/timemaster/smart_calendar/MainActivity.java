package timemaster.smart_calendar;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private Button btnGeo;
    private Button btnUsage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //123456
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGeo = findViewById(R.id.btn_geo);
        btnUsage = findViewById(R.id.btn_usage);
        btnGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, GeoActivity.class);
                startActivity(intent);
            }
        });
        btnUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, UsageActivity.class);
                startActivity(intent);
            }
        });
    }
}