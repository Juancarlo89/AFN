package Models;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class AFN {
    public Set<Estado> estados;
    public Estado edoInicial;
    public Set<Character> alfabeto;
    public Set<Estado> edosAcept;
    public String operador;
    private java.util.List<AFN> subAFNs = new ArrayList<>();

    public AFN() {
        estados = new HashSet<>();
        alfabeto = new HashSet<>();
        edosAcept = new HashSet<>();
    }

    public AFN crearBasico(char c) {
        Estado e1 = new Estado();
        Estado e2 = new Estado();
        estados.add(e1);
        estados.add(e2);

        edoInicial = e1;
        e1.transiciones.add(new Transition(c, e2));

        e2.esAceptacion = true;
        edosAcept.add(e2);
        alfabeto.add(c);
        
        //this.alfabeto = new HashSet<>();
        this.alfabeto.add(c);
        
        this.operador = "";

        return this;
    }

    public AFN crearBasico(char c1, char c2) {
        Estado e1 = new Estado();
        Estado e2 = new Estado();
        estados.add(e1);
        estados.add(e2);

        edoInicial = e1;
        e1.transiciones.add(new Transition(c1, c2, e2));

        e2.esAceptacion = true;
        edosAcept.add(e2);

        for (char c = c1; c <= c2; c++) alfabeto.add(c);
        
        /*this.alfabeto = new HashSet<>();
        this.alfabeto.add(c1);
        this.alfabeto.add(c2);*/
        this.operador = "";

        return this;
    }

    public AFN unir(AFN f2) {
        Estado e1 = new Estado();
        Estado e2 = new Estado();

        e1.transiciones.add(new Transition(SimbEsp.EPSILON, this.edoInicial));
        e1.transiciones.add(new Transition(SimbEsp.EPSILON, f2.edoInicial));

        for (Estado e : this.edosAcept) {
            e.transiciones.add(new Transition(SimbEsp.EPSILON, e2));
            e.esAceptacion = false;
        }

        for (Estado e : f2.edosAcept) {
            e.transiciones.add(new Transition(SimbEsp.EPSILON, e2));
            e.esAceptacion = false;
        }

        e2.esAceptacion = true;

        // --- Crear AFN compuesto ---
        AFN resultado = new AFN();
        resultado.subAFNs = new ArrayList<>();
        resultado.subAFNs.add(this.cloneAFN());
        resultado.subAFNs.add(f2.cloneAFN());
        resultado.operador = "|";
        
        // --- Actualizar información de estructura ---
        resultado.alfabeto.addAll(this.alfabeto);
        resultado.alfabeto.addAll(f2.alfabeto);
        resultado.estados.addAll(this.estados);
        resultado.estados.addAll(f2.estados);
        resultado.edoInicial = e1;
        resultado.edosAcept.add(e2);

        return resultado;
    }

    public AFN concatenar(AFN f2) {
        for (Estado e : this.edosAcept) {
            e.transiciones.addAll(f2.edoInicial.transiciones);
            e.esAceptacion = false;
        }

        // --- Crear AFN compuesto ---
        AFN resultado = new AFN();
        // --- Construir AFN resultante ---
        resultado.edoInicial = this.edoInicial;
        resultado.estados.addAll(this.estados);
        resultado.estados.addAll(f2.estados);

        resultado.edosAcept.addAll(f2.edosAcept);

        // Unir alfabetos
        resultado.alfabeto.addAll(this.alfabeto);
        resultado.alfabeto.addAll(f2.alfabeto);

        // Estructura lógica para visualización
        resultado.subAFNs = new ArrayList<>();
        resultado.subAFNs.add(this.cloneAFN());
        resultado.subAFNs.add(f2.cloneAFN());
        resultado.operador = "&"; 

        return resultado;
    }
    
    /**public AFN concatenar(AFN f2) {
        // 1. CREAR COPIAS PROFUNDAS
        // Los AFNs originales (this y f2) se quedan intactos en la lista.
        AFN a1_copia = this.cloneAFN();
        AFN a2_copia = f2.cloneAFN();

        // 2. APLICAR LÓGICA DE CONCATENACIÓN sobre las COPIAS
        for (Estado e : a1_copia.edosAcept) {
            // Ahora modificamos los estados de la copia (a1_copia)
            e.transiciones.addAll(a2_copia.edoInicial.transiciones);
            e.esAceptacion = false;
        }

        // --- Crear AFN compuesto ---
        AFN resultado = new AFN();
    
        // --- Construir AFN resultante uniendo los componentes de las copias ---
        resultado.edoInicial = a1_copia.edoInicial;
        resultado.estados.addAll(a1_copia.estados);
        resultado.estados.addAll(a2_copia.estados);

        resultado.edosAcept.addAll(a2_copia.edosAcept);

        // Unir alfabetos
        resultado.alfabeto.addAll(a1_copia.alfabeto);
        resultado.alfabeto.addAll(a2_copia.alfabeto);

        // Estructura lógica para visualización
        resultado.subAFNs = new ArrayList<>();
        resultado.subAFNs.add(a1_copia.cloneAFN()); // Se añaden las copias utilizadas
        resultado.subAFNs.add(a2_copia.cloneAFN());
        resultado.operador = "&";
    
        return resultado;
    }*/

    /**public AFN concatenar(AFN f2) {
    // 1️⃣ Crear copias profundas de ambos AFNs
    AFN a1_copia = this.cloneAFN();
    AFN a2_copia = f2.cloneAFN();

    // 2️⃣ Conectar los estados de aceptación de a1 con el inicial de a2 mediante ε
    for (Estado e : a1_copia.edosAcept) {
        e.transiciones.add(new Transition(SimbEsp.EPSILON, a2_copia.edoInicial));
        e.esAceptacion = false;
    }

    // 3️⃣ Crear el AFN resultante
    AFN resultado = new AFN();
    resultado.edoInicial = a1_copia.edoInicial;

    // Agregar todos los estados
    resultado.estados.addAll(a1_copia.estados);
    resultado.estados.addAll(a2_copia.estados);

    // Los estados de aceptación son los del segundo
    resultado.edosAcept.addAll(a2_copia.edosAcept);

    // Unir alfabetos
    resultado.alfabeto.addAll(a1_copia.alfabeto);
    resultado.alfabeto.addAll(a2_copia.alfabeto);

    // 4️⃣ Estructura lógica para visualización y trazabilidad
    resultado.subAFNs = new ArrayList<>();
    resultado.subAFNs.add(a1_copia.cloneAFN());
    resultado.subAFNs.add(a2_copia.cloneAFN());
    resultado.operador = ".";  // o "&", pero "." es más usual para concatenación

    return resultado;
}*/

    public AFN cerraduraKleene() {
        Estado e1 = new Estado();
        Estado e2 = new Estado();

        for (Estado e : this.edosAcept) {
            e.transiciones.add(new Transition(SimbEsp.EPSILON, this.edoInicial));
            e.transiciones.add(new Transition(SimbEsp.EPSILON, e2));
            e.esAceptacion = false;
        }

        e1.transiciones.add(new Transition(SimbEsp.EPSILON, this.edoInicial));
        e1.transiciones.add(new Transition(SimbEsp.EPSILON, e2));

        this.edoInicial = e1;
        this.edosAcept.clear();
        this.edosAcept.add(e2);

        this.estados.add(e1);
        this.estados.add(e2);
        
        this.operador = "*";
        this.subAFNs = new ArrayList<>();
        this.subAFNs.add(this.cloneAFN()); // Es un AFN base con cerradura
    
        return this;
    }
    // AFN.cerraduraKleene() CORREGIDO
/*public AFN cerraduraKleene() {
    // 1. Clonar el AFN base (deep copy)
    AFN resultado = this.cloneAFN(); // Usa el AFN base (THIS) como operando
    
    // 2. Definir los nuevos estados (siempre en el AFN clonado/resultado)
    Estado e1 = new Estado();
    Estado e2 = new Estado();

    // 3. Aplicar la lógica al resultado clonado
    
    // Transiciones desde los estados de aceptación antiguos
    for (Estado e : resultado.edosAcept) { // Usar edosAcept del resultado
        e.transiciones.add(new Transition(SimbEsp.EPSILON, resultado.edoInicial));
        e.transiciones.add(new Transition(SimbEsp.EPSILON, e2));
        e.esAceptacion = false; // El estado de aceptación anterior ya no es de aceptación
    }

    // Transiciones desde el nuevo estado inicial
    e1.transiciones.add(new Transition(SimbEsp.EPSILON, resultado.edoInicial));
    e1.transiciones.add(new Transition(SimbEsp.EPSILON, e2));
    
    // Actualizar los componentes del nuevo AFN
    resultado.edoInicial = e1;
    resultado.edosAcept.clear();
    resultado.edosAcept.add(e2);

    resultado.estados.add(e1);
    resultado.estados.add(e2);
    
    resultado.operador = "*";
    
    // Si estás manejando la jerarquía de AFNs, el subAFNs de la cerradura es el original:
    resultado.subAFNs = new ArrayList<>();
    resultado.subAFNs.add(this.cloneAFN()); // Clonar el AFN base original (this)

    return resultado; // Retornar el NUEVO AFN, no 'this'
}*/
    
    public AFN cerraduraPositiva(){
        Estado e1 = new Estado();
        Estado e2 = new Estado();
        for (Estado e: this.edosAcept){
            e.transiciones.add(new Transition(SimbEsp.EPSILON, this.edoInicial));
            e.transiciones.add(new Transition(SimbEsp.EPSILON, e2));
            e.esAceptacion = false;
        }
        
        e1.transiciones.add(new Transition(SimbEsp.EPSILON, this.edoInicial));
        
        this.edoInicial = e1;
        this.edosAcept.clear();
        this.edosAcept.add(e2);

        this.estados.add(e1);
        this.estados.add(e2);
        
        this.operador = "+";
        this.subAFNs = new ArrayList<>();
        this.subAFNs.add(this.cloneAFN());// Es un AFN base con cerradura
    
        return this;
    }
    
    public AFN cerraduraOpcional(){
        Estado e1 = new Estado();
        Estado e2 = new Estado();
        for (Estado e: this.edosAcept){
            e.transiciones.add(new Transition(SimbEsp.EPSILON, this.edoInicial));
            e.transiciones.add(new Transition(SimbEsp.EPSILON, e2));
            e.esAceptacion = false;
        }
        
        e1.transiciones.add(new Transition(SimbEsp.EPSILON, this.edoInicial));
        
        this.edoInicial = e1;
        this.edosAcept.clear();
        this.edosAcept.add(e2);

        this.estados.add(e1);
        this.estados.add(e2);
        
        this.operador = "?";
        this.subAFNs = new ArrayList<>();
        this.subAFNs.add(this.cloneAFN()); // Es un AFN base con cerradura
    
        
        e1.transiciones.add(new Transition(SimbEsp.EPSILON,e2));
               
        return this;
    }

    public Set<Estado> cerraduraEpsilon(Estado e) {
        Set<Estado> c = new HashSet<>();
        Stack<Estado> p = new Stack<>();
        c.add(e);
        p.push(e);

        while (!p.isEmpty()) {
            Estado e2 = p.pop();
            for (Transition t : e2.transiciones) {
                if (t.simbInf == SimbEsp.EPSILON && !c.contains(t.edoDestino)) {
                    c.add(t.edoDestino);
                    p.push(t.edoDestino);
                }
            }
        }
        return c;
    }

    public Set<Estado> cerraduraEpsilon(Set<Estado> estados) {
        Set<Estado> r = new HashSet<>();
        for (Estado e : estados) r.addAll(cerraduraEpsilon(e));
        return r;
    }

    public Set<Estado> mover(Estado e, char c) {
        Set<Estado> r = new HashSet<>();
        for (Transition t : e.transiciones) if (t.acepta(c)) r.add(t.edoDestino);
        return r;
    }

    public Set<Estado> mover(Set<Estado> estados, char c) {
        Set<Estado> r = new HashSet<>();
        for (Estado e : estados) for (Transition t : e.transiciones) if (t.acepta(c)) r.add(t.edoDestino);
        return r;
    }

    public Set<Estado> ira(Estado e, char c) {
        return cerraduraEpsilon(mover(e, c));
    }

    public Set<Estado> ira(Set<Estado> estados, char c) {
        return cerraduraEpsilon(mover(estados, c));
    }
    
    
    //-----Métodos de apoyo-------
    //Método para clonar el AFN y guardarlo sin modificar su estructura
    public AFN cloneAFN() {
        AFN copia = new AFN();
        copia.alfabeto = new HashSet<>(this.alfabeto);
        copia.operador = this.operador;

        // Copia profunda de los estados
        HashMap<Estado, Estado> mapa = new HashMap<>();
        for (Estado e : this.estados) {
            Estado nuevo = new Estado();
            nuevo.id = e.id;
            nuevo.esAceptacion = e.esAceptacion;
            nuevo.transiciones = new HashSet<>();
            mapa.put(e, nuevo);
            copia.estados.add(nuevo);
        }
        for (Estado e : this.estados) {
            Estado nuevo = mapa.get(e);
            for (Transition t : e.transiciones) {
                nuevo.transiciones.add(
                    new Transition(t.simbInf, t.simbSup, mapa.get(t.edoDestino))
                );
            }
        }

        copia.edoInicial = mapa.get(this.edoInicial);
        for (Estado e : this.edosAcept)
            copia.edosAcept.add(mapa.get(e));

        // Copiar subAFNs de manera recursiva
        if (this.subAFNs != null) {
            copia.subAFNs = new ArrayList<>();
            for (AFN sub : this.subAFNs) {
                copia.subAFNs.add(sub.cloneAFN());
            }
        }

        return copia;
    }
    
    
    //Método para escribir la cadena completa de AFN
    
    
    /*@Override
    public String toString() {      
        // CASO 1: AFN básico (símbolo individual o rango)
        /*if (subAFNs == null || subAFNs.isEmpty()) {
            if (alfabeto.isEmpty()) return "AFN vacío";

            // Condición 1: AFN de un solo carácter
            if (alfabeto.size() == 1) {
                String simbolo = alfabeto.iterator().next().toString();
                // Incluir el operador de cerradura si existe
                if (operador.equals("*") || operador.equals("+") || operador.equals("?")) {
                    return "(" + simbolo + ")" + operador;
                }
                return "(" + simbolo + ")";
            }

            // Condición 2: AFN de múltiples caracteres (ASUMIMOS QUE ES UN RANGO [a-b] o [a|b])
            // Si la estructura del AFN es solo para rangos [a-z], esta lógica es correcta.
            // Si no, necesitarías verificar un flag `esRango` en el AFN.
            char min = alfabeto.stream().min(Character::compare).orElse('?');
            char max = alfabeto.stream().max(Character::compare).orElse('?');
            return "( " + min + " - " + max + " )" + operador; 
        }

        // CASO 2: AFN compuesto (unión, concatenación)
        if (subAFNs != null && !subAFNs.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            // Abrir la agrupación de esta operación compuesta
            sb.append("[ ");
        
            for (int i = 0; i < subAFNs.size(); i++) {
                // Se asume que el AFN hijo ya maneja su propia agrupación interna (ej. la cerradura)
                sb.append(subAFNs.get(i).toString());
            
                // Añadir el operador del AFN actual (unión '|' o concatenación 'º')
                if (i < subAFNs.size() - 1) {
                    sb.append(" ").append(operador).append(" ");
                }
            }
            sb.append(" ]");
        
            // Si tienes una doble cerradura o algo inusual, podrías necesitar 
            // aplicar el operador aquí si el AFN compuesto también tiene uno (poco común).
        
            return sb.toString();
        }
    
        return "AFN inválido";
        
         StringBuilder sb = new StringBuilder();

    // AFN básico
    if (subAFNs == null || subAFNs.isEmpty()) {
        if (alfabeto.isEmpty()) return "AFN vacío";

        if (alfabeto.size() == 1) {
            sb.append("(").append(alfabeto.iterator().next()).append(")");
        } else {
            char min = alfabeto.stream().min(Character::compare).orElse('?');
            char max = alfabeto.stream().max(Character::compare).orElse('?');
            sb.append("(").append(min).append("-").append(max).append(")");
        }
    }
    // AFN compuesto
    else {
        sb.append("[ ");
        for (int i = 0; i < subAFNs.size(); i++) {
            sb.append(subAFNs.get(i).toString());
            if (i < subAFNs.size() - 1) {
                sb.append(" ").append(operador).append(" ");
            }
        }
        sb.append(" ]");
    }

    // Aplicar el operador del AFN actual (si existe)
    if (operador != null && (operador.equals("*") || operador.equals("+") || operador.equals("?"))) {
        sb.append(operador);
    }

    return sb.toString();
    }*/
    
    /*@Override
public String toString() {

    StringBuilder sb = new StringBuilder();

    // CASO 1: AFN básico (símbolo individual o rango)
    /*if (subAFNs == null || subAFNs.isEmpty()) {
        if (alfabeto.isEmpty()) return "AFN vacío";

        if (alfabeto.size() == 1) {
            String simbolo = alfabeto.iterator().next().toString();
            sb.append("(").append(simbolo).append(")");
        } else {
            char min = alfabeto.stream().min(Character::compare).orElse('?');
            char max = alfabeto.stream().max(Character::compare).orElse('?');
            sb.append("(").append(min).append("-").append(max).append(")");
        }
    }*/

    // CASO 2: AFN compuesto (unión, concatenación)
    /*else {
        sb.append("[ ");
        for (int i = 0; i < subAFNs.size(); i++) {
            sb.append(subAFNs.get(i).toString());
            if (i < subAFNs.size() - 1) {
                sb.append(" ").append(operador).append(" ");
            }
        }
        sb.append(" ]");
    }

    // ✅ SOLO aquí, al final, aplicamos el operador del AFN actual
    if (operador != null && (operador.equals("*") || operador.equals("+") || operador.equals("?"))) {
        sb.append(operador);
    }*
    

// CASO 1: AFN básico (símbolo individual o rango)
if (subAFNs == null || subAFNs.isEmpty()) {
    // Condición 1: AFN de un solo carácter
    if (alfabeto.size() == 1) {
                String simbolo = alfabeto.iterator().next().toString();
                // Incluir el operador de cerradura si existe
                if (operador.equals("*") || operador.equals("+") || operador.equals("?")) {
                    return "(" + simbolo + ")" + operador;
                }
                return "(" + simbolo + ")";
            }
    // Condición 2: AFN de múltiples caracteres (ASUMIMOS QUE ES UN RANGO [a-b] o [a|b])
    // Se recomienda usar un flag 'esRango' para hacer esto explícito
    char min = alfabeto.stream().min(Character::compare).orElse('?');
    char max = alfabeto.stream().max(Character::compare).orElse('?');
    
    // Cambiamos el formato para que parezca un rango [min-max]
    String rango = "[" + min + "-" + max + "]"; 
    
    // Aplicar cerradura solo si el operador no es el por defecto (epsilon o vacío)
    if (operador.equals("*") || operador.equals("+") || operador.equals("?")) {
         return rango + operador;
    }
    return rango;
}
    // CASO 2: AFN compuesto (unión, concatenación)
if (subAFNs != null && !subAFNs.isEmpty()) {
    //StringBuilder sb = new StringBuilder();

    // 2a. Manejar la Cerradura (*, +, ?)
    if (operador.equals("*") || operador.equals("+") || operador.equals("?")) {
        // Aplicar paréntesis solo si la base tiene un operador binario (como la unión '|')
        String base = subAFNs.get(0).toString();
        // Verifica si la base es una composición compleja (contiene el operador de unión)
        boolean necesitaParentesis = base.contains("|"); 

        if (necesitaParentesis) {
             sb.append("(").append(base).append(")");
        } else {
             sb.append(base);
        }
        sb.append(operador);
        return sb.toString();
    }
    
    // 2b. Manejar la Unión (|) o Concatenación (º)
    // El AFN resultante de la Unión debería caer aquí
    for (int i = 0; i < subAFNs.size(); i++) {
        // Llama recursivamente, el subAFN devolverá su representación (p. ej., [0-9])
        sb.append(subAFNs.get(i).toString());
    
        // Añadir el operador del AFN actual (unión '|' o concatenación 'º')
        if (i < subAFNs.size() - 1) {
            sb.append(operador); // NO AÑADIR ESPACIOS aquí para que se vea como (A|B)
        }
    }
    // Opcionalmente puedes agregar corchetes [ ] si la convención de tu lenguaje los requiere para la unión
    // return "[ " + sb.toString() + " ]"; 
    
    // Para el resultado: (0-9)|(a-z)|(A-Z)
    return sb.toString(); 
}

    //return sb.toString();
        return null;
}*/

    
    //ESTE TOSTRING DA UNA ESTRUCTURA MÁS BONITA PERO NO SIRVE DEL TODO 
    @Override
    public String toString() {
        // CASO 1: AFN básico (símbolo individual o rango)

        if (subAFNs == null || subAFNs.isEmpty()) {
            if (alfabeto.isEmpty()) return "AFN vacío";
            // Condición 1: AFN de un solo carácter
            if (alfabeto.size() == 1) {
                String simbolo = alfabeto.iterator().next().toString();
                // Incluir el operador de cerradura si existe
                if (operador.equals("*") || operador.equals("+") || operador.equals("?")) {
                    return "(" + simbolo + ")" + operador;
                }
                return "(" + simbolo + ")";
            }
            // Condición 2: AFN de múltiples caracteres (ASUMIMOS QUE ES UN RANGO [a-b] o [a|b])
            // Si la estructura del AFN es solo para rangos [a-z], esta lógica es correcta.
            // Si no, necesitarías verificar un flag `esRango` en el AFN.
            char min = alfabeto.stream().min(Character::compare).orElse('?');
            char max = alfabeto.stream().max(Character::compare).orElse('?');
            return "( " + min + " - " + max + " )" + operador; 
        }
        // CASO 2: AFN compuesto (unión, concatenación)
        if (subAFNs != null && !subAFNs.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            // Abrir la agrupación de esta operación compuesta
            sb.append("[ ");  
            for (int i = 0; i < subAFNs.size(); i++) {
                // Se asume que el AFN hijo ya maneja su propia agrupación interna (ej. la cerradura)
                sb.append(subAFNs.get(i).toString());         
                // Añadir el operador del AFN actual (unión '|' o concatenación 'º')
                if (i < subAFNs.size() - 1) {
                    sb.append(" ").append(operador).append(" ");
                }
            }
            sb.append(" ]");      
            // Si tienes una doble cerradura o algo inusual, podrías necesitar 
            // aplicar el operador aquí si el AFN compuesto también tiene uno (poco común).     
            return sb.toString();
        }
        return "AFN inválido";
    }


}
