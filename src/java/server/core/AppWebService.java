/*
 * Propiedad Intelectual Play Tech.
 */

package server.core;

import server.general.error.AppException;
import server.general.util.Configuracion;
import server.general.util.Sistema;

public class AppWebService {
    public static void initialize()
    throws AppException {
        try {
            String DIR_CONFIG_PROPERTIES = Sistema.getPathFromClass(Init.class, "Init", "server") + "Config.properties";

            Configuracion.CargarConfiguracion(DIR_CONFIG_PROPERTIES);
        } catch ( Exception e ) {
            AppException.getException( e );
        }
    }
}
