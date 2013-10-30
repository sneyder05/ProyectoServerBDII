package server.core;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import server.general.error.AppException;

@Startup
@Singleton
public class Init {
    @PostConstruct
    void Init(){
        System.out.println("PostConstruct Server");
        try {
            AppWebService.initialize();
        } catch (AppException ex) {
            ex.printStackTrace();
        }
    }
}
