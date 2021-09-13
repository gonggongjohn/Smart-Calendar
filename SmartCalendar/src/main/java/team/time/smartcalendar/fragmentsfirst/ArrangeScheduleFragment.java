package team.time.smartcalendar.fragmentsfirst;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.adapters.TimelineRecyclerViewAdapter;
import team.time.smartcalendar.dao.CalendarItemDao;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.databinding.FragmentArrangeScheduleBinding;
import team.time.smartcalendar.databinding.ItemTimelineBinding;
import team.time.smartcalendar.requests.ApiService;
import team.time.smartcalendar.utils.DateUtils;
import team.time.smartcalendar.utils.RequestUtils;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.utils.ViewUtils;
import team.time.smartcalendar.viewmodels.ArrangeViewModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AndroidEntryPoint
public class ArrangeScheduleFragment extends Fragment {

    private FragmentArrangeScheduleBinding binding;
    private ArrangeViewModel viewModel;
    private NavController controller;
    private TimelineRecyclerViewAdapter adapter;
    private Activity parentActivity;
    private RecyclerView.LayoutManager manager;
    private Bundle bundle;
    private CalendarItem item;
    private long listId;

    private List<CalendarItem>items;

    // 更新数据需要的数据结构
    private List<CalendarItem>itemsOrigin=new ArrayList<>();
    private List<CalendarItem>addItems=new ArrayList<>();
    private List<CalendarItem>updateItems=new ArrayList<>();
    private List<CalendarItem>deleteItems=new ArrayList<>();

    private boolean isFirst;
    private boolean isNotCreate;

    @Inject
    ApiService apiService;
    @Named("all")
    @Inject
    List<CalendarItem> calendarItems;
    @Inject
    CalendarItemDao dao;
    @Named("category")
    @Inject
    List<String> categories;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = requireActivity();
        isFirst=true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 重写回退键
        SystemUtils.setBack(this);

        viewModel = new ViewModelProvider(this).get(ArrangeViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_arrange_schedule,container,false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"日程列表",R.drawable.ic_baseline_close_24,R.drawable.ic_baseline_check_24);

        if(isFirst){
            isFirst=false;

            // 工作线程发起网络请求，同步方法
            Log.d("lmx", "categories.size: "+categories.size());
            if(categories.isEmpty() || categories.size()==1){
                categories.clear();
                requestCategories();
            }
            ViewUtils.setSpinnerAdapter(binding.spinnerCategory,categories);

            // 接收参数
            bundle = getArguments();
            if(bundle!=null){
                listId = bundle.getLong("listId");
                item = (CalendarItem) bundle.getSerializable("item");
                items = (ArrayList<CalendarItem>) bundle.getSerializable("items");

                // 修改日程
                if(items==null){
                    isNotCreate=false;
                    items=new ArrayList<>();
                    getItemList();
                }else { // 创建日程
                    isNotCreate=true;
                }
                setViewModel();
                setSpinnerSelection();
            }
        }else {
            ViewUtils.setSpinnerAdapter(binding.spinnerCategory,categories);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        manager = new LinearLayoutManager(parentActivity);
        adapter = new TimelineRecyclerViewAdapter(items) {
            @Override
            protected void onDeleteClick(ItemTimelineBinding binding, List<CalendarItem> items, int position) {
                fakeDeleteItem(binding,items,position);
            }

            @Override
            protected void onAddClick(ItemTimelineBinding binding, List<CalendarItem> items, int position) {
                fakeAddItem(binding,items,position);
            }

            @Override
            protected void onUpdateClick(ItemTimelineBinding binding, List<CalendarItem> items, int position) {
                fakeUpdateItem(binding,items,position);
            }
        };
        binding.viewTimeline.setLayoutManager(manager);
        binding.viewTimeline.setAdapter(adapter);

        binding.action.imageLeft.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(parentActivity,getEditTextList());
            controller.popBackStack();
        });

        binding.action.imageRight.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(parentActivity,getEditTextList());
            if(isNotCreate){
                changeBeforeCreate();
                create();
            }else {
                changeBeforeUpdate();
                update();
            }
            controller.popBackStack(R.id.calendarFragment,false);
        });
    }

    private void fakeUpdateItem(ItemTimelineBinding binding, List<CalendarItem> is, int position) {
        CalendarItem i=is.get(position);
        Bundle bundle=new Bundle();
        bundle.putSerializable("items",(ArrayList<CalendarItem>)items);
        bundle.putBoolean("isCreate",false);
        // 更新
        if(!isNotCreate){
            // 如果不在添加列表里，则更新
            if(!addItems.contains(i)){
                updateItems.add(i);
            }
        }
        // 跳转
        bundle.putSerializable("item",i);
        controller.navigate(R.id.action_arrangeScheduleFragment_to_timeFragment,bundle);
    }

    private void fakeAddItem(ItemTimelineBinding binding, List<CalendarItem> is, int position) {
        CalendarItem i=new CalendarItem(is.get(position));
        i.id=0;
        i.uuid= UUID.randomUUID().toString().toUpperCase();
        Bundle bundle=new Bundle();
        bundle.putSerializable("items",(ArrayList<CalendarItem>)items);
        bundle.putBoolean("isCreate",true);
        if (!isNotCreate){
           addItems.add(i);
        }
        // 跳转
        bundle.putSerializable("item",i);
        controller.navigate(R.id.action_arrangeScheduleFragment_to_timeFragment,bundle);
    }

    private void fakeDeleteItem(ItemTimelineBinding binding, List<CalendarItem> is, int position) {
        CalendarItem i=is.get(position);
        if(!isNotCreate){
            // 在添加列表里，移除
            if(addItems.contains(i)){
                addItems.remove(i);
                // 在更新列表里，移除并删除
            }else if(updateItems.contains(i)){
                updateItems.remove(i);
                deleteItems.add(i);
                // 都不在，直接删除
            } else {
                deleteItems.add(i);
            }
        }
        // 从当前列表中删除日程
        is.remove(i);
        // 重新加载数据
        adapter.notifyDataSetChanged();
    }

    private void changeBeforeCreate() {
        String info=viewModel.getInfo().getValue();
        int categoryId=getCategoryId(binding.spinnerCategory.getSelectedItemPosition());
        String categoryName=binding.spinnerCategory.getSelectedItem().toString();

        if(!items.isEmpty() && !info.equals(items.get(0).info)){
            for(CalendarItem i:items){
                i.info=info;
            }
        }

        if(!items.isEmpty() && !categoryName.equals(items.get(0).categoryName)){
            for(CalendarItem i:items){
                i.categoryId=categoryId;
                i.categoryName=categoryName;
            }
        }
    }

    private void changeBeforeUpdate() {
        String info=viewModel.getInfo().getValue();
        int categoryId=getCategoryId(binding.spinnerCategory.getSelectedItemPosition());
        String categoryName=binding.spinnerCategory.getSelectedItem().toString();

        if(!items.isEmpty() && !info.equals(items.get(0).info)){
            for(CalendarItem i:items){
                i.info=info;

                if(!addItems.contains(i) && !updateItems.contains(i)){
                    updateItems.add(i);
                }
            }
        }

        if(!items.isEmpty() && !categoryName.equals(items.get(0).categoryName)){
            for(CalendarItem i:items){
                i.categoryId=categoryId;
                i.categoryName=categoryName;

                if(!addItems.contains(i) && !updateItems.contains(i)){
                    updateItems.add(i);
                }
            }
        }
    }

    private void create() {
        for(CalendarItem i:items){
            // 通知服务器添加日程
            boolean[] isSuccess=new boolean[1];
            requestAddItems(isSuccess,i);
            // 判断dirty值
            if(isSuccess[0]){
                i.dirty=0;
            }else {
                i.dirty=1;
            }
            // 本地创建日程
            calendarItems.add(i);
        }
        // 添加到数据库
        addLocalItems(items);
    }

    private void update() {
        /* 增 */
        for(CalendarItem i:addItems){
            // 通知服务器添加日程
            boolean[] isSuccess=new boolean[1];
            requestAddItems(isSuccess,i);
            // 判断dirty值
            if(isSuccess[0]){
                i.dirty=0;
            }else {
                i.dirty=1;
            }
            // 本地创建日程
            calendarItems.add(i);
        }
        // 添加到数据库
        addLocalItems(addItems);

        /* 删 */
        for(CalendarItem i:deleteItems){
            // 通知服务器删除日程
            boolean[] isSuccess=new boolean[1];
            requestDeleteItems(isSuccess,i.uuid);
            // 判断是否删除成功
            if(isSuccess[0]){
                // 此处有bug
            }
            // 从总列表中删除日程
            calendarItems.remove(DateUtils.findItemById(i.uuid,calendarItems));
        }
        // 从数据库删除日程
        deleteLocalItems(deleteItems);

        /* 改 */
        for(CalendarItem i:updateItems){
            // 通知服务器修改日程
            boolean[] isSuccess=new boolean[1];
            requestUpdateItems(isSuccess,i);
            // 判断dirty值
            if(isSuccess[0]){
                i.dirty=0;
            }else {
                i.dirty=2;
            }
            // 本地修改日程
            DateUtils.findItemById(i.uuid,itemsOrigin).copy(i);
        }
        // 更新到数据库
        updateLocalItems(updateItems);
    }

    private void requestAddItems(boolean[] isSuccess,CalendarItem i) {
        Thread thread=new Thread(() -> {
            RequestUtils.requestAddItems(apiService,isSuccess,i);
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addLocalItems(List<CalendarItem>is) {
        Thread thread=new Thread(() -> {
            for(CalendarItem i:is){
                dao.insertCalendarItem(i);
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void requestDeleteItems(boolean[] isSuccess, String uuid) {
        Thread thread=new Thread(() -> {
            RequestUtils.requestDeleteItems(apiService, isSuccess,uuid);
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void deleteLocalItems(List<CalendarItem> is) {
        Thread thread=new Thread(() -> {
            for(CalendarItem i:is){
                dao.deleteCalendarItemByUUID(i.uuid);
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void requestUpdateItems(boolean[] isSuccess,CalendarItem i) {
        Thread thread=new Thread(() -> {
            RequestUtils.requestUpdateItems(apiService,isSuccess,i);
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateLocalItems(List<CalendarItem>is) {
        Thread thread=new Thread(() -> {
            for(CalendarItem i:is){
                if(i.id==0){
                    i.id= dao.getIdByUUID(i.uuid);
                }
                dao.updateCalendarItem(i);
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<View> getEditTextList() {
        List<View> viewList=new ArrayList<>();
        viewList.add(binding.editTextTittle);
        return viewList;
    }

    private void requestCategories() {
        Thread thread=new Thread(() -> {
            RequestUtils.requestCategories(apiService,categories);
            Log.d("lmx", "requestCategories: ");
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setViewModel() {
        viewModel.getInfo().setValue(item.info);
    }

    private void setSpinnerSelection() {
        int index=categories.indexOf(item.categoryName);
        if(index<0){
            binding.spinnerCategory.setSelection(categories.size()-1);
        }else {
            binding.spinnerCategory.setSelection(index);
        }
    }

    // 计算服务器提供的category的ID
    private int getCategoryId(int index){
        if(categories.size()==1){
            return 1;
        }
        return (index+2) % categories.size();
    }

    private void getItemList() {
        for(CalendarItem i:calendarItems){
            if(i.type==2 && i.listId==item.listId){
                itemsOrigin.add(i);
                // 副本
                items.add(new CalendarItem(i));
            }
        }
        DateUtils.sortItemList(items);
    }
}