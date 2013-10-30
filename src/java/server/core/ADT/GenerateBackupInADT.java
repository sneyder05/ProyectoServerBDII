package server.core.ADT;

/**
 * Sneyder Navia
 * fabiansneyder@gmail.com
 * Copyright 2013
 */
public class GenerateBackupInADT {
    private String FileName;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String FileName) {
        this.FileName = FileName;
    }
    
    public GenerateBackupInADT(){}

    @Override
    public String toString() {
        return "GenerateBackupInADT{" + "FileName=" + FileName + '}';
    }
}