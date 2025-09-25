package syntacticanalysis.commands;

import syntacticanalysis.expression.Expression;

import java.util.List;

public record IfCommand(Expression cond, List<Command> thenPart, List<Command> elsePart) implements Command {
}
