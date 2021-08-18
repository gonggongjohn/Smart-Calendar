package team.time.smartcalendar.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.daimajia.swipe.SwipeLayout;
import com.haibin.calendarview.CalendarView;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import team.time.smartcalendar.MainApplication;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.ItemCalendarBinding;
import team.time.smartcalendar.utils.ColorUtils;
import team.time.smartcalendar.utils.DateUtils;

import java.io.IOException;

public class CalendarRecyclerViewAdapter extends RecyclerView.Adapter<CalendarRecyclerViewAdapter.innerHolder> {
    private MainApplication app;
    private Activity parentActivity;
    private CalendarView calendarView;
    private boolean[] isUpdated;

    public CalendarRecyclerViewAdapter(
            Activity parentActivity, CalendarView calendarView, boolean[] isUpdated
    ) {
        this.app=(MainApplication) parentActivity.getApplication();
        this.parentActivity=parentActivity;
        this.calendarView=calendarView;
        this.isUpdated=isUpdated;
    }

    @NonNull
    @Override
    public innerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCalendarBinding binding= DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_calendar,
                parent,
                false
        );

        return new innerHolder(binding);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull innerHolder holder, int position) {
        holder.binding.setItem(app.getCurCalendarItems().get(position));
        holder.binding.setTime(DateUtils.getTimeStamp(calendarView.getSelectedCalendar()));

        ColorDrawable color= (ColorDrawable) holder.binding.itemBackgroundLayout.getBackground();

        holder.binding.itemSwipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                if(color.getColor()== ColorUtils.OrangeRed) {
                    holder.binding.itemBackgroundLayout.setBackgroundColor(ColorUtils.DoDodgerBlue);
                    holder.binding.imageItemDelete.setImageResource(R.drawable.ic_baseline_delete_outline_24);
                }
            }

            @Override
            public void onOpen(SwipeLayout layout) {}

            @Override
            public void onStartClose(SwipeLayout layout) {}

            @Override
            public void onClose(SwipeLayout layout) {}

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {}

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {}
        });

        holder.binding.imageItemDelete.setOnClickListener(v -> {
            if(color.getColor()==ColorUtils.OrangeRed){
                // 得到UUID
                String uuid=app.getCurCalendarItems().get(position).uuid;
                // 通知服务器删除日程
                boolean[] isSuccess=new boolean[1];
                requestDeleteItems(isSuccess,uuid);
                // 本地删除日程
                if(isSuccess[0]){
                    // 从当前列表中删除日程
                    app.getCurCalendarItems().remove(position);
                    // 重新加载数据
                    this.notifyDataSetChanged();
                    // 从总列表中删除日程
                    app.getCalendarItems().remove(DateUtils.findItemById(uuid,app.getCalendarItems()));
                    // 更新日历上的日程标签
                    setMonthSchedule(
                            calendarView.getSelectedCalendar().getYear(),
                            calendarView.getSelectedCalendar().getMonth()
                    );
                    isSuccess[0]=false;
                }
            }else {
                holder.binding.itemBackgroundLayout.setBackgroundColor(ColorUtils.OrangeRed);
                holder.binding.imageItemDelete.setImageResource(R.drawable.ic_baseline_delete_forever_24);
            }
        });

        holder.binding.itemLayout.setOnClickListener(v -> {
            NavController controller= Navigation.findNavController((Activity) v.getContext(),R.id.firstNavHostFragment);
            Bundle bundle=new Bundle();
            bundle.putSerializable("item",app.getCurCalendarItems().get(position));
            controller.navigate(R.id.action_calendarFragment_to_scheduleFragment,bundle);
            isUpdated[0]=true;
        });
    }

    @Override
    public int getItemCount() {
        return app.getCurCalendarItems().size();
    }

    public static class innerHolder extends RecyclerView.ViewHolder {
        private ItemCalendarBinding binding;

        public innerHolder(ItemCalendarBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

    private void setMonthSchedule(int year,int month) {
        new Thread(() -> {
            synchronized (app.getMap()){
                DateUtils.setMonthScheme(app.getMap(),app.getCalendarItems(),year,month);
                calendarView.setSchemeDate(app.getMap());
            }
        }).start();
    }

    private void requestDeleteItems(boolean[] isSuccess,String uuid){
        Thread thread=new Thread(() -> {
            String PATH="/calendar/remove";

            JSONObject object=new JSONObject();
            try {
                object.put("uuid",uuid);

                String body=object.toString();
                RequestBody requestBody=RequestBody.create(
                        body,
                        MediaType.parse("application/json;charset=utf-8")
                );

                Request request=new Request.Builder()
                        .url(app.getString(R.string.URL)+PATH)
                        .addHeader("contentType","application/json;charset=UTF-8")
                        .post(requestBody)
                        .build();

                Call call=app.getClient().newCall(request);

                try {
                    Response response=call.execute();
                    JSONObject result=new JSONObject(response.body().string());
                    Log.d("lmx", "requestDeleteItems: "+result);
                    int status=result.getInt("status");
                    if(status==1){
                        isSuccess[0]=true;
                    }else {
                        parentActivity.runOnUiThread(() -> {
                            Toast.makeText(parentActivity, "未登录，删除失败", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (IOException e) {
                    parentActivity.runOnUiThread(() -> {
                        Toast.makeText(parentActivity, "网络未连接，删除失败", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
