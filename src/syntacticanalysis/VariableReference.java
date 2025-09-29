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
    public int avaliar() {
        throw new UnsupportedOperationException(
            "VariableReference '" + name + "' precisa de ambiente para avaliação."
        );
    }

    @Override
    public void printTree(String indent) {
        System.out.println(indent + "VarRef " + name);
    }

    @Override
    public String toString() {
        return name;
    }
}
