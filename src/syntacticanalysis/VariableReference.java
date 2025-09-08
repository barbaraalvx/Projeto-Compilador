package syntacticanalysis;

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
    public String toString() {
        return name;
    }
}
