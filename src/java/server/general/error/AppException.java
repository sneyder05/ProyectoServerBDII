/*
 * Propiedad Intelectual Play Tech.
 */
package server.general.error;

public class AppException extends Exception {
    private static final long serialVersionUID = -5213793666303258030L;
    private int nuCodigoError;
    private String sbParametrosError;

    private AppException(int nuNewCodigoError) {
        nuCodigoError = nuNewCodigoError;
        sbParametrosError = new String();
    }

    private AppException(int nuNewCodigoError, String sbNewParametros) {
        nuCodigoError = nuNewCodigoError;
        sbParametrosError = sbNewParametros;
    }

    public static AppException getException(int nuCodigoError) {
        return new AppException(nuCodigoError);
    }

    public static AppException getException(int nuCodigoError, String sbParametrosError) {
        return new AppException(nuCodigoError, sbParametrosError);
    }

    public int getCodigoError() {
        return nuCodigoError;
    }

    public String getParametrosError() {
        return sbParametrosError;
    }

    public static AppException getException(Exception e) {
        if ((e.getClass().getName()).equals(AppException.class.getName())) {
            return (AppException) e;
        };
        
        e.printStackTrace();
        
        System.out.println(e.getMessage());
        
        return new AppException(10, e.toString());
    }
}
