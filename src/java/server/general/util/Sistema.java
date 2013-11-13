package server.general.util;

import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;
import org.codehaus.jettison.json.JSONObject;
import server.general.error.AppException;

public class Sistema {
    public static String ServiceResponse(String sbCallback, String sbMsg, boolean blSuccess) throws AppException{
        try{
            if(sbCallback != null && !sbCallback.equals("")){
                JSONObject objJSONReturn = new JSONObject();
                objJSONReturn.put("success", blSuccess);
                objJSONReturn.put("data", sbMsg);
                return sbCallback + "(" + objJSONReturn + ")";
            } 
            else
                return sbMsg;
        }
        catch(Exception e){
            throw AppException.getException(e);
        }
    }
    
    public static String ServiceResponse(String sbCallback, JSONObject sbMsg, boolean blSuccess) throws AppException{
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
    
    public static void compressFile(File file, boolean blSameName, String sbFileDestName, boolean blFileDir, String sbDest, boolean blDeleteFile) throws AppException {
        ZIP zip = new ZIP();
        try {
            Vector files = new Vector(0);
            files.add(file);

            String sbExtension = Sistema.getFileExtension(file);

            String absolutePath = file.getAbsolutePath();
            String fileNameOutput = (blSameName ? file.getName().substring(0, (file.getName().length() - (sbExtension.length() + 1))) : sbFileDestName) + ".zip";
            String pathFileContenedor = (blFileDir ? absolutePath.substring(0, (absolutePath.length() - file.getName().length())) : sbDest) + fileNameOutput;

            System.out.println(">>absolutePath:"+absolutePath);
            System.out.println(">>fileNameOutput:"+fileNameOutput);
            System.out.println(">>pathFileContenedor:"+pathFileContenedor);
            
            System.out.println("Comprimiendo archivo {" + pathFileContenedor + "}");
            zip.Compress(files, pathFileContenedor);

            if (blDeleteFile) {
                file.delete();
            }
        } catch (Exception e) {
            throw AppException.getException(e);
        }
    }

    public static void compressFile(String path, boolean blSameName, String sbFileDestName, boolean blFileDir, String sbDest, boolean blDeleteFile) throws AppException {
        try {
            Sistema.compressFile(new File(path), blSameName, sbFileDestName, blFileDir, sbDest, blDeleteFile);
        } catch (Exception e) {
            throw AppException.getException(e);
        }
    }
    
    public static String getFileExtension(File file) throws AppException {
        try {
            String sbExtension = "";

            int i = file.getAbsolutePath().lastIndexOf('.');
            int p = Math.max(file.getAbsolutePath().lastIndexOf('/'), file.getAbsolutePath().lastIndexOf('\\'));

            if (i > p) {
                sbExtension = file.getAbsolutePath().substring(i + 1);
            }

            return sbExtension;
        } catch (Exception e) {
            throw AppException.getException(e);
        }
    }

    public static String getFileExtension(String path) throws AppException {
        try {
            return Sistema.getFileExtension(new File(path));
        } catch (Exception e) {
            throw AppException.getException(e);
        }
    }
}