package Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LL1Parser {
    private Grammar G;
    // table: A -> (terminal -> production (list of symbols))
    public Map<String, Map<String, List<String>>> table;
    public List<String> conflicts;

    public LL1Parser(Grammar g){
        this.G = g;
        this.table = new HashMap<>();
        this.conflicts = new ArrayList<>();
    }

    public boolean build(){
        table.clear(); conflicts.clear();
        Map<String, Set<String>> FIRST = G.computeFirst();
        Map<String, Set<String>> FOLLOW = G.computeFollow();

        for(String A : G.rules.keySet()){
            table.put(A, new HashMap<>());
        }

        for(String A : G.rules.keySet()){
            List<List<String>> alts = G.rules.get(A);
            for(List<String> alpha : alts){
                Set<String> firstAlpha = firstOfSequence(alpha, FIRST);
                for(String a : firstAlpha){
                    if(a.equals("ε")) continue;
                    Map<String, List<String>> row = table.get(A);
                    List<String> existing = row.get(a);
                    if(existing != null && !existing.equals(alpha)){
                        conflicts.add("Conflict at table["+A+"]["+a+"]: multiple productions");
                    } else {
                        row.put(a, alpha);
                    }
                }
                if(firstAlpha.contains("ε")){
                    Set<String> fol = FOLLOW.getOrDefault(A, java.util.Collections.emptySet());
                    for(String b : fol){
                        Map<String, List<String>> row = table.get(A);
                        List<String> existing = row.get(b);
                        if(existing != null && !existing.equals(alpha)){
                            conflicts.add("Conflict at table["+A+"]["+b+"]: multiple productions (via epsilon)");
                        } else {
                            row.put(b, alpha);
                        }
                    }
                }
            }
        }

        return conflicts.isEmpty();
    }

    private Set<String> firstOfSequence(List<String> seq, Map<String, Set<String>> FIRST){
        Set<String> res = new HashSet<>();
        boolean nullablePrefix = true;
        for(String X : seq){
            if(X.equals("ε")){
                res.add("ε"); nullablePrefix = false; break;
            }
            if(!G.rules.containsKey(X)){
                res.add(X); nullablePrefix = false; break;
            }
            Set<String> FX = FIRST.getOrDefault(X, java.util.Collections.emptySet());
            for(String t : FX) if(!t.equals("ε")) res.add(t);
            if(FX.contains("ε")) { nullablePrefix = true; } else { nullablePrefix = false; break; }
        }
        if(nullablePrefix) res.add("ε");
        return res;
    }

    public String tableToString(){
        StringBuilder sb = new StringBuilder();
        List<String> nts = new ArrayList<>(G.rules.keySet());
        sb.append("LL(1) Parsing Table:\n");
        for(String A : nts){
            sb.append(A).append(" ->\n");
            Map<String, List<String>> row = table.getOrDefault(A, java.util.Collections.emptyMap());
            for(Map.Entry<String, List<String>> e : row.entrySet()){
                sb.append("   [").append(e.getKey()).append("] : ");
                sb.append(String.join(" ", e.getValue())).append("\n");
            }
        }
        if(!conflicts.isEmpty()){
            sb.append("\nConflicts:\n");
            for(String c : conflicts) sb.append(c).append("\n");
        }
        return sb.toString();
    }

    // simple parse using terminals separated by spaces, returns boolean
    public boolean parseInput(String input){
        // tokens separated by spaces
        List<String> tokens = new ArrayList<>();
        for(String t : input.trim().split("\\s+")) if(!t.isEmpty()) tokens.add(t);
        tokens.add("$");

        java.util.Stack<String> stack = new java.util.Stack<>();
        stack.push("$");
        stack.push(G.start);

        int idx = 0;
        while(!stack.isEmpty()){
            String top = stack.peek();
            String a = tokens.get(idx);
            if(top.equals("$") && a.equals("$")) return true;
            if(!G.rules.containsKey(top)){
                // terminal
                if(top.equals(a)){
                    stack.pop(); idx++; continue;
                } else return false;
            } else {
                Map<String, List<String>> row = table.get(top);
                List<String> prod = row.get(a);
                if(prod == null){
                    // try using epsilon via FOLLOW (if exists entry for a)
                    prod = row.get("ε");
                }
                if(prod == null) return false;
                stack.pop();
                if(!(prod.size()==1 && prod.get(0).equals("ε"))){
                    for(int i=prod.size()-1;i>=0;i--) stack.push(prod.get(i));
                }
            }
        }
        return false;
    }
}
