package syntacticanalysis;

import syntacticanalysis.expression.Expression;

public class VariableReference extends Expression {
    private final String name;

    public VariableReference(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "VarRef " + name);
    }
}
