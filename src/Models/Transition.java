package Models;

import java.util.Set;


public class Transition {
    public char simbInf;
    public char simbSup;
    public Estado edoDestino;
    public Set<Estado> estados;

    public Transition() {
        this.simbInf = 0;
        this.simbSup = 0;
        this.edoDestino = null;
    }

    public Transition(char c, Estado e) {
        this.simbInf = c;
        this.simbSup = c;
        this.edoDestino = e;
    }

    public Transition(char cInf, char cSup, Estado e) {
        this.simbInf = cInf;
        this.simbSup = cSup;
        this.edoDestino = e;
    }

    public boolean acepta(char c) {
        return c >= simbInf && c <= simbSup;
    }
    
    public char getSimbInf() {
        return simbInf;
    }

    public char getSimbSup() {
        return simbSup;
    }

    public Estado getedoDestino() {
        return edoDestino;
    }
    
    public Estado getEstado(){
        return (Estado) estados;
    }
    
    @Override
    public Object clone() {
        try {
            return super.clone(); // Llama al clone() de Object
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // No debería ocurrir
        }
    }
}
