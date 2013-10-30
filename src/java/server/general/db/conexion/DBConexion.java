/*
 * Propiedad Intelectual Play Tech.
 */

package server.general.db.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import server.general.error.AppException;
import server.general.util.Configuracion;

public class DBConexion  {

    static boolean buscandoConexion = false;
    static boolean buscandoConexionBackup = false;
    
    static class Conexion {
        public Conexion(){
        }
        public Connection con;
        public boolean    act;
    }

    public static Vector<Conexion> conexiones = new Vector<Conexion>();
    public static Vector<Conexion> conexionesBackup= new Vector<Conexion>();
    
    static private Conexion addConexion()
    throws AppException {
        try{
            System.setProperty("file.encoding", "iso-8859-1");                                              
            
            Conexion conexion = new Conexion();
            System.out.println("Inicializando conexion DB...");
            Connection con;
            Class.forName ("oracle.jdbc.driver.OracleDriver");
            String sbCnn = "jdbc:oracle:thin:@"+Configuracion.serverDB+":"+ Configuracion.puertoDB+":"+Configuracion.nombreDB;
            System.out.println(">>CNN:" + sbCnn);
            con = DriverManager.getConnection(sbCnn, Configuracion.usuarioDB, Configuracion.passwordDB);           

            conexion.con = con;
            conexion.con.setAutoCommit(false);
            conexion.act = true;
            conexiones.addElement(conexion);            
            
            return conexion;
        }
        catch ( Exception e ) {
            throw AppException.getException ( e );
        }
    }
    
    static private synchronized Conexion buscarConexion()
    throws AppException {
        try{
            while (buscandoConexion){
            	Thread.sleep(50);
            }
            buscandoConexion = true;
            Conexion conexion = null;
            boolean encontro = false;
            for ( int i = 0; i<conexiones.size() && !encontro ; i++){
                conexion = (Conexion) conexiones.elementAt(i);
                if ( ! conexion.act ){
                    encontro = true;
                    conexion.act = true;
                }
            }
            if ( ! encontro ){
                conexion = addConexion();
            }
            buscandoConexion = false;
            System.out.println("Conexiones abiertas "+conexiones.size());
            return conexion;

        }
        catch ( Exception e ) {
            buscandoConexion = false;
            throw AppException.getException ( e );
        }
    }
    
    static private synchronized Conexion buscarConexionBackup()
    throws AppException {
        try{
            while (buscandoConexionBackup);
            buscandoConexionBackup = true;
            Conexion conexion = null;
            boolean encontro = false;
            for ( int i = 0; i<conexionesBackup.size() && !encontro ; i++){
                conexion = (Conexion) conexionesBackup.elementAt(i);
                if ( ! conexion.act ){
                    encontro = true;
                    conexion.act = true;
                }
            }
            buscandoConexionBackup = false;
            System.out.println("Conexiones Backup abiertas "+conexionesBackup.size());
            return conexion;

        }
        catch ( Exception e ) {
            buscandoConexionBackup = false;
            //throw AppException.getException ( e );
        }
        return null;
    }

    private static ThreadLocal<Conexion> conexionLocal = new ThreadLocal<Conexion>(){
        protected Conexion initialValue() {
            try{
                //este codigo se ejecuta una sola vez por session
                Conexion con;
                con = buscarConexion();
                con.con.setAutoCommit(false);
                return con;
            }catch ( Exception e ) {
                return null;
            }
        }
    };
    
    private static ThreadLocal conexionLocalBackup = new ThreadLocal(){
        protected Object initialValue() {
            try{
                //este codigo se ejecuta una sola vez por session
                Conexion con;
                con = buscarConexionBackup();
                con.con.setAutoCommit(false);
                return con;
            }catch ( Exception e ) {
                return null;
            }
        }
    };

    private static Connection getDBConexion()
    throws AppException {
        try{
            Conexion conexionDB;
            conexionDB = (Conexion) conexionLocal.get();
            if (conexionDB==null) {
            	conexionLocal.set(buscarConexion());
            	conexionDB=(Conexion)conexionLocal.get();
            }
            
            if (conexionDB==null)
            	throw AppException.getException(20);
            
            return conexionDB.con;
        }
        catch ( Exception e ) {
            throw AppException.getException ( e );
        }
    }
    
    private static Connection getDBConexionBackup()
    throws AppException {
        try{
            Conexion conexionDB;
            conexionDB = (Conexion) conexionLocalBackup.get();
            return conexionDB.con;
        }
        catch ( Exception e ) {
            throw AppException.getException ( e );
        }
    }

    public static PreparedStatement getPreparedStatement( String sbStatement )
    throws AppException {
        try{
            PreparedStatement ps;
            ps = getDBConexion().prepareStatement(sbStatement);            
            return ps;
        }
        catch ( Exception e ) {
            throw AppException.getException ( e );
        }
    }
    
    public static PreparedStatement getPreparedStatement( String sbStatement, int nuType, int nuConcurrency )
    throws AppException {
        try{
            PreparedStatement ps;
            ps = getDBConexion().prepareStatement(sbStatement, nuType, nuConcurrency);            
            return ps;
        }
        catch ( Exception e ) {
            throw AppException.getException ( e );
        }
    }
    
    // 05-ago-2005 - 12:39:10
    public static PreparedStatement getPreparedStatementBackup (PreparedStatement stmPreparerReady)
    throws AppException {
        try {
            PreparedStatement ps;
            ps = getDBConexionBackup().prepareStatement(stmPreparerReady.toString());
            return ps;
        } catch (Exception e) {
            throw AppException.getException (e);
        }
    }
    
    public static void commit()
    throws AppException {
        try{
            getDBConexion().commit();
        }
        catch ( Exception e ) {
            throw AppException.getException ( e );
        }
    }
    
    public static void rollback()
    throws AppException {
        try{
            getDBConexion().rollback();
        }
        catch ( Exception e ) {
            throw AppException.getException ( e );
        }
    }

    public static void close(){
        try{
            System.out.println("Cerrando conexion DB...");
            Conexion conexionDB;
            conexionDB = (Conexion) conexionLocal.get();
            conexionLocal.set(null);
            conexionDB.act = false;
        }
        catch ( Exception e ) {
            System.out.println("Error cerrando conexion a base de datos"+e);
        }
    }
    
}
