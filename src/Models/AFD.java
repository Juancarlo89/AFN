package Models;

import java.io.*;
import java.util.*;

public class AFD extends AFN{
    public EdoAFD[] edosAFD;
    List<EdoAFD> listaEstadosAFD = new ArrayList<>();
    private Set<Character> alfabeto;
    private Set<Character> alfabetoAFD = new HashSet<>();
    private int numEdos;
    private List<String> tokens = new ArrayList<>();
    private List<Map<Character, Integer>> tablaTransiciones = new ArrayList<>();
    public Map<Integer, Map<Character, Integer>> transiciones;
    public Map<Integer, Integer> estadosAceptacion; // Estado -> token

    // ----- Constructores -----

    public AFD() {
        this.numEdos = 0;
        this.alfabeto = new HashSet<>();
        this.transiciones = new HashMap<>();
        this.estadosAceptacion = new HashMap<>();
    }

    public AFD(int n) {
        this.edosAFD = new EdoAFD[n];
        this.numEdos = n;
        this.alfabeto = new HashSet<>();
    }

    public AFD(int n, Set<Character> alf) {
        this.edosAFD = new EdoAFD[n];
        this.numEdos = n;
        this.alfabeto = new HashSet<>(alf);
    }
    
    public class EstadoAFD {
        public int[] Trans;
        public int idtoken;
    }

    // ----- Métodos para guardar y cargar el AFD -----

    // Guarda el AFD en un archivo de texto.
    public boolean saveAFD(String nameFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nameFile))) {
                      
            // --- Encabezado general ---
        writer.write("======= AFD =======");
        writer.newLine();
        writer.newLine();

        // --- Alfabeto ---
        writer.write("Alfabeto: " + getAlfabeto().toString());
        writer.newLine();

        // --- Número de estados ---
        writer.write("Número de Estados: " + numEdos);
        writer.newLine();

        writer.newLine();

        // --- Estados ---
        writer.write("== Estados del AFD ==");
        writer.newLine();
        if (edosAFD != null) {
            for (int i = 0; i < numEdos; i++) {
                /*if (edosAFD[i] != null) {
                    writer.write("Estado " + i + ": " + edosAFD[i].toString());
                    writer.newLine();
                }*/
                EdoAFD estadoActual = edosAFD[i];
        
                if (estadoActual != null) {
                    // 1. Obtener las transiciones de la tabla central (AFD)
                    Map<Character, Integer> transicionesDelEstado = null;
            
                    // Verificación de índice seguro 
                    if (i < this.tablaTransiciones.size()) {
                        transicionesDelEstado = this.tablaTransiciones.get(i);
                    }
            
                   // 2. Formatear la salida del estado
                    StringBuilder sb = new StringBuilder();
                    sb.append("Estado ").append(i).append(": ");
            
                    // 3. Imprimir transiciones usando la tabla central
                    sb.append("Transiciones: ");
                    sb.append(transicionesDelEstado != null ? transicionesDelEstado.toString() : "{}");
            
                    // 4. Añadir información de aceptación usando el toString del EdoAFD para esa parte
                    if (estadoActual.esAceptacion) {
                        sb.append(" [ACEPT] token=").append(estadoActual.token);
                    }
            
                    writer.write(sb.toString());
                    writer.newLine();
                }
            }
        }

        writer.newLine();

        // --- Tokens asociados ---
        if (tokens != null && !tokens.isEmpty()) {
            writer.write("== Tokens Asociados ==");
            writer.newLine();
            for (int i = 0; i < tokens.size(); i++) {
                writer.write("AFN " + (i + 1) + " → Token: " + tokens.get(i));
                writer.newLine();
            }
        }
        
        writer.newLine();

        // --- Bloque para la TABLA DE TRANSICIONES ---
        writer.write("== TABLA DE TRANSICIONES ==");
        writer.newLine();

        if (numEdos > 0) {
            // 1. Preparar Cabecera: [Estado | Símbolo 1 | Símbolo 2 | ... | Aceptación]
            List<Character> simbolos = new ArrayList<>(this.alfabeto);
            Collections.sort(simbolos); // Ordenar para consistencia

            StringBuilder header = new StringBuilder("Estado");
            for (char c : simbolos) {
                header.append("\t").append(c);
            }
            header.append("\tAceptación/Token");
            writer.write(header.toString());
            writer.newLine();
            writer.write("-------------------------------------------------------");
            writer.newLine();

            // 2. Llenar la Tabla: una fila por estado
            for (int i = 0; i < numEdos; i++) {
                EdoAFD estadoActual = edosAFD[i];
        
                // Obtener transiciones para el estado 'i'
                Map<Character, Integer> transicionesDelEstado = 
                    (i < this.tablaTransiciones.size()) ? this.tablaTransiciones.get(i) : Collections.emptyMap();
        
                StringBuilder fila = new StringBuilder("S" + i);

                // Imprimir el estado destino para cada símbolo
                for (char c : simbolos) {
                    fila.append("\t");
                    if (transicionesDelEstado.containsKey(c)) {
                        fila.append("S").append(transicionesDelEstado.get(c)); // S1, S2, etc.
                    } else {
                        fila.append("-"); // Símbolo para 'no transición'
                    }
                }

                // 3. Imprimir Aceptación/Token
                fila.append("\t");
                if (estadoActual.esAceptacion) {
                    fila.append(estadoActual.token);
                } else {
                    fila.append("-");
                }
        
                writer.write(fila.toString());
                writer.newLine();
            }
        } else {
            writer.write("No hay estados generados.");
            writer.newLine();
        }

        writer.newLine();
        writer.write("======= Fin del archivo =======");
        writer.newLine();

        return true;

        } catch (IOException e) {
            System.err.println("Error al guardar el AFD: " + e.getMessage());
            return false;
        }
    }

    //Carga el AFD desde un archivo de texto.   
    public boolean loadAFD(String nameFile) {
    try (BufferedReader reader = new BufferedReader(new FileReader(nameFile))) {
        String line;
        boolean leyendoTransiciones = false;
        List<Character> tablaSimbolos = null; // orden de columnas de la tabla final

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Leer alfabeto
            if (line.startsWith("Alfabeto:")) {
                int idx1 = line.indexOf('[');
                int idx2 = line.indexOf(']');
                if (idx1 != -1 && idx2 != -1 && idx2 > idx1) {
                    String contenido = line.substring(idx1 + 1, idx2);
                    for (String c : contenido.split(",")) {
                        c = c.trim();
                        if (!c.isEmpty()) {
                            alfabeto.add(c.charAt(0));
                        }
                    }
                }
            }

            // Número de estados
            else if (line.startsWith("Número de Estados:")) {
                String[] parts = line.split(":");
                if (parts.length > 1) {
                    numEdos = Integer.parseInt(parts[1].trim());
                }
            }

            // Estados con sus transiciones (bloque "Estado X: {...} [ACEPT] token=..")
            else if (line.startsWith("Estado")) {
                // extraer id entre "Estado " y ":"
                int colon = line.indexOf(':');
                if (colon == -1) continue;
                String idStr = line.substring(7, colon).trim(); // "Estado " tiene 7 char
                int id = Integer.parseInt(idStr);

                // asegúrate de tener un mapa para ese estado
                Map<Character, Integer> mapaTrans = transiciones.get(id);
                if (mapaTrans == null) {
                    mapaTrans = new HashMap<>();
                    transiciones.put(id, mapaTrans);
                }

                // extraer contenido {...}
                int idx1 = line.indexOf('{');
                int idx2 = line.indexOf('}');
                if (idx1 != -1 && idx2 != -1 && idx2 > idx1 + 1) {
                    String contenido = line.substring(idx1 + 1, idx2);
                    String[] pares = contenido.split(",");

                    for (String p : pares) {
                        p = p.trim();
                        if (p.isEmpty()) continue;

                        int sep = p.lastIndexOf('=');
                        if (sep == -1) continue;

                        String simbolo = p.substring(0, sep).trim();
                        String destinoStr = p.substring(sep + 1).trim();

                        if (simbolo.isEmpty() || destinoStr.isEmpty()) continue;

                        try {
                            int destino = Integer.parseInt(destinoStr);
                            if (simbolo.length() == 1) {
                                mapaTrans.put(simbolo.charAt(0), destino);
                            } else {
                                // símbolo múltiple (lo indicamos pero no lo agregamos al mapa de char simple)
                                System.out.println("Símbolo múltiple detectado en Estado " + id + ": '" + simbolo + "' -> " + destino);
                            }
                        } catch (NumberFormatException ex) {
                            System.err.println("Error parseando transición: " + p);
                        }
                    }
                }

                // Verificar si es estado de aceptación en esta línea
                if (line.contains("[ACEPT]") && line.contains("token=")) {
                    int tokIdx = line.indexOf("token=");
                    String tokenStr = line.substring(tokIdx + 6).trim();
                    try {
                        int token = Integer.parseInt(tokenStr);
                        // Solo poner el token si no existía (no sobrescribir)
                        estadosAceptacion.putIfAbsent(id, token);
                    } catch (NumberFormatException ex) {
                        System.err.println("Token inválido en estado " + id + ": " + tokenStr);
                    }
                }
            }

            // Inicio de la tabla de transiciones final
            else if (line.startsWith("== TABLA DE TRANSICIONES ==") || line.startsWith("==TABLA DE TRANSICIONES==")) {
                leyendoTransiciones = true;
                // Leer línea de encabezado (puede estar en la siguiente línea)
                String header = reader.readLine();
                if (header == null) break;
                header = header.trim();
                // el header puede tener tabs; lo usamos para definir el orden de símbolos
                String[] headerParts = header.split("\t");
                tablaSimbolos = new ArrayList<>();
                // asumimos que headerParts[0] es "Estado" o similar, y que la última columna es "Aceptación/Token"
                for (int i = 1; i < headerParts.length; i++) {
                    String h = headerParts[i].trim();
                    if (h.equalsIgnoreCase("Aceptación/Token") || h.equalsIgnoreCase("Aceptación/Token") || h.toLowerCase().contains("acept")) {
                        break;
                    }
                    if (!h.isEmpty()) {
                        // tomar el primer caracter del nombre de la columna como símbolo
                        tablaSimbolos.add(h.charAt(0));
                    }
                }
                // saltar la línea de separación si existe
                reader.readLine();
            }

            // Leer filas de la tabla (solo si se detectó la sección)
            else if (leyendoTransiciones && (line.startsWith("S") || line.startsWith("s"))) {
                // dividir por tabs; si el archivo usa espacios, podría necesitar split("\\s+")
                String[] partes = line.split("\t");
                if (partes.length < 1) continue;

                String estadoStr = partes[0].trim(); // p.ej "S0"
                int estado;
                try {
                    estado = Integer.parseInt(estadoStr.substring(1));
                } catch (Exception ex) {
                    continue;
                }

                // Asegurar mapa de transiciones del estado (no sobreescribir)
                Map<Character, Integer> mapaTrans = transiciones.get(estado);
                if (mapaTrans == null) {
                    mapaTrans = new HashMap<>();
                    transiciones.put(estado, mapaTrans);
                }

                // recorrer columnas según tablaSimbolos
                int col = 1;
                for (int i = 0; i < (tablaSimbolos != null ? tablaSimbolos.size() : 0); i++) {
                    if (col >= partes.length) break;
                    String destino = partes[col++].trim();
                    char simbolo = tablaSimbolos.get(i);
                    if (!destino.equals("-") && !destino.equals("-1") && !destino.isEmpty()) {
                        // si apunta a Sx
                        if (destino.startsWith("S") || destino.startsWith("s")) {
                            try {
                                int destEstado = Integer.parseInt(destino.substring(1));
                                // solo insertar si no existe ya una transición definida (priorizar bloque Estado)
                                mapaTrans.putIfAbsent(simbolo, destEstado);
                            } catch (NumberFormatException ex) {
                                // ignorar
                            }
                        } else {
                            // si la tabla usa números directos en lugar de Sx
                            try {
                                int destEstado = Integer.parseInt(destino);
                                mapaTrans.putIfAbsent(simbolo, destEstado);
                            } catch (NumberFormatException ex) {
                                // ignorar
                            }
                        }
                    }
                }

                // última columna puede ser token
                String ultima = partes[partes.length - 1].trim();
                if (!ultima.equals("-") && !ultima.equals("-1") && !ultima.isEmpty()) {
                    try {
                        int token = Integer.parseInt(ultima);
                        // guardar token SOLO si no existe (no sobrescribir la info de "Estado" más fiable)
                        estadosAceptacion.putIfAbsent(estado, token);
                    } catch (NumberFormatException ex) {
                        // ignorar
                    }
                }
            }
        }

        System.out.println("AFD cargado correctamente.");
        System.out.println("Estados: " + numEdos);
        System.out.println("Alfabeto: " + alfabeto);
        System.out.println("Estados de aceptación: " + estadosAceptacion);

        return true;

    } catch (IOException e) {
        System.err.println("Error al cargar el AFD: " + e.getMessage());
        return false;
    }
}

    
    //Convertir AFN a AFD
    public AFD AFNtoAFD(AFN a) {
        AFD afd_conv = new AFD();
        
        for (char c : a.alfabeto) {
            if (c != SimbEsp.EPSILON) {
                afd_conv.alfabeto.add(c);
            }
        }
        
        //Elementos de almacenaje para el AFD
        Set<ElemSj> C = new HashSet<>();
        Queue<ElemSj> Q = new LinkedList<>();
        int NumElemSj = 0;
        List<TransicionAFD> transicionesPendientes = new ArrayList<>();
        //List<EdoAFD> tablatemp = new ArrayList();
        
        ElemSj SjAux = new ElemSj();
        ElemSj SjAct;
        
        // Cerradura epsilon del estado inicial del AFN
        SjAux.S = cerraduraEpsilon(a.edoInicial);
        SjAux.id = NumElemSj++;  // S0
                
        C.add(SjAux);
        Q.add(SjAux);
                       
        listaEstadosAFD.add(new EdoAFD(SjAux.id ,SjAux.S));
        
        while (!Q.isEmpty()) {
            SjAct = Q.poll(); // DeQueue tiene subconjunto de estados y id
            
            for (char c : a.alfabeto) {
                if (c == SimbEsp.EPSILON) { 
                    continue; //Ignorar epsilon
                }
                
                ElemSj nuevoSj = new ElemSj();
                ElemSj stemp = new ElemSj();
                stemp.S = ira(SjAct.S, c);
                //nuevoSj.S = ira(SjAct.S, c);//se calcula el ira de todos 
                nuevoSj.S = cerraduraEpsilon(stemp.S);//cerradura epsilon después del ira
                
                if (nuevoSj.S.isEmpty()) {
                    continue; // No hay transición para este símbolo
                }
                
                // Verificar si este conjunto ya existe en C
                int found = Search(C, nuevoSj);
                //ElemSj foundElement = Search(C, nuevoSj);
                int idDestino;
                
                if (found == -1) {
                    nuevoSj.id = NumElemSj++;
                    Q.add(nuevoSj);
                    C.add(nuevoSj);
                    //afd_conv.edosAFD[afd_conv.numEdos++] = new EdoAFD(nuevoSj.id, nuevoSj.S);
                    listaEstadosAFD.add(new EdoAFD(nuevoSj.id, nuevoSj.S));
                    idDestino = nuevoSj.id;
                }else {
                    List<ElemSj> listaC = new ArrayList<>(C);
                    ElemSj elemExist = listaC.get(found);
                    idDestino = elemExist.id;
                }
                                               
                // Registrar la transición SjAct --c--> nuevoSj
                //afd_conv.agregarTransicion(SjAct.id, c, nuevoSj.id);
                transicionesPendientes.add(new TransicionAFD(SjAct.id, c, idDestino));
            }
            
            if (listaEstadosAFD.size() > 10000) {
            System.out.println("Demasiados estados generados, posible bucle infinito");
            break;
            } 
        }
        
        afd_conv.edosAFD = listaEstadosAFD.toArray(new EdoAFD[0]);
        afd_conv.numEdos = listaEstadosAFD.size();
        
        for (TransicionAFD t : transicionesPendientes) {
            afd_conv.agregarTransicion(t.origen, t.simbolo, t.destino);
        }
        //identificar edos de acept para ponerles los tokens
        // --- Marcar estados de aceptación ---
        /*for (ElemSj elem : C) {
            for (Estado e : elem.S) {
                if (a.edosAcept.contains(e)) {
                    afd_conv.marcarAceptacion(elem.id, e.token);
                    break;
                }
            }
        }*/
        // --- Marcar estados de aceptación con prioridad ---
for (ElemSj elem : C) {
    int tokenAsignado = -1;
    
    // 1. Encontrar el token de aceptación de MÁXIMA PRIORIDAD (el valor más bajo)
    for (Estado e : elem.S) {
        if (a.edosAcept.contains(e)) {
            // Inicializar el token asignado si es el primero encontrado
            if (tokenAsignado == -1 ||Integer.valueOf(e.token) < tokenAsignado) {
                tokenAsignado = Integer.valueOf(e.token);
            }
        }
    }
    
    // 2. Si se encontró un token, marcar el estado del AFD
    if (tokenAsignado != -1) {
        // En tu AFD, las palabras reservadas tienen menor valor (10, 20, 30)
        // que los operadores y números (40, 50, 60... 100).
        // Esto le da prioridad a las palabras reservadas.
        afd_conv.marcarAceptacion(elem.id, String.valueOf(tokenAsignado));
    }
}
        
        System.out.println("AFD final -> Estados: " + afd_conv.getNumEdos() + 
                       ", Alfabeto: " + a.alfabeto);
        
        //afd_conv.numEdos = NumElemSj;
        return afd_conv;
    }

    // ----- Getters y Setters -----

    public EdoAFD[] getEdosAFD() {
        return edosAFD;
    }

    public void setEdosAFD(EdoAFD[] edosAFD) {
        this.edosAFD = edosAFD;
    }

    public Set<Character> getAlfabeto() {
        return alfabeto;
    }

    public void setAlfabeto(Set<Character> alfabeto) {
        this.alfabeto = alfabeto;
    }
    
    public void getAlfabeto(Set<Character> alfabet){
        if (alfabeto == null) this.alfabetoAFD = new HashSet<>();
        else this.alfabetoAFD = new HashSet<>(alfabeto);
    }

    public int getNumEdos() {
        return (int) numEdos;
    }

    public void setNumEdos(int numEdos) {
        this.numEdos = numEdos;
    }

    private int Search(Set<ElemSj> conjunto, ElemSj elemento) {
        int i = 0;
        for (ElemSj e : conjunto) {
            //Comparar estados del conjunto 
            if (e.S.equals(elemento.S)) {
                return i;//encontrado
            }
            i++;
        }
        return -1   ;//no encontrado
    }

    private void agregarTransicion(int id, char c, int id0) {
        // 1. Asegurar el crecimiento de la tabla
        while (tablaTransiciones.size() <= id) {
            tablaTransiciones.add(new HashMap<>()); // Añade un mapa vacío
        }
        // 2. Obtener el mapa de transiciones (ahora el índice existe)
        Map<Character, Integer> transicionesEstado = tablaTransiciones.get(id);
    
        // 3. Añadir la transición.
        transicionesEstado.put(c, id0);
    }
    
    //Geters y Seters de la clase para los Tokens 
    public void setTokens(List<String> tokens){
        this.tokens = tokens;
    }
    
    public List<String> getTokens(){
        return tokens; 
    }
    
    /*public void setAlfabeto2(Set<Character> alfabeto) {
        this.alfabeto = new HashSet<>(alfabeto);
    }*/

    /*public Set<Character> getAlfabeto2() {
        return this.alfabeto;
    }*/
    public void marcarAceptacion(int id, String token) {
        if (edosAFD[id] != null) {
            edosAFD[id].esAceptacion = true;
            edosAFD[id].token = token;
        }
    }
    
        
    public List<String[]> analizar(String cadena) {
    List<String[]> resultado = new ArrayList<>();

    int inicio = 0;
    while (inicio < cadena.length()) {
        int estadoActual = 0; // estado inicial
        int ultimoAcept = -1; // último estado de aceptación alcanzado
        int finAcept = -1;    // índice del último carácter aceptado
        int i = inicio;

        while (i < cadena.length()) {
            char c = cadena.charAt(i);
            Map<Character, Integer> mapa = transiciones.get(estadoActual);

            if (mapa == null || !mapa.containsKey(c)) {
                break; // no hay transición: se detiene el avance
            }

            estadoActual = mapa.get(c);

            // Si el estado actual es de aceptación, lo guardamos
            if (estadosAceptacion.containsKey(estadoActual)) {
                ultimoAcept = estadoActual;
                finAcept = i;
            }

            i++;
        }

        if (ultimoAcept != -1) {
            // Token válido encontrado
            String lexema = cadena.substring(inicio, finAcept + 1);
            int token = estadosAceptacion.get(ultimoAcept);
            resultado.add(new String[]{String.valueOf(token), lexema});
            inicio = finAcept + 1; // avanzar después del lexema reconocido
        } else {
            // Ningún estado de aceptación alcanzado → error léxico
            resultado.add(new String[]{"ERROR", String.valueOf(cadena.charAt(inicio))});
            inicio++; // avanzar un carácter y continuar
        }
    }

    return resultado;
}


    
}
