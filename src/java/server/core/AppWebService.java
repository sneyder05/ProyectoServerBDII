/*
 * Propiedad Intelectual Play Tech.
 */

package server.core;

import server.general.db.conexion.DBConexion;
import server.general.error.AppException;
import server.general.util.Configuracion;

public class AppWebService {
    public static void initialize()
    throws AppException {
        try {
            String DIR_CONFIG_PROPERTIES = "/ConfigServer.properties";

            Configuracion.CargarConfiguracion(DIR_CONFIG_PROPERTIES);
            
            DBConexion.getPreparedStatement("SELECT SYSDATE FROM DUAL");
        } catch ( Exception e ) {
            AppException.getException( e );
        }
    }
}
