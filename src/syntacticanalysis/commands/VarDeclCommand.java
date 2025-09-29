package syntacticanalysis.commands;

import syntacticanalysis.expression.Expression;

public record VarDeclCommand(String name, Expression expr) implements Command {
    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "VarDecl " + name + " =");
        expr.printTree(prefix + "  ");
    }
}
