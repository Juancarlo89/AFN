package Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LR0Parser {
    private Grammar G;

    public static class Item {
        public String A;
        public List<String> alpha;
        public int dot;
        public Item(String A, List<String> alpha, int dot){ this.A=A; this.alpha=new ArrayList<>(alpha); this.dot=dot; }
        public boolean isReduce(){ return dot >= alpha.size(); }
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append(A).append(" -> ");
            for(int i=0;i<alpha.size();i++){
                if(i==dot) sb.append(".");
                sb.append(alpha.get(i)).append(" ");
            }
            if(dot==alpha.size()) sb.append(".");
            return sb.toString();
        }
        public int hashCode(){ return (A+alpha.toString()+dot).hashCode(); }
        public boolean equals(Object o){
            if(!(o instanceof Item)) return false;
            Item it = (Item)o; return A.equals(it.A) && alpha.equals(it.alpha) && dot==it.dot;
        }
    }

    public LR0Parser(Grammar g){ this.G=g; }

    private Set<Item> closure(Set<Item> I){
        Set<Item> C = new HashSet<>(I);
        boolean changed = true;
        while(changed){
            changed = false;
            Set<Item> toAdd = new HashSet<>();
            for(Item it : C){
                if(it.dot < it.alpha.size()){
                    String B = it.alpha.get(it.dot);
                    if(G.rules.containsKey(B)){
                        for(List<String> beta : G.rules.get(B)){
                            Item ni = new Item(B, beta, 0);
                            if(!C.contains(ni)) toAdd.add(ni);
                        }
                    }
                }
            }
            if(!toAdd.isEmpty()){ C.addAll(toAdd); changed = true; }
        }
        return C;
    }

    private Set<Item> goTo(Set<Item> I, String X){
        Set<Item> J = new HashSet<>();
        for(Item it : I){
            if(it.dot < it.alpha.size() && it.alpha.get(it.dot).equals(X)){
                J.add(new Item(it.A, it.alpha, it.dot+1));
            }
        }
        return closure(J);
    }

    public static class State {
        public Set<Item> items;
        public State(Set<Item> items){ this.items = items; }
        public boolean equals(Object o){ if(!(o instanceof State)) return false; return items.equals(((State)o).items); }
        public int hashCode(){ return items.hashCode(); }
    }

    public static class CanonicalResult {
        public List<State> states = new ArrayList<>();
        public Map<Integer, Map<String, Integer>> transitions = new HashMap<>();
        public List<String> conflicts = new ArrayList<>();
    }

    public CanonicalResult buildCanonicalCollection(){
        CanonicalResult res = new CanonicalResult();
        // augmented grammar: create S' -> S
        String S0 = G.start;
        List<String> startProd = new ArrayList<>(); startProd.add(S0);

        Item startItem = new Item("S'", startProd, 0);
        Set<Item> I0 = new HashSet<>(); I0.add(startItem);
        I0 = closure(I0);
        State s0 = new State(I0);
        res.states.add(s0);

        boolean changed = true;
        while(changed){
            changed = false;
            for(int i=0;i<res.states.size();i++){
                State Si = res.states.get(i);
                // collect symbols after dots
                Set<String> symbols = new HashSet<>();
                for(Item it : Si.items){
                    if(it.dot < it.alpha.size()) symbols.add(it.alpha.get(it.dot));
                }
                for(String X : symbols){
                    Set<Item> gotoI = goTo(Si.items, X);
                    if(gotoI.isEmpty()) continue;
                    State newS = new State(gotoI);
                    int idx = indexOfState(res.states, newS);
                    if(idx == -1){ res.states.add(newS); idx = res.states.size()-1; changed = true; }
                    // record transition
                    res.transitions.computeIfAbsent(i, k->new HashMap<>()).put(X, idx);
                }
            }
        }

        // detect conflicts: for each state, if there is any reduce item and any item with dot before a symbol -> shift-reduce
        for(int i=0;i<res.states.size();i++){
            State Si = res.states.get(i);
            List<Item> reduces = new ArrayList<>();
            Set<String> shifts = new HashSet<>();
            for(Item it : Si.items){
                if(it.isReduce()){ reduces.add(it); }
                else {
                    String a = it.alpha.get(it.dot);
                    shifts.add(a);
                }
            }
            if(reduces.size() > 1){
                res.conflicts.add("Reduce-Reduce conflict in state " + i + ": " + reduces);
            }
            if(reduces.size() >=1 && !shifts.isEmpty()){
                res.conflicts.add("Shift-Reduce conflict in state " + i + ": reduce=" + reduces + " shifts=" + shifts);
            }
        }

        return res;
    }

    private int indexOfState(List<State> list, State s){
        for(int i=0;i<list.size();i++) if(list.get(i).items.equals(s.items)) return i; return -1;
    }
}
