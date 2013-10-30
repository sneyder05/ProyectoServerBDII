package server.general.util;

import java.net.URL;
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
    
    public static String getPathFromClass(Class objClass, String sbClassName, String sbIndexOf){
        String filePath = "";
        URL url = objClass.getResource(sbClassName + ".class");
        
        if(url == null) {
            return System.getProperty("user.dir");
        }
        
        String className = url.getFile();

        sbIndexOf = className.contains(sbIndexOf) ? sbIndexOf : sbClassName;
        filePath = className.substring(0,className.indexOf(sbIndexOf) + sbIndexOf.length());

        return filePath + "/";
    }
}