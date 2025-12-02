package Models;

import java.util.Map;
import java.util.Stack;

public class AnalizadorLexico {
    int token, edoActual, edoTransicion;
     String cadenaSigma;
     public String lexema;
     boolean PasoPorEdoAcept;
     int IniLexema, FinLexema, IndiceCaracterActual;
     char caracterActual;
     Stack<Integer> Pila = new Stack<>();
     AFD AutomataFD;
     
     public AnalizadorLexico(){
         cadenaSigma = "";
         PasoPorEdoAcept = false;
         IniLexema = FinLexema = -1;
         IndiceCaracterActual = -1;
         token = -1;
         AutomataFD = null;
         
     }
     
     public AnalizadorLexico(String sigma, String FileAFD) {
        this.cadenaSigma = sigma;
        this.PasoPorEdoAcept = false;
        this.IniLexema = 0;
        this.FinLexema = -1;
        this.IndiceCaracterActual = 0;
        this.token = -1;
        
        // Forma correcta de cargar el AFD
        try {
            this.AutomataFD = new AFD();
            boolean cargado = this.AutomataFD.loadAFD(FileAFD);
            if (!cargado) {
                System.err.println("Error al cargar el AFD desde archivo: " + FileAFD);
                this.AutomataFD = null;
            }
        } catch (Exception e) {
            System.err.println("Ocurrió un error " + e.getMessage());
            this.AutomataFD = null; // Asegurarse de que el autómata es nulo si hay error
        }
    }
     
     public void SetSigma(String sigma){
         cadenaSigma = sigma;
         PasoPorEdoAcept = false;
         IniLexema = 0;
         FinLexema = -1;
         IndiceCaracterActual = 0;
         token = -1;
     }
     
     public String CadenaXAnalizar(){
         return cadenaSigma.substring(IndiceCaracterActual, cadenaSigma.length());
     }
     
     public int yylex() {
        if (AutomataFD == null) {
            lexema = "";
            return SimbEsp.ERROR;
        }
        Pila.push(IndiceCaracterActual);
        
        // Reiniciar para el nuevo token
        IniLexema = IndiceCaracterActual;
        edoActual = 0; // Siempre empezamos en el estado 0 del AFD
        PasoPorEdoAcept = false;
        FinLexema = -1;
        token = -1;

        //Si ya llegó al final de la cadena
        if (IndiceCaracterActual >= cadenaSigma.length()) {
            lexema = "";
            return SimbEsp.FIN;
        }

        // Bucle para encontrar la coincidencia más larga
        while (IndiceCaracterActual < cadenaSigma.length()) {
            caracterActual = cadenaSigma.charAt(IndiceCaracterActual);
            
            // SINTAXIS CORRECTA para acceder a la tabla de transiciones
            //edoTransicion = AutomataFD.edosAFD[edoActual].Trans[(int)caracterActual];
            Map<Character, Integer> mapa = AutomataFD.transiciones.get(edoActual);
            if (mapa == null || !mapa.containsKey(caracterActual)) {
                edoTransicion = -1;
            } else {
                edoTransicion = mapa.get(caracterActual);
            }
            
            if (edoTransicion == -1) { // Si no hay transición (callejón sin salida)
                break; // Rompemos el bucle
            }
            
            edoActual = edoTransicion; // Avanzamos al siguiente estado
            
            // SINTAXIS CORRECTA para verificar si el nuevo estado es de aceptación
            /*if (AutomataFD.edosAFD[edoActual].idtoken != -1) {
                PasoPorEdoAcept = true;
                token = AutomataFD.edosAFD[edoActual].idtoken; // Guardamos el token encontrado
                FinLexema = IndiceCaracterActual; // Guardamos la posición del último carácter aceptado
            }*/
            if (AutomataFD.estadosAceptacion.containsKey(edoActual)) {
                PasoPorEdoAcept = true;
                token = AutomataFD.estadosAceptacion.get(edoActual);
                FinLexema = IndiceCaracterActual;
            }
            
            IndiceCaracterActual++;
        }

        if (!PasoPorEdoAcept) { // Nunca pasamos por un estado de aceptación
            IndiceCaracterActual = IniLexema + 1; // Avanzamos un carácter
            if(IniLexema >= cadenaSigma.length()){
                lexema = "";
            }else if(IndiceCaracterActual > cadenaSigma.length()){
                lexema = cadenaSigma.substring(IniLexema);
            }else{
                lexema = cadenaSigma.substring(IniLexema, IndiceCaracterActual); // El lexema es el carácter erróneo
            }
            Pila.pop();
            return SimbEsp.ERROR;
        }
        
        // Regla de la coincidencia más larga: retrocedemos al último estado de aceptación
        IndiceCaracterActual = FinLexema + 1;
        lexema = cadenaSigma.substring(IniLexema, FinLexema + 1); 
        
        // Si el token es para omitir (espacios, comentarios), busca el siguiente
        if (token == SimbEsp.OMITIR) {
            return yylex(); // Llamada recursiva para obtener el siguiente token válido
        }
        
        return token; // Devuelve el token encontrado
    }
     
     public String yytext(){
         return lexema;
     }
     
    public boolean undoToken(){
         if(Pila.isEmpty()){
             return false;
         }
         IndiceCaracterActual = Pila.pop();
         return true;
    }
}
