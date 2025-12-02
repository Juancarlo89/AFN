package Models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class EdoAFD {
    public boolean esAceptacion;
    public String token;
    //public int idtoken = Integer.parseInt(token);
    public Map<Character, Integer> transiciones;
    int[] Trans;
    int ID;

    public EdoAFD() {
        Trans = new int[257];
        ID = -1;
        for(int i=0; i<=256 ; i++){
            Trans[i]=-1;
        }
    }
    
    EdoAFD(int idEdo) {
        Trans = new int[257];
        ID = idEdo;
        for (int i = 0; i < 256; i++)
            Trans[i] = -1;
    }
    
     public EdoAFD(int ID, Set<Estado> conjunto) {
        this.ID = ID;
        this.transiciones = new HashMap<>();
        this.esAceptacion = false;
    }

    public void addTransicion(char c, int destino) {
        transiciones.put(c, destino);
    }
    
    @Override
    public String toString() {
        return "Transiciones: " + transiciones + (esAceptacion ? " [ACEPT] token=" + token : "");
    }
}
