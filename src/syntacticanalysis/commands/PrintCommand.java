package syntacticanalysis.commands;

import syntacticanalysis.expression.Expression;

public class PrintCommand implements Command {
    private final Expression expr;

    public PrintCommand(Expression expr) {
        this.expr = expr;
    }

    public Expression getExpr() {
        return expr;
    }

    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "Print");
        expr.printTree(prefix + "  ");
    }
}