package team.time.smartcalendar.dataBeans;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeqItem {
    public String question;
    public Map<Integer,Integer>options;
    public List<String>descriptions;
    public int length;
    private int id;

    public MeqItem(JSONObject item){
        try {
            this.id=item.getInt("id");
            this.question="Q"+this.id+": "+item.getString("description");
            this.options=new HashMap<>();
            this.descriptions=new ArrayList<>();
            JSONArray options=item.getJSONArray("option");
            for(int i=0;i<options.length();i++){
                JSONObject option=options.getJSONObject(i);
                String description=option.getString("description");
                int score=option.getInt("score");
                this.descriptions.add(description);
                this.options.put(i,score);
            }
            this.length=this.options.size();
        } catch (JSONException e) {
            Log.d("lmx", "JSONException: "+e);
        }
    }
}
