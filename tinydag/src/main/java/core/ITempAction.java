package core;

import com.alibaba.fastjson2.JSONObject;

public interface ITempAction {

    JSONObject doAction(JSONObject data);

    default Object getFlotsam(){
        return TinyDag.GetFlotsam();
    }

    default String getTrackId(){
        return TinyDag.GetTrackId();
    }
}
