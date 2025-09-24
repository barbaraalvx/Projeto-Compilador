package syntacticanalysis.commands;

import syntacticanalysis.expression.Expression;

public final class AssignCmd implements Cmd {
    public final String var;
    public final Expression expr;
    public AssignCmd(String var, Expression expr) { this.var = var; this.expr = expr; }
}

