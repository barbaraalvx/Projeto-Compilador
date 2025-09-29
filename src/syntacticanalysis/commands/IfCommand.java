package syntacticanalysis.commands;

import java.util.List;
import syntacticanalysis.expression.Expression;

public record IfCommand(Expression cond, List<Command> thenPart, List<Command> elsePart) implements Command {
    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "If");
        System.out.println(prefix + "  Condition:");
        cond.printTree(prefix + "    ");

        System.out.println(prefix + "  Then:");
        for (Command c : thenPart) c.printTree(prefix + "    ");

        System.out.println(prefix + "  Else:");
        for (Command c : elsePart) c.printTree(prefix + "    ");
    }
}
