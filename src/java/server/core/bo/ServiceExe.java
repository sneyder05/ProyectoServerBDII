package server.core.bo;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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
            ResultSet rsColumns, rsConstraints;
            StringBuilder sbSQL = new StringBuilder();
            
            while(rsTables.next()){
                String sbTabla = rsTables.getString("table_name");
                
                /*
                 * GET TABLE COLUMNS
                 */
                rsColumns = Query.getTableColumns(sbTabla);
                
                sbSQL.append("CREATE TABLE \"").append(sbTabla).append("\" (");
                
                while(rsColumns.next()){
                    String sbColumnName = rsColumns.getString("COLUMN_NAME");
                    String sbDataType = rsColumns.getString("DATA_TYPE");
                    int nuDataLength = rsColumns.getInt("DATA_LENGTH");
                    int nuDataPrecision = rsColumns.getInt("DATA_PRECISION");
                    int nuDataScale = rsColumns.getInt("DATA_SCALE");
                    boolean blNullable = rsColumns.getString("NULLABLE").equals("Y");
                    
                    System.out.println(">sbColumnName:"+sbColumnName+",sbDataType:"+sbDataType+",nuDataLength:"+nuDataLength+",nuDataPrecision:"+nuDataPrecision+",nuDataScale:"+nuDataScale+",blNullable:"+blNullable);
                    
                    sbSQL.append("\t\"").append(sbColumnName).append("\" ").append(ServiceExe.FormatFieldType(sbDataType));
                    
                    if(sbDataType.equals("?NUMBER") && nuDataPrecision > 0){
                        sbSQL.append("(").append(nuDataPrecision).append(nuDataScale > 0 ? "," + nuDataScale : "").append(")");
                    }
                    else if(Arrays.asList("VARCHAR2", "NVARCHAR2", "CHAR").contains(sbDataType)){
                        sbSQL.append("(").append(nuDataLength).append(")");
                    }
                    
                    sbSQL.append(!blNullable ? " NOT NULL" : "").append(!rsColumns.isLast() ? "," : "");
                }
                
                /*
                 * GET TABLE CONTRAINTS
                 */
                rsConstraints = Query.getTableConstraints(sbTabla);
                
                while(rsConstraints.next()){
                    if(!sbSQL.toString().endsWith(",")){
                        sbSQL.append(",");
                    }
                    
                    String sbConstraintName = rsConstraints.getString("CONSTRAINT_NAME");
                    
                    System.out.println(">sbConstraintName:"+sbConstraintName+",sbConstraintType:"+rsConstraints.getString("CONSTRAINT_TYPE"));
                    
                    sbSQL.append("CONSTRAINT ").append(sbConstraintName).append(!rsColumns.isLast() ? "," : "");
                }
                
                sbSQL.append("\r\n);\r\n");
            }
            
            return Sistema.ServiceResponse(sbCallback, sbSQL.toString(), true);
        } catch (Exception ex) {
            DBConexion.rollback();
            throw AppException.getException(ex);
        }
    }
    
    private static String FormatFieldType(String sbName) throws AppException{
        try{
            return sbName.toUpperCase().startsWith("TIMESTAMP") ? "TIMESTAMP" : sbName;
        }
        catch(Exception e){
            throw AppException.getException(e);
        }
    }
}