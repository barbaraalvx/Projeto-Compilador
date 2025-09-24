package syntacticanalysis;

import syntacticanalysis.expression.BinaryOperation;
import syntacticanalysis.expression.Expression;

public class Declaration {
    private final String variableName;
    private final Expression expression;

    public Declaration(String variableName, Expression expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    public String getVariableName() { return variableName; }
    public Expression getExpression() { return expression; }

    /** Imprime a declaração como nó da árvore.
     *  @param prefix prefixo de desenho (ex.: "", "│   ", "    ")
     *  @param isLast se esta declaração é a última entre suas irmãs
     */
    public void printTree(String prefix, boolean isLast) {
        String branch = isLast ? "└── " : "├── ";
        System.out.println(prefix + branch + "Decl " + variableName);

        // prefixo para os filhos desta declaração
        String childPrefix = prefix + (isLast ? "    " : "│   ");

        // imprime a expressão associada à declaração
        printExpr(expression, childPrefix, true);
    }

    // ----- helpers -----

    private void printExpr(Expression e, String prefix, boolean isLast) {
        String branch = isLast ? "└── " : "├── ";
        if (e instanceof BinaryOperation bo) {
            System.out.println(prefix + branch + ((BinaryOperation) e).getOperator().getValor());
            String next = prefix + (isLast ? "    " : "│   ");
            printExprChild(bo.getLeft(),  next, false);
            printExprChild(bo.getRight(), next, true);
        } else {
            System.out.println(prefix + branch + e);
        }
    }

    private void printExprChild(Expression e, String prefix, boolean isLast) {
        String branch = isLast ? "└── " : "├── ";
        if (e instanceof BinaryOperation bo) {
            System.out.println(prefix + branch + bo.getOperator().getValor());
            String next = prefix + (isLast ? "    " : "│   ");
            printExprChild(bo.getLeft(),  next, false);
            printExprChild(bo.getRight(), next, true);
        } else {
            System.out.println(prefix + branch + e);
        }
    }

    @Override
    public String toString() {
        return "Decl(" + variableName + " = " + expression + ")";
    }
}
