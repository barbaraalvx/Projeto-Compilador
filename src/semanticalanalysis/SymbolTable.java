package semanticalanalysis;

import java.util.HashSet;
import java.util.Set;

public class SymbolTable {
    private final Set<String> declared = new HashSet<>();

    public void declare(String name) {
        declared.add(name);
    }

    public boolean isDeclared(String name) {
        return declared.contains(name);
    }
}