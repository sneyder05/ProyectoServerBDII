package server.core;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import server.general.error.AppException;

@Startup
@Singleton
public class Init {
    @PostConstruct
    void Init() throws AppException{
        System.out.println("PostConstruct Server");
        AppWebService.initialize();
    }
}
