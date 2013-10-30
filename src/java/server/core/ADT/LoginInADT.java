package server.core.ADT;

/**
 * Sneyder Navia
 * fabiansneyder@gmail.com
 * Copyright 2013
 */
public class LoginInADT {
    private String Login, Password, DB;

    public String getDB() {
        return DB;
    }

    public void setDB(String DB) {
        this.DB = DB;
    }
    
    public LoginInADT(){}

    public String getLogin() {
        return Login;
    }

    public void setLogin(String Login) {
        this.Login = Login;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    @Override
    public String toString() {
        return "LoginInADT{" + "Login=" + Login + ", Password=" + Password + ", DB=" + DB + '}';
    }
}