package server.core.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
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
            return sbResponse;
        }
    }
}