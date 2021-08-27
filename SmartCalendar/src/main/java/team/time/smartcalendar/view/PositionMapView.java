package team.time.smartcalendar.view;

import android.content.Context;
import android.util.AttributeSet;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.amap.api.maps.TextureMapView;

public class PositionMapView extends TextureMapView implements LifecycleObserver {

    public PositionMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void destroy(){
        onDestroy();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void resume(){
        onResume();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void pause(){
        onResume();
    }
}
