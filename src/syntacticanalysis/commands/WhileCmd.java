package syntacticanalysis.commands;

import syntacticanalysis.expression.Expression;

import java.util.List;

public final class WhileCmd implements Cmd {
    public final Expression cond;
    public final List<Cmd> body;
    public WhileCmd(Expression cond, List<Cmd> body) { this.cond = cond; this.body = body; }
}
