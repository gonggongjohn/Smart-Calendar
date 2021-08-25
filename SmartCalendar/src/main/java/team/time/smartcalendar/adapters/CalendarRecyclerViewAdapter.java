package team.time.smartcalendar.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.daimajia.swipe.SwipeLayout;
import com.haibin.calendarview.CalendarView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Response;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.ItemCalendarBinding;
import team.time.smartcalendar.fragmentsfirst.CalendarFragment;
import team.time.smartcalendar.utils.ColorUtils;
import team.time.smartcalendar.utils.DateUtils;

import java.io.IOException;

public class CalendarRecyclerViewAdapter extends RecyclerView.Adapter<CalendarRecyclerViewAdapter.innerHolder> {
    private Activity parentActivity;
    private CalendarFragment fragment;
    private CalendarView calendarView;
    private boolean[] isUpdated;

    public CalendarRecyclerViewAdapter(
            Fragment fragment, CalendarView calendarView, boolean[] isUpdated
    ) {
        this.parentActivity=fragment.getActivity();
        this.fragment= (CalendarFragment) fragment;
        this.calendarView=calendarView;
        this.isUpdated=isUpdated;
    }

    @NonNull
    @Override
    public innerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCalendarBinding binding= DataBindingUtil.inflate(
                LayoutInflater.from(parentActivity),
                R.layout.item_calendar,
                parent,
                false
        );
        return new innerHolder(binding);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull innerHolder holder, int position) {
        holder.binding.setItem(fragment.curCalendarItems.get(position));
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
                String uuid=fragment.curCalendarItems.get(position).uuid;
                // 通知服务器删除日程
                boolean[] isSuccess=new boolean[1];
                requestDeleteItems(isSuccess,uuid);
                // 判断是否删除成功
                if(isSuccess[0]){
                    // 此处有bug
                }
                // 从当前列表中删除日程
                fragment.curCalendarItems.remove(position);
                // 重新加载数据
                this.notifyDataSetChanged();
                // 从总列表中删除日程
                fragment.calendarItems.remove(DateUtils.findItemById(uuid,fragment.calendarItems));
                // 从数据库删除日程
                new Thread(() -> fragment.dao.deleteCalendarItemByUUID(uuid)).start();
                // 更新日历上的日程标签
                setMonthSchedule(
                        calendarView.getSelectedCalendar().getYear(),
                        calendarView.getSelectedCalendar().getMonth()
                );
            }else {
                holder.binding.itemBackgroundLayout.setBackgroundColor(ColorUtils.OrangeRed);
                holder.binding.imageItemDelete.setImageResource(R.drawable.ic_baseline_delete_forever_24);
            }
        });

        holder.binding.itemLayout.setOnClickListener(v -> {
            NavController controller= Navigation.findNavController((Activity) v.getContext(),R.id.firstNavHostFragment);
            Bundle bundle=new Bundle();
            bundle.putSerializable("item",fragment.curCalendarItems.get(position));
            controller.navigate(R.id.action_calendarFragment_to_scheduleFragment,bundle);
            isUpdated[0]=true;
        });
    }

    @Override
    public int getItemCount() {
        return fragment.curCalendarItems.size();
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
            synchronized (fragment.map){
                DateUtils.setMonthScheme(fragment.map,fragment.calendarItems,year,month);
                calendarView.setSchemeDate(fragment.map);
            }
        }).start();
    }

    private void requestDeleteItems(boolean[] isSuccess,String uuid){
        Thread thread=new Thread(() -> {
            JSONObject body=new JSONObject();
            try {
                body.put("uuid",uuid);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody requestBody=RequestBody.create(
                    body.toString(),
                    MediaType.parse("application/json;charset=utf-8")
            );

            try {
                Response<ResponseBody> response=fragment.apiService.remove(requestBody).execute();
                try {
                    JSONObject result=new JSONObject(response.body().string());
                    Log.d("lmx", "requestDeleteItems: "+result);
                    int status=result.getInt("status");
                    if(status==1){
                        isSuccess[0]=true;
                    }
                }catch (JSONException e){
                    Log.d("lmx", "requestDeleteItems: "+e);
                }

            } catch (IOException e) {
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
