package syntacticanalysis.expression;

import java.util.List;

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
    public void printTree(String prefix) {
        System.out.println(prefix + "Call " + name);
        for (int i = 0; i < args.size(); i++) {
            System.out.print(prefix + "  Arg" + i + ": ");
            args.get(i).printTree("");
        }
    }
}
