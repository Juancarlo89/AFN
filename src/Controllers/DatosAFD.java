package Controllers;

import Models.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DatosAFD {
    private AFD afd;
    private List<String> tokens = new ArrayList<>(); 
    
    public DatosAFD(){
        afd = new AFD();
    }
    
    // Convierte varios AFN en un solo AFD  
    public AFD convertirAFNsSeleccionados(List<AFN> listaAFNs, List<String> tokens) {
        if (listaAFNs == null || listaAFNs.isEmpty()) return null;

        AFN combinado = new AFN();
        Estado nuevoInicial = new Estado();
        combinado.edoInicial = nuevoInicial;
        combinado.estados.add(nuevoInicial);

        for (int i = 0; i < listaAFNs.size(); i++) {
            AFN a = listaAFNs.get(i);
            String token = (tokens != null && i < tokens.size()) ? tokens.get(i) : "T" + (i + 1);
            //String token = tokens.get(i);

            // Enlazar con epsilon
            nuevoInicial.transiciones.add(new Transition(SimbEsp.EPSILON, a.edoInicial));

            // Unir alfabetos y estados
            combinado.estados.addAll(a.estados);
            if (a.alfabeto != null)
                combinado.alfabeto.addAll(a.alfabeto);

        
            // Marcar estados de aceptación
            for (Estado e : a.edosAcept) {
                e.token = token;
                combinado.edosAcept.addAll(a.edosAcept);
            }        
        }
    
        //System.out.println("AFN combinado -> Estados: " + combinado.estados.size() + 
        //                   ", Alfabeto: " + combinado.alfabeto);
        System.out.println("AFNs seleccionados: " + listaAFNs.size());
        System.out.println("Tokens recibidos: " + tokens.size() + " -> " + tokens);
        System.out.println("AFN combinado -> Estados: " + combinado.estados.size() +
                       ", Alfabeto: " + combinado.alfabeto +
                       ", Aceptación: " + combinado.edosAcept.size());

        // Conversión a AFD
        AFD afdFinal = new AFD().AFNtoAFD(combinado);
        afdFinal.setAlfabeto(combinado.alfabeto);
        afdFinal.setTokens(tokens);
    
        System.out.println("AFD final -> Estados:" + afdFinal.getNumEdos() + "  Alfabeto" + afdFinal.getAlfabeto());
        return afdFinal;
}
    public boolean cargarAFD(String ruta){
        return afd.loadAFD(ruta);
    }
    
    /*public List<String[]> analizarExpresion(String sigma) {
        if (afd == null) {
            System.err.println("Error: no hay AFD cargado.");
            return Collections.emptyList();
        }
        return afd.analizar(sigma);
    }*/
    public List<String[]> analizarExpresion(String sigma, String rutaAFD) {
    List<String[]> resultados = new ArrayList<>();

    AnalizadorLexico analizador = new AnalizadorLexico(sigma, rutaAFD);
    int token;

    while ((token = analizador.yylex()) != SimbEsp.FIN) {
        String lexema = analizador.lexema;
        String clase;

        if (token == SimbEsp.ERROR) {
            clase = "ERROR";
        } else {
            clase = "Token " + token;
        }

        resultados.add(new String[]{lexema, clase});
    }

    return resultados;
}

}
