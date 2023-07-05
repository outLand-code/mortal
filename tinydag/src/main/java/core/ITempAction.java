package core;

import com.alibaba.fastjson2.JSONObject;

public interface ITempAction {

    JSONObject doAction(JSONObject data);
}
