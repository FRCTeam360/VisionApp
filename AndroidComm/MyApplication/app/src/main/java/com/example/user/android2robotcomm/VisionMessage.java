package com.example.user.android2robotcomm;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class VisionMessage {

    public abstract String getType();

    public abstract String getMessage();

    public String toJson() {
        JSONObject j = new JSONObject();
        try {
            j.put("type", getType());
            j.put("message", getMessage());
        } catch (JSONException e) {
            Log.e("VisionMessage", "Could not encode JSON");
        }
        return j.toString();
    }

}