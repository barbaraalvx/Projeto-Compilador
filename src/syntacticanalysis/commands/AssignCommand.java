package syntacticanalysis.commands;

import syntacticanalysis.expression.Expression;

public record AssignCommand(String var, Expression expr) implements Command {
}

