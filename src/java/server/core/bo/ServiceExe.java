package server.core.bo;

import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Arrays;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import server.core.ADT.GenerateBackupInADT;
import server.core.ADT.GetTableInfoInADT;
import server.core.ADT.LoginInADT;
import server.general.db.conexion.DBConexion;
import server.general.db.query.Query;
import server.general.error.AppException;
import server.general.util.Configuracion;
import server.general.util.Sistema;

/**
 * Sneyder Navia fabiansneyder@gmail.com Copyright 2013
 */
public class ServiceExe {

    public static String Login(LoginInADT objLoginInADT, String sbCallback) throws AppException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String sbCnn = "jdbc:oracle:thin:@" + Configuracion.serverDB + ":" + Configuracion.puertoDB + ":" + objLoginInADT.getDB();
            System.out.println(">>Trying connect:" + sbCnn);
            DriverManager.getConnection(sbCnn, objLoginInADT.getLogin(), objLoginInADT.getPassword());

            Configuracion.usuarioDB = objLoginInADT.getLogin().toUpperCase();
            Configuracion.passwordDB = objLoginInADT.getPassword().toUpperCase();
            Configuracion.nombreDB = objLoginInADT.getDB().toUpperCase();

            JSONObject objJSONReturn = new JSONObject();
            objJSONReturn.put("tables", ServiceExe.GetTablesFromUser());

            return Sistema.ServiceResponse(sbCallback, objJSONReturn, true);
        } catch (Exception ex) {
            DBConexion.rollback();
            throw AppException.getException(ex);
        }
    }

    public static JSONObject GetTablesFromUser() throws AppException {
        try {
            JSONObject objJSONTables = new JSONObject();
            ResultSet rsTables = Query.getTablesUser(Configuracion.usuarioDB, true);

            while (rsTables.next()) {
                String sbTabla = rsTables.getString("table_name");

                objJSONTables.put(sbTabla, sbTabla);
            }

            return objJSONTables;
        } catch (Exception ex) {
            throw AppException.getException(ex);
        }
    }

    public static String GetTableInfo(GetTableInfoInADT objGetTableInfoInADT, String sbCallback) throws AppException {
        try {
            ResultSet rsColumns, rsColumnConstraints, rsConstraints, rsDetailConstraint, rsFKDetailConstraint;

            /*
             * Columns
             */
            JSONObject objJSONTable, objJSONColumns, objJSONColumn, objJSONColumnConstraints;

            objJSONTable = new JSONObject();
            objJSONColumns = new JSONObject();
            
            rsColumns = Query.getTableColumns(objGetTableInfoInADT.getTable());

            while (rsColumns.next()) {
                objJSONColumn = new JSONObject();
                
                String sbColumnName = rsColumns.getString("COLUMN_NAME");
                String sbDataType = rsColumns.getString("DATA_TYPE");
                int nuDataLength = rsColumns.getInt("DATA_LENGTH");
                int nuDataPrecision = rsColumns.getInt("DATA_PRECISION");
                int nuDataScale = rsColumns.getInt("DATA_SCALE");
                boolean blNullable = rsColumns.getString("NULLABLE").equals("Y");

                rsColumnConstraints = Query.getColumnConstraints(objGetTableInfoInADT.getTable(), sbColumnName);
                objJSONColumnConstraints = new JSONObject();
                
                while(rsColumnConstraints.next()){
                    String sbConstraintType = rsColumnConstraints.getString("CONSTRAINT_TYPE");                    
                    String sbKey = "is" + sbConstraintType;
                    objJSONColumnConstraints.put(sbKey, true);
                }
                
                objJSONColumn.put("type", sbDataType);
                objJSONColumn.put("length", nuDataLength);
                objJSONColumn.put("precision", nuDataPrecision);
                objJSONColumn.put("scale", nuDataScale);
                objJSONColumn.put("nullable", blNullable);
                objJSONColumn.put("has_constraint", objJSONColumnConstraints);

                objJSONColumns.put(sbColumnName, objJSONColumn);
            }

            objJSONTable.put("columns", objJSONColumns);

            /*
             * Constraints
             */
            JSONObject objJSONConstraints, objJSONConstraint, objJSONDetailFKConst;
            JSONArray objArrayColumnConst, objArrayColumnFKConst;

            objJSONConstraints = new JSONObject();
            
            rsConstraints = Query.getTableConstraints(objGetTableInfoInADT.getTable());

            while (rsConstraints.next()) {
                objJSONConstraint = new JSONObject();
                objJSONDetailFKConst = new JSONObject();
                objArrayColumnConst = new JSONArray();
                objArrayColumnFKConst = new JSONArray();
                
                String sbConstraintName = rsConstraints.getString("CONSTRAINT_NAME");
                String sbConstraingType = rsConstraints.getString("CONSTRAINT_TYPE");
                String sbFKConstraintName = rsConstraints.getString("R_CONSTRAINT_NAME");
                String sbDeleteRule = rsConstraints.getString("DELETE_RULE");
                String sbSearchCond = rsConstraints.getString("SEARCH_CONDITION");

                objJSONConstraint.put("type", sbConstraingType);
                objJSONConstraint.put("fk_name", sbFKConstraintName);
                objJSONConstraint.put("delete_rule", sbDeleteRule);
                objJSONConstraint.put("search_cond", sbSearchCond);

                /*
                 * GET DETAIL CONSTRAINT
                 */
                rsDetailConstraint = Query.getDetailConstraint(sbConstraintName);

                if (!rsDetailConstraint.isAfterLast() && !rsDetailConstraint.isBeforeFirst()) {
                    System.out.println("No data for constraint [" + sbConstraintName + "]-> Omiting...");
                    continue;
                }

                /*
                 * GET DETAIL FOR FK CONSTRAINT
                 */
                rsFKDetailConstraint = Query.getDetailConstraint(sbFKConstraintName);

                if (sbConstraingType.equals("R") && !rsFKDetailConstraint.isAfterLast() && !rsFKDetailConstraint.isBeforeFirst()) {
                    System.out.println("No data for FK constraint [" + sbFKConstraintName + "]-> Omiting...");
                    continue;
                }

                while (rsDetailConstraint.next()) {
                    String sbColumnName = rsDetailConstraint.getString("COLUMN_NAME");
                    objArrayColumnConst.put(sbColumnName);
                }
                
                objJSONConstraint.put("detail", objArrayColumnConst);
                
                String sbFKTable = "";
                while (rsFKDetailConstraint.next()) {
                    String sbColumnName = rsFKDetailConstraint.getString("COLUMN_NAME");
                    sbFKTable = rsFKDetailConstraint.getString("TABLE_NAME");
                    objArrayColumnFKConst.put(sbColumnName);
                }
                
                objJSONDetailFKConst.put("table", sbFKTable);
                objJSONDetailFKConst.put("columns", objArrayColumnFKConst);
                
                objJSONConstraint.put("detail_fk", objJSONDetailFKConst);
                
                objJSONConstraints.put(sbConstraintName, objJSONConstraint);
            }

            objJSONTable.put("contraints", objJSONConstraints);

            return Sistema.ServiceResponse(sbCallback, objJSONTable, true);
        } catch (Exception ex) {
            DBConexion.rollback();
            throw AppException.getException(ex);
        }
    }
    
    public static String GetTableSQL(GetTableInfoInADT objGetTableInfoInADT, String sbCallback) throws AppException {
        try {
            ResultSet rsTables = Query.getTablesUser(Configuracion.usuarioDB, false);
            ResultSet rsColumns, rsConstraints, rsDetailConstraint, rsFKDetailConstraint;
            StringBuilder sbSQL = new StringBuilder();

            while (rsTables.next()) {
                String sbTabla = rsTables.getString("table_name");
                
                /*
                 * Check table
                 */
                if(!sbTabla.toUpperCase().equals(objGetTableInfoInADT.getTable().toUpperCase())){
                    continue;
                }

                /*
                 * GET TABLE COLUMNS
                 */
                rsColumns = Query.getTableColumns(sbTabla);

                sbSQL.append("CREATE TABLE \"").append(sbTabla).append("\" (\r\n");

                while (rsColumns.next()) {
                    String sbColumnName = rsColumns.getString("COLUMN_NAME");
                    String sbDataType = rsColumns.getString("DATA_TYPE");
                    int nuDataLength = rsColumns.getInt("DATA_LENGTH");
                    int nuDataPrecision = rsColumns.getInt("DATA_PRECISION");
                    int nuDataScale = rsColumns.getInt("DATA_SCALE");
                    boolean blNullable = rsColumns.getString("NULLABLE").equals("Y");

                    System.out.println(">sbColumnName:" + sbColumnName + ",sbDataType:" + sbDataType + ",nuDataLength:" + nuDataLength + ",nuDataPrecision:" + nuDataPrecision + ",nuDataScale:" + nuDataScale + ",blNullable:" + blNullable);

                    sbSQL.append("\t\"").append(sbColumnName).append("\" ").append(ServiceExe.FormatFieldType(sbDataType));

                    if (sbDataType.equals("?NUMBER") && nuDataPrecision > 0) {
                        sbSQL.append("(").append(nuDataPrecision).append(nuDataScale > 0 ? "," + nuDataScale : "").append(")");
                    } else if (Arrays.asList("VARCHAR2", "NVARCHAR2", "CHAR").contains(sbDataType)) {
                        sbSQL.append("(").append(nuDataLength).append(")");
                    }

                    sbSQL.append(!blNullable ? " NOT NULL" : "").append(!rsColumns.isLast() ? "," : "").append(!rsColumns.isLast() ? "\r\n" : "");
                }

                /*
                 * GET TABLE CONTRAINTS
                 */
                rsConstraints = Query.getTableConstraints(sbTabla);

                if (!rsConstraints.isAfterLast() && !rsConstraints.isBeforeFirst()) {
                    sbSQL.append("\r\n");
                }

                while (rsConstraints.next()) {
                    if (rsConstraints.isFirst() && !sbSQL.toString().endsWith(",")) {
                        sbSQL.append(",\r\n");
                    }

                    String sbConstraintName = rsConstraints.getString("CONSTRAINT_NAME");
                    String sbConstraingType = rsConstraints.getString("CONSTRAINT_TYPE");
                    String sbFKConstraintName = rsConstraints.getString("R_CONSTRAINT_NAME");
                    String sbDeleteRule = rsConstraints.getString("DELETE_RULE");
                    String sbSearchCond = rsConstraints.getString("SEARCH_CONDITION");

                    /*
                     * GET DETAIL CONSTRAINT
                     */
                    rsDetailConstraint = Query.getDetailConstraint(sbConstraintName);

                    if (!rsDetailConstraint.isAfterLast() && !rsDetailConstraint.isBeforeFirst()) {
                        System.out.println("No data for constraint [" + sbConstraintName + "]-> Omiting...");
                        continue;
                    }

                    /*
                     * GET DETAIL FOR FK CONSTRAINT
                     */
                    rsFKDetailConstraint = Query.getDetailConstraint(sbFKConstraintName);

                    if (sbConstraingType.equals("R") && !rsFKDetailConstraint.isAfterLast() && !rsFKDetailConstraint.isBeforeFirst()) {
                        System.out.println("No data for FK constraint [" + sbFKConstraintName + "]-> Omiting...");
                        continue;
                    }

                    System.out.println(">sbConstraintName:" + sbConstraintName + ",sbConstraintType:" + sbConstraingType + ",sbDeleteRule:" + sbDeleteRule + ",sbSearchCond:" + sbSearchCond);

                    sbSQL.append("\tCONSTRAINT \"").append(sbConstraintName).append("\" ");

                    String sbKeys = "";

                    if (Arrays.asList("P", "U").contains(sbConstraingType)) {
                        sbKeys = (sbConstraingType.equals("P") ? "PRIMARY KEY" : "UNIQUE") + "({$KEYS})";
                    } else if (sbConstraingType.equals("R")) {
                        sbKeys = "FOREIGN KEY ({$KEYS}) REFERENCES \"{$TABLE}\" ({$FK_KEYS})";
                    } else if (sbConstraingType.equals("C")) {
                        sbKeys = "CHECK " + sbSearchCond;
                    }

                    String sbColumns = "";
                    while (rsDetailConstraint.next()) {
                        String sbColumnName = rsDetailConstraint.getString("COLUMN_NAME");
                        sbColumns += "\"" + sbColumnName + "\"" + (!rsDetailConstraint.isLast() ? "," : "");
                    }

                    String sbFKColumns = "";
                    String sbFKTable = "";
                    while (rsFKDetailConstraint.next()) {
                        String sbColumnName = rsFKDetailConstraint.getString("COLUMN_NAME");
                        sbFKTable = rsFKDetailConstraint.getString("TABLE_NAME");
                        sbFKColumns += "\"" + sbColumnName + "\"" + (!rsFKDetailConstraint.isLast() ? "," : "");
                    }

                    sbSQL.append(sbKeys.replaceAll("\\{\\$KEYS\\}", sbColumns).replaceAll("\\{\\$FK_KEYS\\}", sbFKColumns).replaceAll("\\{\\$TABLE\\}", sbFKTable));

                    sbSQL.append(sbDeleteRule != null && sbDeleteRule.equals("CASCADE") ? " ON DELETE CASCADE" : "").append(!rsConstraints.isLast() ? "," : "").append(!rsConstraints.isLast() ? "\r\n" : "");
                }

                sbSQL.append("\r\n);\r\n");
            }
            
            return Sistema.ServiceResponse(sbCallback, sbSQL.toString(), true);
        } catch (Exception ex) {
            DBConexion.rollback();
            throw AppException.getException(ex);
        }
    }

    public static String GenerateBackup(GenerateBackupInADT objGenerateBackupInADT, String sbCallback) throws AppException {
        try {
            ResultSet rsTables = Query.getTablesUser(Configuracion.usuarioDB, false);
            ResultSet rsColumns, rsConstraints, rsDetailConstraint, rsFKDetailConstraint;
            StringBuilder sbSQL = new StringBuilder();

            while (rsTables.next()) {
                String sbTabla = rsTables.getString("table_name");

                /*
                 * GET TABLE COLUMNS
                 */
                rsColumns = Query.getTableColumns(sbTabla);

                sbSQL.append("CREATE TABLE \"").append(sbTabla).append("\" (\r\n");

                while (rsColumns.next()) {
                    String sbColumnName = rsColumns.getString("COLUMN_NAME");
                    String sbDataType = rsColumns.getString("DATA_TYPE");
                    int nuDataLength = rsColumns.getInt("DATA_LENGTH");
                    int nuDataPrecision = rsColumns.getInt("DATA_PRECISION");
                    int nuDataScale = rsColumns.getInt("DATA_SCALE");
                    boolean blNullable = rsColumns.getString("NULLABLE").equals("Y");

                    System.out.println(">sbColumnName:" + sbColumnName + ",sbDataType:" + sbDataType + ",nuDataLength:" + nuDataLength + ",nuDataPrecision:" + nuDataPrecision + ",nuDataScale:" + nuDataScale + ",blNullable:" + blNullable);

                    sbSQL.append("\t\"").append(sbColumnName).append("\" ").append(ServiceExe.FormatFieldType(sbDataType));

                    if (sbDataType.equals("?NUMBER") && nuDataPrecision > 0) {
                        sbSQL.append("(").append(nuDataPrecision).append(nuDataScale > 0 ? "," + nuDataScale : "").append(")");
                    } else if (Arrays.asList("VARCHAR2", "NVARCHAR2", "CHAR").contains(sbDataType)) {
                        sbSQL.append("(").append(nuDataLength).append(")");
                    }

                    sbSQL.append(!blNullable ? " NOT NULL" : "").append(!rsColumns.isLast() ? "," : "").append(!rsColumns.isLast() ? "\r\n" : "");
                }

                /*
                 * GET TABLE CONTRAINTS
                 */
                rsConstraints = Query.getTableConstraints(sbTabla);

                if (!rsConstraints.isAfterLast() && !rsConstraints.isBeforeFirst()) {
                    sbSQL.append("\r\n");
                }

                while (rsConstraints.next()) {
                    if (rsConstraints.isFirst() && !sbSQL.toString().endsWith(",")) {
                        sbSQL.append(",\r\n");
                    }

                    String sbConstraintName = rsConstraints.getString("CONSTRAINT_NAME");
                    String sbConstraingType = rsConstraints.getString("CONSTRAINT_TYPE");
                    String sbFKConstraintName = rsConstraints.getString("R_CONSTRAINT_NAME");
                    String sbDeleteRule = rsConstraints.getString("DELETE_RULE");
                    String sbSearchCond = rsConstraints.getString("SEARCH_CONDITION");

                    /*
                     * GET DETAIL CONSTRAINT
                     */
                    rsDetailConstraint = Query.getDetailConstraint(sbConstraintName);

                    if (!rsDetailConstraint.isAfterLast() && !rsDetailConstraint.isBeforeFirst()) {
                        System.out.println("No data for constraint [" + sbConstraintName + "]-> Omiting...");
                        continue;
                    }

                    /*
                     * GET DETAIL FOR FK CONSTRAINT
                     */
                    rsFKDetailConstraint = Query.getDetailConstraint(sbFKConstraintName);

                    if (sbConstraingType.equals("R") && !rsFKDetailConstraint.isAfterLast() && !rsFKDetailConstraint.isBeforeFirst()) {
                        System.out.println("No data for FK constraint [" + sbFKConstraintName + "]-> Omiting...");
                        continue;
                    }

                    System.out.println(">sbConstraintName:" + sbConstraintName + ",sbConstraintType:" + sbConstraingType + ",sbDeleteRule:" + sbDeleteRule + ",sbSearchCond:" + sbSearchCond);

                    sbSQL.append("\tCONSTRAINT \"").append(sbConstraintName).append("\" ");

                    String sbKeys = "";

                    if (Arrays.asList("P", "U").contains(sbConstraingType)) {
                        sbKeys = (sbConstraingType.equals("P") ? "PRIMARY KEY" : "UNIQUE") + "({$KEYS})";
                    } else if (sbConstraingType.equals("R")) {
                        sbKeys = "FOREIGN KEY ({$KEYS}) REFERENCES \"{$TABLE}\" ({$FK_KEYS})";
                    } else if (sbConstraingType.equals("C")) {
                        sbKeys = "CHECK " + sbSearchCond;
                    }

                    String sbColumns = "";
                    while (rsDetailConstraint.next()) {
                        String sbColumnName = rsDetailConstraint.getString("COLUMN_NAME");
                        sbColumns += "\"" + sbColumnName + "\"" + (!rsDetailConstraint.isLast() ? "," : "");
                    }

                    String sbFKColumns = "";
                    String sbFKTable = "";
                    while (rsFKDetailConstraint.next()) {
                        String sbColumnName = rsFKDetailConstraint.getString("COLUMN_NAME");
                        sbFKTable = rsFKDetailConstraint.getString("TABLE_NAME");
                        sbFKColumns += "\"" + sbColumnName + "\"" + (!rsFKDetailConstraint.isLast() ? "," : "");
                    }

                    sbSQL.append(sbKeys.replaceAll("\\{\\$KEYS\\}", sbColumns).replaceAll("\\{\\$FK_KEYS\\}", sbFKColumns).replaceAll("\\{\\$TABLE\\}", sbFKTable));

                    sbSQL.append(sbDeleteRule != null && sbDeleteRule.equals("CASCADE") ? " ON DELETE CASCADE" : "").append(!rsConstraints.isLast() ? "," : "").append("\r\n");
                }

                sbSQL.append("\r\n);\r\n");
            }

            String sbURLDownload = Configuracion.pathDownloadFiles + objGenerateBackupInADT.getFileName();
            PrintWriter writer = new PrintWriter(Configuracion.pathFiles + objGenerateBackupInADT.getFileName(), "UTF-8");
            writer.println(sbSQL);
            writer.close();

            return Sistema.ServiceResponse(sbCallback, sbURLDownload, true);
        } catch (Exception ex) {
            throw AppException.getException(ex);
        }
    }

    private static String FormatFieldType(String sbName) throws AppException {
        try {
            return sbName.toUpperCase().startsWith("TIMESTAMP") ? "TIMESTAMP" : sbName;
        } catch (Exception e) {
            throw AppException.getException(e);
        }
    }
}
