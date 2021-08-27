package team.time.smartcalendar.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import team.time.smartcalendar.dataBeans.POIItem;

import java.util.ArrayList;
import java.util.List;

public class PositionViewModel extends ViewModel {
    private MutableLiveData<List<POIItem>>poiItems;
    private MutableLiveData<String>searchInfo;
    private MutableLiveData<String>city;

    public MutableLiveData<List<POIItem>> getPoiItems() {
        if(poiItems==null){
            poiItems=new MutableLiveData<>();
            poiItems.setValue(new ArrayList<>());
        }
        return poiItems;
    }

    public MutableLiveData<String> getSearchInfo() {
        if(searchInfo==null){
            searchInfo=new MutableLiveData<>();
            searchInfo.setValue("");
        }
        return searchInfo;
    }

    public MutableLiveData<String> getCity() {
        if (city==null){
            city=new MutableLiveData<>();
            city.setValue("");
        }
        return city;
    }
}
