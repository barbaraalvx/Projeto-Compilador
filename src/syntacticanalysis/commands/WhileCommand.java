package syntacticanalysis.commands;

import syntacticanalysis.expression.Expression;

import java.util.List;

public record WhileCommand(Expression cond, List<Command> body) implements Command {
}
