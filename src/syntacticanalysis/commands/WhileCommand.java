package syntacticanalysis.commands;

import java.util.List;
import syntacticanalysis.expression.Expression;

public record WhileCommand(Expression cond, List<Command> body) implements Command {
    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "While");
        System.out.println(prefix + "  Condition:");
        cond.printTree(prefix + "    ");
        System.out.println(prefix + "  Body:");
        for (Command c : body) c.printTree(prefix + "    ");
    }
}
