package syntacticanalysis.commands;

import syntacticanalysis.expression.Expression;

public record AssignCommand(String var, Expression expr) implements Command {
    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "Assign " + var + " = " + expr);
    }
}
