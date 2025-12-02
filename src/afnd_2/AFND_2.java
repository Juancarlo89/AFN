package afnd_2;

import Controllers.*;
import Views.*;

public class AFND_2 {
    
    public static void main(String[] args) {
        DatosAFN datos = new DatosAFN();
        Principal mainPage = new Principal(datos);
        mainPage.setVisible(true);    
    }
    
}
