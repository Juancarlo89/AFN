package Models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple grammar loader and FIRST/FOLLOW calculator.
 * Expected grammar file format (one rule per line):
 *   S -> A B | a
 *   A -> a | epsilon
 */
public class Grammar {
    public Set<String> Vn; // non-terminals
    public Set<String> Vt; // terminals
    public String start;
    // rules: LHS -> list of alternatives, each alternative is list of symbols
    public Map<String, List<List<String>>> rules;

    private Map<String, Set<String>> first;
    private Map<String, Set<String>> follow;

    public Grammar(){
        Vn = new HashSet<>();
        Vt = new HashSet<>();
        rules = new HashMap<>();
        first = new HashMap<>();
        follow = new HashMap<>();
        start = null;
    }

    public boolean loadFromFile(String ruta) throws IOException{
        List<String> lines = Files.readAllLines(Path.of(ruta));
        return loadFromLines(lines);
    }

    public boolean loadFromLines(List<String> lines){
        rules.clear(); Vn.clear(); Vt.clear(); start = null;

        for(String raw : lines){
            String line = raw.trim();
            if(line.isEmpty()) continue;
            if(line.startsWith("#") || line.startsWith("//")) continue;

            // normalize arrow
            String arrow = "->";
            if(!line.contains(arrow)){
                if(line.contains(":")) arrow = ":";
                else if(line.contains("→")) arrow = "→";
            }

            String[] parts = line.split(arrow, 2);
            if(parts.length < 2) continue;
            String lhs = parts[0].trim();
            String rhs = parts[1].trim();

            if(start == null) start = lhs;

            Vn.add(lhs);
            List<List<String>> alts = rules.getOrDefault(lhs, new ArrayList<>());

            String[] alternatives = rhs.split("\\|");
            for(String alt : alternatives){
                String a = alt.trim();
                if(a.isEmpty()) continue;
                List<String> symbols = new ArrayList<>();
                // split by spaces
                String[] toks = a.split("\\s+");
                for(String t : toks){
                    if(t.isEmpty()) continue;
                    if(t.equals("epsilon") || t.equals("EPS") || t.equals("ε")){
                        symbols.add("ε");
                        continue;
                    }
                    symbols.add(t);
                }
                alts.add(symbols);
            }

            rules.put(lhs, alts);
        }

        // collect terminals: any symbol in RHS that is not a non-terminal
        for(Map.Entry<String, List<List<String>>> e : rules.entrySet()){
            for(List<String> alt : e.getValue()){
                for(String s : alt){
                    if(s.equals("ε")) continue;
                    // heuristic: non-terminals are those that appear as LHS
                    if(!rules.containsKey(s)) Vt.add(s);
                }
            }
        }

        // initialize first/follow maps
        for(String A : rules.keySet()){
            first.put(A, new HashSet<>());
            follow.put(A, new HashSet<>());
        }

        return true;
    }

    public Map<String, Set<String>> computeFirst(){
        // FIRST for terminals
        Map<String, Set<String>> F = new HashMap<>();
        // initialize
        for(String A : rules.keySet()) F.put(A, new HashSet<>());

        boolean changed = true;
        while(changed){
            changed = false;
            for(String A : rules.keySet()){
                Set<String> FA = F.get(A);
                List<List<String>> alts = rules.get(A);
                for(List<String> alpha : alts){
                    boolean nullablePrefix = true;
                    for(String X : alpha){
                        if(X.equals("ε")){
                            if(!FA.contains("ε")) { FA.add("ε"); changed = true; }
                            nullablePrefix = false; break;
                        }
                        // if X is terminal
                        if(!rules.containsKey(X)){
                            if(!FA.contains(X)) { FA.add(X); changed = true; }
                            nullablePrefix = false; break;
                        } else {
                            // X is non-terminal
                            Set<String> FX = F.get(X);
                            if(FX != null){
                                for(String t : FX){
                                    if(t.equals("ε")) continue;
                                    if(!FA.contains(t)){ FA.add(t); changed = true; }
                                }
                                if(FX.contains("ε")){
                                    nullablePrefix = true;
                                } else {
                                    nullablePrefix = false; break;
                                }
                            }
                        }
                    }
                    if(nullablePrefix){
                        if(!FA.contains("ε")){ FA.add("ε"); changed = true; }
                    }
                }
            }
        }

        this.first = F;
        return F;
    }

    public Map<String, Set<String>> computeFollow(){
        Map<String, Set<String>> FOL = new HashMap<>();
        for(String A : rules.keySet()) FOL.put(A, new HashSet<>());
        if(start != null) FOL.get(start).add("$");

        Map<String, Set<String>> FIRST = (this.first == null || this.first.isEmpty()) ? computeFirst() : this.first;

        boolean changed = true;
        while(changed){
            changed = false;
            for(String A : rules.keySet()){
                for(List<String> alpha : rules.get(A)){
                    for(int i=0;i<alpha.size();i++){
                        String B = alpha.get(i);
                        if(B.equals("ε")) continue;
                        if(!rules.containsKey(B)) continue; // terminal

                        // compute FIRST(beta)
                        Set<String> firstBeta = new HashSet<>();
                        boolean nullable = true;
                        for(int j=i+1;j<alpha.size();j++){
                            String Y = alpha.get(j);
                            if(Y.equals("ε")) { nullable = true; continue; }
                            if(!rules.containsKey(Y)){
                                firstBeta.add(Y); nullable = false; break;
                            } else {
                                Set<String> FY = FIRST.get(Y);
                                for(String t : FY) if(!t.equals("ε")) firstBeta.add(t);
                                if(FY.contains("ε")) { nullable = true; } else { nullable = false; break; }
                            }
                        }

                        // add FIRST(beta) - {ε} to FOLLOW(B)
                        Set<String> FB = FOL.get(B);
                        int before = FB.size();
                        FB.addAll(firstBeta);
                        if(nullable){
                            // add FOLLOW(A) to FOLLOW(B)
                            FB.addAll(FOL.get(A));
                        }
                        if(FB.size() > before) changed = true;
                    }
                }
            }
        }

        this.follow = FOL;
        return FOL;
    }

    // helpers to return readable lists
    public List<String> nonTerminals(){
        return new ArrayList<>(rules.keySet());
    }

}
