package syntacticanalysis;

public class Declaration {
    private final String variableName;
    private final Expression expression;

    public Declaration(String variableName, Expression expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public Expression getExpression() {
        return expression;
    }

    // Método para imprimir a declaração na árvore.
    public void printTree(String prefix) {
        System.out.println(prefix + "├── Declaration: " + variableName);
        if (expression instanceof BinaryOperation) {
            ((BinaryOperation) expression).printTree(prefix + "│   ", true);
        } else {
            System.out.println(prefix + "└── " + expression);
        }
    }
}
