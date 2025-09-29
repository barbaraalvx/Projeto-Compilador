package syntacticanalysis.commands;

import java.util.List;
import syntacticanalysis.expression.Expression;

public record IfCommand(Expression cond, List<Command> thenPart, List<Command> elsePart) implements Command {
    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "If (" + cond + ")");
        System.out.println(prefix + "  Then:");
        if (thenPart != null) {
            for (Command c : thenPart) c.printTree(prefix + "    ");
        }
        System.out.println(prefix + "  Else:");
        if (elsePart != null) {
            for (Command c : elsePart) c.printTree(prefix + "    ");
        }
    }
}
