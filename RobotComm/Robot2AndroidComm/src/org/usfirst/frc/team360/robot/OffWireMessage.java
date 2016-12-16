
package org.usfirst.frc.team360.robot;

import org.usfirst.frc.team360.JSON.*;
/**
 * Used to convert Strings into OffWireMessage objects, which can be interpreted
 * as generic VisionMessages.
 */
public class OffWireMessage  {

    private boolean mValid = false;
    private String mType = "unknown";
    private String mMessage = "{}";

    public OffWireMessage(String message) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject j = (JSONObject) parser.parse(message);
            mType = (String) j.get("type");
            mMessage = (String) j.get("message");
            mValid = true;
        } catch (ParseException e) {
        }
    }
    public String toJson() {
        JSONObject j = new JSONObject();
        j.put("type", getType());
        j.put("message", getMessage());
        return j.toString();
    }

    public boolean isValid() {
        return mValid;
    }

    public String getType() {
        return mType;
    }

    public String getMessage() {
        return mMessage;
    }
}