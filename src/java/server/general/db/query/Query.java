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
    public static ResultSet getTablesUser(String sbUser, boolean blSort) throws AppException {
        try{
            int nuIdx = 1;
            String sbSQL = "SELECT OWNER, TABLE_NAME FROM ALL_TABLES WHERE OWNER = ?" + (blSort ? " ORDER BY TABLE_NAME" : "");
            PreparedStatement stm = DBConexion.getPreparedStatement(sbSQL);

            stm.setString(nuIdx++, sbUser);
            
            System.out.println("SQL::" + sbSQL + " >> " + stm.toString());

            return stm.executeQuery();
        }
        catch(Exception e){
            throw AppException.getException(e);
        }
    }
    
    public static ResultSet getTableColumns(String sbTable) throws AppException {
        try{
            int nuIdx = 1;
            String sbSQL = "SELECT ATC.* FROM ALL_TAB_COLUMNS ATC WHERE ATC.TABLE_NAME = ? ORDER BY COLUMN_ID";
            PreparedStatement stm = DBConexion.getPreparedStatement(sbSQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            stm.setString(nuIdx++, sbTable);
            
            System.out.println("SQL::" + sbSQL + " >> " + stm.toString());

            return stm.executeQuery();
        }
        catch(Exception e){
            throw AppException.getException(e);
        }
    }
    
    public static ResultSet getTableConstraints(String sbTable) throws AppException {
        try{
            int nuIdx = 1;
            String sbSQL = "SELECT * FROM ALL_CONSTRAINTS WHERE CONSTRAINT_NAME NOT LIKE 'SYS%' AND TABLE_NAME = ?";
            PreparedStatement stm = DBConexion.getPreparedStatement(sbSQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            stm.setString(nuIdx++, sbTable);
            
            System.out.println("SQL::" + sbSQL + " >> " + stm.toString());

            return stm.executeQuery();
        }
        catch(Exception e){
            throw AppException.getException(e);
        }
    }
    
    public static ResultSet getDetailConstraint(String sbConstraint) throws AppException {
        try{
            int nuIdx = 1;
            String sbSQL = "SELECT * FROM ALL_CONS_COLUMNS WHERE CONSTRAINT_NAME = ? ORDER BY POSITION";
            PreparedStatement stm = DBConexion.getPreparedStatement(sbSQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            stm.setString(nuIdx++, sbConstraint);
            
            System.out.println("SQL::" + sbSQL + " >> " + stm.toString());

            return stm.executeQuery();
        }
        catch(Exception e){
            throw AppException.getException(e);
        }
    }
    
    public static ResultSet getColumnConstraints(String sbTable, String sbColumn) throws AppException {
        try{
            int nuIdx = 1;
            String sbSQL = "SELECT " +
                                "AC.CONSTRAINT_TYPE " +
                            "FROM " +
                                "ALL_CONSTRAINTS AC," +
                                "ALL_CONS_COLUMNS ACC " +
                            "WHERE " +
                                "AC.CONSTRAINT_NAME = ACC.CONSTRAINT_NAME " +
                                "AND ACC.TABLE_NAME = ? " +
                                "AND ACC.COLUMN_NAME = ? " +
                                "AND AC.CONSTRAINT_NAME NOT LIKE 'SYS%' " +
                            "GROUP BY " +
                                "AC.CONSTRAINT_TYPE";
            PreparedStatement stm = DBConexion.getPreparedStatement(sbSQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            stm.setString(nuIdx++, sbTable);
            stm.setString(nuIdx++, sbColumn);
            
            System.out.println("SQL::" + sbSQL + " >> " + stm.toString());

            return stm.executeQuery();
        }
        catch(Exception e){
            throw AppException.getException(e);
        }
    }
    
    public static ResultSet getTableTriggers(String sbTable) throws AppException {
        try{
            int nuIdx = 1;
            String sbSQL = "SELECT * FROM ALL_TRIGGERS WHERE TABLE_NAME = ?";
            PreparedStatement stm = DBConexion.getPreparedStatement(sbSQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            stm.setString(nuIdx++, sbTable);
            
            System.out.println("SQL::" + sbSQL + " >> " + stm.toString());

            return stm.executeQuery();
        }
        catch(Exception e){
            throw AppException.getException(e);
        }
    }
    
    public static ResultSet getSequences(String sbUser) throws AppException {
        try{
            int nuIdx = 1;
            String sbSQL = "SELECT * FROM ALL_SEQUENCES WHERE SEQUENCE_OWNER = ?";
            PreparedStatement stm = DBConexion.getPreparedStatement(sbSQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            
            stm.setString(nuIdx++, sbUser);
            
            System.out.println("SQL::" + sbSQL + " >> " + stm.toString());

            return stm.executeQuery();
        }
        catch(Exception e){
            throw AppException.getException(e);
        }
    }
}