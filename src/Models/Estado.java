package Models;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Estado extends Transition{
    public Set<Transition> transiciones;
    public boolean esAceptacion;
    public String token;
    //public int idtoken = Integer.parseInt(token);
    public int id;

    public Estado() {
        transiciones = new HashSet<>();
        esAceptacion = false;
    }

    public Estado(boolean esAceptacion) {
        transiciones = new HashSet<>();
        this.esAceptacion = esAceptacion;
    }
    
    public Set<Estado> tieneTransicion(char c) {
        Set<Estado> R = new HashSet<>();
        
        // Recorrer todas las transiciones de este estado
        for (Transition t : transiciones) {
            if (t.getSimbInf() <= c && c <= t.getSimbSup()) {
                R.add(t.getedoDestino());
            }
        }
        
        return R;
    }
    
    // Dentro de la clase Estado
    public Estado cloneEdo() {
        Estado nuevoEstado = new Estado(); // Suponiendo un constructor vacío
        nuevoEstado.id = this.id; // Clonar primitivos
        nuevoEstado.esAceptacion = this.esAceptacion;
        
        // IMPORTANTE: Clonar la lista de transiciones
        nuevoEstado.transiciones = new HashSet<>();
        for (Transition t : this.transiciones) {
            nuevoEstado.transiciones.add((Transition) t.clone()); // Clonar cada Transicion
        }
        return nuevoEstado;
    }

    public void agregarTransicion(Transition t) {
        transiciones.add(t);
    }
}
