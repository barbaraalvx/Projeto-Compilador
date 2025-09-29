package syntacticanalysis;

import syntacticanalysis.expression.Expression;

public class VarDeclaration extends Declaration {
    private final String name;
    private final Expression expression;

    public VarDeclaration(String name, Expression expression) {
        this.name = name;
        this.expression = expression;
    }

    public String getName() { return name; }
    public Expression getExpression() { return expression; }

    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "Var " + name + " = " + expression);
    }
}
