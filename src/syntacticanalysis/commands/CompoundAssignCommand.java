package syntacticanalysis.commands;

import lexicalanalysis.TypeEnum;
import syntacticanalysis.expression.Expression;

public record CompoundAssignCommand(String var, TypeEnum operator, Expression expr) implements Command {
    public void print(String prefix) {
        System.out.println(prefix + "CompoundAssign " + var + " " + operator.getValor() + " =");
        expr.printTree(prefix + "  ");
    }

    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "CompoundAssign " + var + " " + operator.getValor() + " =");
        expr.printTree(prefix + "  ");
    }
}
