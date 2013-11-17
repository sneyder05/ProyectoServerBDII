package server.core.ADT;

/**
 * Sneyder Navia
 * fabiansneyder@gmail.com
 * Copyright 2013
 */
public class DeleteFieldInADT {
    private String Field;
    private String Table;
    
    public DeleteFieldInADT(){}

    public String getField() {
        return Field;
    }

    public void setField(String Field) {
        this.Field = Field;
    }

    public String getTable() {
        return Table;
    }

    public void setTable(String Table) {
        this.Table = Table;
    }

    @Override
    public String toString() {
        return "DeleteFieldInADT{" + "Field=" + Field + ", Table=" + Table + '}';
    }
}