package team.time.smartcalendar.utils;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class GlideUtils {
    public static void loadLocalImage(String s, ImageView view,int resId){
        Glide.with(view.getContext())
                .load(s)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .error(resId)
                .placeholder(android.R.color.white)
                .fallback(resId)
                .into(view);
    }
}
