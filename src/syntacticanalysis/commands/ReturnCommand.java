package syntacticanalysis.commands;

import syntacticanalysis.expression.Expression;

public record ReturnCommand(Expression expr) implements Command {
    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "Return");
        expr.printTree(prefix + "  ");
    }
}
