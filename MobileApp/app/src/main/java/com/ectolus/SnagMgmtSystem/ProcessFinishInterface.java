package com.ectolus.SnagMgmtSystem;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 02-04-2018.
 */

public interface ProcessFinishInterface {
    void processFinish(JSONObject output) throws JSONException;
}