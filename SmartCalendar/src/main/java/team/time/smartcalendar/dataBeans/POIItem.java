package team.time.smartcalendar.dataBeans;

import com.amap.api.services.core.PoiItem;

public class POIItem {
    public String name;
    public String address;
    public double latitude;
    public double longitude;

    public POIItem(PoiItem item) {
        this.name = item.getTitle();
        this.address = item.getSnippet();
        this.latitude = item.getLatLonPoint().getLatitude();
        this.longitude = item.getLatLonPoint().getLongitude();
    }

    @Override
    public String toString() {
        return "POIItem{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
