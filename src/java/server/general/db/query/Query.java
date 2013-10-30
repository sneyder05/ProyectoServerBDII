package server.general.db.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import server.general.db.conexion.DBConexion;
import server.general.error.AppException;

/**
 * Sneyder Navia
 * fabiansneyder@gmail.com
 * Copyright 2013
 */
public class Query {
    public static ResultSet getTablesUser(String sbUser) throws AppException {
        try{
            int nuIdx = 1;
            String sbSQL = "SELECT owner, table_name FROM all_tables WHERE owner = ?";
            PreparedStatement stm = DBConexion.getPreparedStatement(sbSQL);

            stm.setString(nuIdx++, sbUser);
            
            System.out.println("SQL::" + stm.toString());

            return stm.executeQuery();
        }
        catch(Exception e){
            throw AppException.getException(e);
        }
    }
}