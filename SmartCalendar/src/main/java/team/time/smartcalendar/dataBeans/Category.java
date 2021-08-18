package team.time.smartcalendar.dataBeans;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * category data structure receive from server
 */
public class Category {
    public int id;
    public String name;

    public Category(JSONObject jsonObject) throws JSONException {
        this.id=jsonObject.getInt("id");
        this.name=jsonObject.getString("name");
    }
}
