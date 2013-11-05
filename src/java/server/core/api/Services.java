package server.core.api;

import com.google.gson.Gson;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import server.core.ADT.GenerateBackupInADT;
import server.core.ADT.GetTableInfoInADT;
import server.core.ADT.LoginInADT;
import server.core.bo.ServiceExe;
import server.general.error.AppException;
import server.general.util.Sistema;

@Path("/Services")
public class Services {
    
    @GET
    @Path("/Test")
    public String Test(@QueryParam("data") String sbData, @QueryParam("callback") String sbCallback){
        String sbResponse = "";
        try{
            sbResponse = Sistema.ServiceResponse(sbCallback, "", true);
        }
        catch(Exception e){
            sbResponse = Sistema.ServiceResponse(sbCallback, e.getMessage(), false);
        }
        finally{
            System.out.println("end with_" + sbResponse);
            return sbResponse;
        }
    }
    
    @GET
    @Path("/Login")
    public String Login(@QueryParam("data") String sbData, @QueryParam("callback") String sbCallback){
        String sbResponse = "";
        try{
            Sistema.Log("/Login", sbData, sbCallback);
            
            Gson objGson = new Gson();
            LoginInADT objLoginInADT = objGson.fromJson(sbData, LoginInADT.class);
            
            Sistema.fieldRequired(objLoginInADT.getLogin(), "Login");
            Sistema.fieldRequired(objLoginInADT.getPassword(), "Password");
            Sistema.fieldRequired(objLoginInADT.getDB(), "DB");
                        
            sbResponse = ServiceExe.Login(objLoginInADT, sbCallback);
        }
        catch(AppException e){
            sbResponse = Sistema.ServiceResponse(sbCallback, e.getParametrosError(), false);
        }
        catch(Exception e){
            sbResponse = Sistema.ServiceResponse(sbCallback, e.getMessage(), false);
        }
        finally{
            Sistema.LogCall("/Login", sbData, sbResponse);
            return sbResponse;
        }
    }
    
    @GET
    @Path("/GenerateBackup")
    public String GenerateBackup(@QueryParam("data") String sbData, @QueryParam("callback") String sbCallback){
        String sbResponse = "";
        try{
            Sistema.Log("/GenerateBackup", sbData, sbCallback);
            
            Gson objGson = new Gson();
            GenerateBackupInADT objGenerateBackupInADT = objGson.fromJson(sbData, GenerateBackupInADT.class);
            
            Sistema.fieldRequired(objGenerateBackupInADT.getFileName(), "Archivo destino");
            
            sbResponse = ServiceExe.GenerateBackup(objGenerateBackupInADT, sbCallback);
        }
        catch(AppException e){
            sbResponse = Sistema.ServiceResponse(sbCallback, e.getParametrosError(), false);
        }
        catch(Exception e){
            sbResponse = Sistema.ServiceResponse(sbCallback, e.getMessage(), false);
        }
        finally{
            Sistema.LogCall("/GenerateBackup", sbData, sbResponse);
            return sbResponse;
        }
    }
    
    @GET
    @Path("/GetTableInfo")
    public String GetTableInfo(@QueryParam("data") String sbData, @QueryParam("callback") String sbCallback){
        String sbResponse = "";
        try{
            Sistema.Log("/GenerateBackup", sbData, sbCallback);
            
            Gson objGson = new Gson();
            GetTableInfoInADT objGetTableInfoInADT = objGson.fromJson(sbData, GetTableInfoInADT.class);
            
            Sistema.fieldRequired(objGetTableInfoInADT.getTable(), "Nombre de la tabla");
            
            sbResponse = ServiceExe.GetTableInfo(objGetTableInfoInADT, sbCallback);
        }
        catch(AppException e){
            sbResponse = Sistema.ServiceResponse(sbCallback, e.getParametrosError(), false);
        }
        catch(Exception e){
            sbResponse = Sistema.ServiceResponse(sbCallback, e.getMessage(), false);
        }
        finally{
            Sistema.LogCall("/GenerateBackup", sbData, sbResponse);
            return sbResponse;
        }
    }
}