package Models;


public class SimbEsp {
    public static final char EPSILON = (char) 5;
    public static final char FIN = (char) 0;
    public static final int ERROR = 2000;
    public static final int OMITIR = 2001;
    public static final int ID = 10;
    public static final int NUM = 20;
    public static final int IF = 30;
    
    public static int obtenerCodigoToken(String nombreToken) {
        switch (nombreToken.toUpperCase()) {
            case "ID": return ID;
            case "NUM": return NUM;
            case "IF": return IF;
            case "OMITIR": return OMITIR;
            case "FIN": return FIN;
            default: return ERROR; // Si no existe el token
        }
    }
}
