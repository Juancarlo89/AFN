package Models;

import java.util.*;

public class AFNParser {

    /**
     * Dado un texto que representa un símbolo o rango entre paréntesis,
     * por ejemplo "(a)" o "(a-z)" o "(0-9)" devuelve el conjunto de caracteres.
     *
     * NOTA: asume que recibes el texto sin corchetes exteriores,
     * por ejemplo "a" o "a-z".
     */
    public static Set<Character> expandSymbolContent(String content) {
        Set<Character> set = new LinkedHashSet<>();
        content = content.trim();
        if (content.isEmpty()) return set;

        // Si tiene un '-' y forma un rango simple a-z o 0-9
        // acepta una sola forma 'x-y' con exactamente un '-'
        if (content.length() == 3 && content.charAt(1) == '-') {
            char start = content.charAt(0);
            char end = content.charAt(2);
            if (start <= end) {
                for (char c = start; c <= end; c++) set.add(c);
                return set;
            }
        }

        // Si no es un rango simple, tomar cada caracter literal
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (!Character.isWhitespace(c)) set.add(c);
        }
        return set;
    }

    /**
     * Expande una notación completa de símbolo entre paréntesis,
     * por ejemplo "(a)" -> {'a'}, "(a-z)" -> {'a',...,'z'}.
     * Recibe la string completa incluyendo paréntesis y devuelve el set.
     */
    public static Set<Character> expandSymbol(String token) {
        token = token.trim();
        if (!token.startsWith("(") || !token.endsWith(")")) {
            return Collections.emptySet();
        }
        String inner = token.substring(1, token.length() - 1).trim();
        return expandSymbolContent(inner);
    }

    /**
     * Ejemplo de parseo simple de una expresión AFN compuesta en tu sintaxis:
     * "[ (a-z)+ & (0-9)+ ]"  (concatenación & ; unión | )
     *
     * Este método NO construye el AFN completo de Thompson, solo muestra
     * cómo extraer los símbolos y operadores correctamente.
     */
    public static void parseComposite(String composite) {
        // Quitar corchetes
        composite = composite.trim();
        if (composite.startsWith("[") && composite.endsWith("]")) {
            String inside = composite.substring(1, composite.length()-1).trim();
            // separar por & o | (aquí de ejemplo solo manejo &)
            String[] parts = inside.split("&");
            for (String p : parts) {
                p = p.trim();
                // puede ser "(a-z)+" o "(a)" etc.
                // detecto el primer paréntesis (y posible sufijo +)
                int endPar = p.indexOf(')');
                if (endPar > 0) {
                    String symbolPart = p.substring(0, endPar+1);
                    Set<Character> sym = expandSymbol(symbolPart);
                    System.out.println("Sub-AFN símbolos: " + sym + " resto: " + p.substring(endPar+1));
                }
            }
        }
    }

    // Metodo de prueba
    /*public static void main(String[] args) {
        System.out.println(expandSymbol("(a-z)"));
        System.out.println(expandSymbol("(0-9)"));
        System.out.println(expandSymbol("(+)"));
    }*/
}

