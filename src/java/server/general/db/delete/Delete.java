package server.general.db.delete;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import server.general.db.conexion.DBConexion;
import server.general.error.AppException;

/**
 * Sneyder Navia
 * fabiansneyder@gmail.com
 * Copyright 2013
 */
public class Delete {
    public static int DeleteField(String sbTable, String sbField) throws AppException {
        try{
            int nuIdx = 1;
            String sbSQL = "ALTER TABLE " + sbTable + " DROP COLUMN " + sbField;
            
            System.out.println(">>>>ALTER TABLE ? DROP COLUMN ?, " + sbTable + "," + sbField);
            
            PreparedStatement stm = DBConexion.getPreparedStatement(sbSQL);
            
            System.out.println("SQL::" + sbSQL + " >> " + stm.toString());

            return stm.executeUpdate();
        }
        catch(Exception e){
            throw AppException.getException(e);
        }
    }
}