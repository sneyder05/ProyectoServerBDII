package server.general.util;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import org.codehaus.jettison.json.JSONObject;
import server.general.error.AppException;

public class Sistema {
    public static String ServiceResponse(String sbCallback, String sbMsg, boolean blSuccess){
        if(sbCallback != null && !sbCallback.equals("")){
            return sbCallback + "({\"success\":" + blSuccess + ", \"data\":\"" + sbMsg.replaceAll("\r\n", ".") + "\"})";
        } 
        else
            return sbMsg;
    }
    
    public static String ServiceResponse(String sbCallback, JSONObject sbMsg, boolean blSuccess){
        return Sistema.ServiceResponse(sbCallback, sbMsg.toString(), blSuccess);
    }
    
    public static void Log(String sbWS, String sbData, String sbCallback){
        try {
            System.out.println("Executing [" + sbWS + "] with data [" + sbData + "] encapsuling callback [" + sbCallback + "] at " + Sistema.getSystemDateHour());
        } catch (AppException ex) {}
    }
    
    public static void LogCall(String sbWS, String sbData, String sbMsg){
        try {
            System.out.println("Ending [" + sbWS + "] with data [" + sbData + "] return [" + sbMsg + "] at " + Sistema.getSystemDateHour());
        } catch (AppException ex) {}
    }
    
    public static void LogCall(String sbWS, String sbData, JSONObject sbMsg){
        Sistema.LogCall(sbWS, sbData, sbWS.toString());
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
    
    public static void fieldRequired(String sbString, String sbNombreCampo) throws AppException {
        try {
            if (sbString == null || sbString.equals("")) {
                throw AppException.getException(30, "El campo " + sbNombreCampo + " no puede ser nulo.");
            }
        } catch (Exception ex) {
            throw AppException.getException(ex);
        }
    }
    
    public static Timestamp getSystemDateHour()
            throws AppException {
        try {
            Timestamp dtFechaHoraActual;

            Calendar objCalendario = Calendar.getInstance();
            objCalendario.set(Calendar.MILLISECOND, 0);
            dtFechaHoraActual = new Timestamp(objCalendario.getTime().getTime());
            return dtFechaHoraActual;
        } catch (Exception e) {
            throw AppException.getException(e);
        }
    }
}