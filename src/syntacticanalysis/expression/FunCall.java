package syntacticanalysis.expression;

import java.util.List;
import java.util.stream.Collectors;

/** Expressão de chamada de função: nome(args...) */
public class FunCall extends Expression {
    private final String name;
    private final List<Expression> args;

    public FunCall(String name, List<Expression> args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public List<Expression> getArgs() {
        return args;
    }

    @Override
    public String toString() {
        String inside = (args == null || args.isEmpty())
                ? ""
                : args.stream().map(Object::toString).collect(Collectors.joining(", "));
        return name + "(" + inside + ")";
    }

    @Override
    public int avaliar() {
        return 0;
    }

    @Override
    public String toTreeString() {
        return name + "(" + args.stream()
                .map(Expression::toTreeString)
                .collect(Collectors.joining(", ")) + ")";
    }
}
