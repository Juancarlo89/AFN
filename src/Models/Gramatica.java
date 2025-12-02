package Models;

import static Models.SimbEsp.EPSILON;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Gramatica {
    public int NumReglas;
    public LadoIzq[] Reglas;
    public Set<String> Vn;
    public Set<String> Vt;
    public SimbolG SimbIn;
    public AnalizadorLexico Lexic;
    public Token Tokens;
    
    
    //Método para calcular el FIRST
    public Set<SimbolG> First(List<SimbolG> l){
        Set<SimbolG> R;
        R = new HashSet<SimbolG>();
        R.clear();
        SimbolG primer = l.get(0);
        
        if(primer.EsTerminal){
            R.add(primer);
            return R;
        }
        
        for(int i=0; i<NumReglas; i++){
            /*if(Reglas[i].SimbIzq.NombSimb == primer.NombSimb){
                R.unir(First(Reglas[i]).LadoDer);
            }*/
            if (Reglas[i].SimbIzq.NombSimb.equals(primer.NombSimb)) {
                R.addAll(First(Reglas[i].LadoDer));
            }
        }
        
        if(R.contains(EPSILON)){
            if(l.size()== 1){
                return R;
            }
            List<SimbolG> sub = l.subList(1, l.size());
            R.add((SimbolG) First(sub));
        }
        return R;
    }
    
    //Método para calcular el FOLLOW
    public Set<SimbolG> Follow (SimbolG S){
        int j;
        Set<SimbolG> aux;
        Set<SimbolG> R = new HashSet<SimbolG>();
        aux = new HashSet<SimbolG>();
        R.clear();
        if(S.EsTerminal){
            R.add(S);
            return R;
        }
        //Buscar s en los lados derechos
        for(int i = 0; i < NumReglas; i++){
            List<SimbolG> LD = Reglas[i].LadoDer;
            j = LD.indexOf(S);
            
            if( j == -1) continue;
            
            
            if (j == LD.size() - 1) {
                if (S.equals(Reglas[i].SimbIzq)) continue;
                R.addAll(Follow(Reglas[i].SimbIzq));
                continue;
            }
            
            aux.clear();
            List<SimbolG> resto = LD.subList(j + 1, LD.size());
            aux = First(resto);

            if (aux.contains(EPSILON)) {
                aux = new HashSet<>(aux);
                aux.remove(EPSILON);
                R.addAll(aux);
                R.addAll(Follow(Reglas[i].SimbIzq));
            }

            R.addAll(aux);
        }
        
        return R;
    }
    
    //Reglas procesamiento
        public boolean G() {
        return Reglas();
    }

    public boolean Reglas() {
        int token;

        if (Regla()) {
            token = Lexic.yylex();
            if (token == Tokens.PC) {
                return ReglasP();
            }
        }
        return false;
    }

    public boolean ReglasP() {
        int token;
        //StatusLexic edo = Lexic.GetEdo();

        if (Regla()) {
            token = Lexic.yylex();
            if (token == Tokens.PC) {
                return ReglasP();
            }
        }
        return false;
    }

    public boolean Regla() {
        String lexIzq = "";

        if (LadoIzq(lexIzq)) {
            int token = Lexic.yylex();
            if (token == Tokens.FLECHA) {
                if (LadoDer(lexIzq)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean LadoIzq(String lexRef) {
        SimbolG s = new SimbolG();
        int token = Lexic.yylex();

        if (token == Tokens.SIMBOLO) {
            s.NombSimb = Lexic.yytext();
            s.EsTerminal = false;

            Vn.add(s.NombSimb);
            lexRef = Lexic.yytext();
            return true;
        }
        return false;
    }

    public boolean LadosDer(String lex) {
        if (LadoDer(lex)) {
            return LadoDerP(lex);
        }
        return false;
    }

    public boolean LadoDerP(String lex) {
        int token = Lexic.yylex();

        if (token == Tokens.OR) {
            if (LadoDer(lex)) {
                return LadoDerP(lex);
            }
            return false;
        }

        Lexic.undoToken();
        return true;
    }

    public boolean LadoDer(String lex) {
        List<SimbolG> l = new ArrayList<>();
        if (SecSimbolos(l)) {

            SimbolG izq = new SimbolG();

            LadoIzq regla = new LadoIzq();
            regla.SimbIzq = izq;
            regla.LadoDer = l;

            // Aquí deberías almacenarlo en Reglas[]
            return true;
        }
        return false;
    }

    public boolean SecSimbolos(List<SimbolG> l) {
        int token = Lexic.yylex();

        if (token == Tokens.SIMBOLO) {
            SimbolG s = new SimbolG();
            s.NombSimb = Lexic.yytext();

            if (SecSimbolosP(l)) {
                l.add(0, s);
                return true;
            }
        }
        return false;
    }

    public boolean SecSimbolosP(List<SimbolG> l) {
        int token = Lexic.yylex();
        SimbolG s;
        if (token == Tokens.SIMBOLO) {
            s = new SimbolG();
            s.NombSimb = Lexic.yytext();

            if (SecSimbolosP(l)) {
                l.add(0, s);
                return true;
            }
            return false;
        }

        Lexic.undoToken();
        return true;
    }
    
}
