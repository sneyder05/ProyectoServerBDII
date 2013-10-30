package server.core.bo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import server.core.ADT.GenerateBackupInADT;
import server.core.ADT.LoginInADT;
import server.general.db.conexion.DBConexion;
import server.general.db.query.Query;
import server.general.error.AppException;
import server.general.util.Configuracion;
import server.general.util.Sistema;

/**
 * Sneyder Navia
 * fabiansneyder@gmail.com
 * Copyright 2013
 */
public class ServiceExe {
    public static String Login(LoginInADT objLoginInADT, String sbCallback) throws AppException{
        try{
            Class.forName ("oracle.jdbc.driver.OracleDriver");
            String sbCnn = "jdbc:oracle:thin:@"+Configuracion.serverDB+":"+ Configuracion.puertoDB+":"+objLoginInADT.getDB();
            System.out.println(">>Trying connect:" + sbCnn);
            DriverManager.getConnection(sbCnn, objLoginInADT.getLogin(), objLoginInADT.getPassword());
            
            Configuracion.usuarioDB = objLoginInADT.getLogin();
            Configuracion.passwordDB = objLoginInADT.getPassword();
            Configuracion.nombreDB = objLoginInADT.getDB();
            
            return Sistema.ServiceResponse(sbCallback, "", true);
        } catch (Exception ex) {
            DBConexion.rollback();
            throw AppException.getException(ex);
        }
    }
    
    public static String GenerateBackup(GenerateBackupInADT objGenerateBackupInADT, String sbCallback) throws AppException{
        try{
            ResultSet rsTables = Query.getTablesUser(Configuracion.usuarioDB);
            
            while(rsTables.next()){
                System.out.println(">>>" + rsTables.getString("table_name"));
            }
            
            return Sistema.ServiceResponse(sbCallback, "", true);
        } catch (Exception ex) {
            DBConexion.rollback();
            throw AppException.getException(ex);
        }
    }
}