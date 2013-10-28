package server.general.util;

import org.codehaus.jettison.json.JSONObject;

public class Sistema {
    public static String ServiceResponse(String sbCallback, String sbMsg, boolean blSuccess){
        if(sbCallback != null && !sbCallback.equals("")){
            return sbCallback + "({\"success\":" + blSuccess + ", \"data\":\"" + sbMsg + "\"})";
        } 
        else
            return sbMsg;
    }
    
    public static String ServiceResponse(String sbCallback, JSONObject sbMsg, boolean blSuccess){
        return Sistema.ServiceResponse(sbCallback, sbMsg.toString(), blSuccess);
    }
}