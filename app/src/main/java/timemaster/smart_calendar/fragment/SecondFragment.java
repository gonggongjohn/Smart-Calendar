package timemaster.smart_calendar.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.amap.api.maps.*;
import com.amap.api.maps.model.*;
import timemaster.smart_calendar.R;
import timemaster.smart_calendar.sqlite.MyLocationHelper;

import java.util.Objects;

public class SecondFragment extends Fragment
        implements AMap.OnMyLocationChangeListener,
        AMap.OnMapTouchListener {
    private MapView mapView;
    private AMap aMap;
    private Button btn_now;
    private MyLocationStyle myLocationStyle;
    private Location myLocation;
    private boolean isFirstLoc=true;

    private static final int FILL_COLOR = Color.argb(20, 0, 0, 180);

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
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btn_now=Objects.requireNonNull(getActivity()).findViewById(R.id.btn_now);
        btn_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aMap!=null && myLocation!=null){
                    aMap.animateCamera(CameraUpdateFactory.
                            newCameraPosition(new CameraPosition(new LatLng(
                                    myLocation.getLatitude(), myLocation.getLongitude()
                            ), 17, 30, 0)));
                    aMap.setMyLocationStyle(myLocationStyle);
                }
                Log.e("OnClick", "OK");
            }
        });
        mapView=Objects.requireNonNull(getActivity()).findViewById(R.id.amap);
        mapView.onCreate(savedInstanceState);
        myLocationStyle=new MyLocationStyle();

        init();
    }

    private void init(){
        if(aMap==null){
            aMap=mapView.getMap();
            MyLocationHelper helper=new MyLocationHelper(getActivity(),"data.db",null,1);
            SQLiteDatabase db=helper.getWritableDatabase();
            final Cursor cursor=db.rawQuery("select * from locations",null);

            int i=0;
            for(cursor.moveToLast();!cursor.isBeforeFirst() && i<100;cursor.moveToPrevious(),i++){
                LatLng latLng=new LatLng(cursor.getDouble(cursor.getColumnIndex("latitude"))
                        ,cursor.getDouble(cursor.getColumnIndex("longitude")));
                MarkerOptions markerOptions=new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_circle))
                        .position(latLng)
                        .setFlat(true)
                        .draggable(false);
                aMap.addMarker(markerOptions);
            }
            cursor.close();

            setUpMap();
        }
    }

    private void setUpMap(){
        aMap.setMyLocationEnabled(true);
        aMap.setOnMyLocationChangeListener(this);
        aMap.setOnMapTouchListener(this);
        aMap.showIndoorMap(true);

        UiSettings uiSettings=aMap.getUiSettings();
        uiSettings.setScaleControlsEnabled(true);

        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.circle));
        myLocationStyle.strokeWidth(0);
        myLocationStyle.radiusFillColor(FILL_COLOR);
        aMap.setMyLocationStyle(myLocationStyle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMyLocationChange(Location location) {
        myLocation=location;
        if(isFirstLoc) {
            aMap.moveCamera(CameraUpdateFactory.
                    newCameraPosition(new CameraPosition(new LatLng(
                            myLocation.getLatitude(), myLocation.getLongitude()),
                            17, 30, 0)));
            btn_now.setText("我的位置: (" + location.getLatitude() + ", " + location.getLongitude() + ")");
            isFirstLoc=false;
        }
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if(myLocationStyle!=null && myLocationStyle.
                getMyLocationType()==MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE){
            myLocationStyle.myLocationType(MyLocationStyle.
                    LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
            aMap.setMyLocationStyle(myLocationStyle);
        }
    }
}