package syntacticanalysis;

import java.util.List;
import syntacticanalysis.commands.Command;

public class FunDeclaration extends Declaration {
    private final String name;
    private final List<String> params;
    private final List<VarDeclaration> locals;
    private final List<Command> commands;

    public FunDeclaration(String name,
                          List<String> params,
                          List<VarDeclaration> locals,
                          List<Command> commands) {
        this.name = name;
        this.params = params;
        this.locals = locals;
        this.commands = commands;
    }

    public String getName() {
        return name;
    }

    public List<String> getParameters() {
        return params;
    }

    public List<VarDeclaration> getLocals() {
        return locals;
    }

    public List<Command> getCommands() {
        return commands;
    }

    @Override
    public void printTree(String prefix) {
        String paramsStr = String.join(", ", params);
        System.out.println(prefix + "Fun " + name + "(" + paramsStr + ")");

        if (!locals.isEmpty()) {
            System.out.println(prefix + "  Locals:");
            for (VarDeclaration v : locals) {
                v.printTree(prefix + "    ");
            }
        }

        if (!commands.isEmpty()) {
            System.out.println(prefix + "  Body:");
            for (Command c : commands) {
                c.printTree(prefix + "    ");
            }
        }
    }
}
