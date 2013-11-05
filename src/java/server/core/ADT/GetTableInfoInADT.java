package server.core.ADT;

/**
 * Sneyder Navia
 * fabiansneyder@gmail.com
 * Copyright 2013
 */
public class GetTableInfoInADT {
    private String Table;
    
    public GetTableInfoInADT(){}

    public String getTable() {
        return Table;
    }

    public void setSbTable(String Table) {
        this.Table = Table;
    }

    @Override
    public String toString() {
        return "GetTableInfoInADT{" + "Table=" + Table + '}';
    }
}