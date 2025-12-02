package Models;

import java.util.HashSet;
import java.util.Set;

public class ElemSj {
    int id;
    Set<Estado> S;
    
    ElemSj(){	
	id = -1;
	S = new HashSet<>();
	S.clear();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElemSj)) return false;
        ElemSj other = (ElemSj) o;
        return S.equals(other.S);
    }

    @Override
    public int hashCode() {
        return S.hashCode();
    }
}
