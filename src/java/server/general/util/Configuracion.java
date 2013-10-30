/*
 * Propiedad Intelectual Play Tech.
 */

package server.general.util;

import java.io.FileInputStream;
import java.util.Properties;
import server.general.error.AppException;

public class Configuracion  {

    public static String usuarioDB;
    public static String passwordDB;
    public static String nombreDB;
    public static String serverDB;
    public static int puertoDB;

    public static void CargarConfiguracion( String sbRutaArchivo )
    throws AppException {
        System.out.println("Trying load configuration from {" + sbRutaArchivo + "}");
        try{
            Properties objProperties = new Properties();
            FileInputStream fiArchivoConfiguracion;
            fiArchivoConfiguracion = new FileInputStream( sbRutaArchivo );
            objProperties.load( fiArchivoConfiguracion );
	    
            usuarioDB = objProperties.getProperty("usuarioDB");
            passwordDB = objProperties.getProperty("paswordDB");
            nombreDB = objProperties.getProperty("nombreDB");
            serverDB = objProperties.getProperty("serverDB");
            puertoDB = Integer.parseInt(objProperties.getProperty("puertoDB"));
            
            System.out.println("CONFIGURATION::\nusuarioDB:"+usuarioDB+"\npasswordDB:"+passwordDB+"\nnombreDB:"+nombreDB+"\nserverDB:"+serverDB+"\npuertoDB:"+puertoDB);
        } catch ( Exception e ) {
            throw   AppException.getException( e );
        }
    }

}

