package syntacticanalysis.commands;

import syntacticanalysis.expression.Expression;

import java.util.List;

public final class IfCmd implements Cmd {
    public final Expression cond;
    public final List<Cmd> thenPart;
    public final List<Cmd> elsePart;
    public IfCmd(Expression cond, List<Cmd> thenPart, List<Cmd> elsePart) {
        this.cond = cond; this.thenPart = thenPart; this.elsePart = elsePart;
    }
}
