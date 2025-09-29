package syntacticanalysis;

import java.util.List;
import syntacticanalysis.commands.Command;

public class FunDeclaration extends Declaration {
    private final String name;
    private final List<String> parameters;
    private final List<VarDeclaration> locals;   // vars locais
    private final List<Command> commands;        // corpo da função

    public FunDeclaration(String name,
                          List<String> parameters,
                          List<VarDeclaration> locals,
                          List<Command> commands) {
        this.name = name;
        this.parameters = parameters;
        this.locals = locals;
        this.commands = commands;
    }

    public String getName() { return name; }
    public List<String> getParameters() { return parameters; }
    public List<VarDeclaration> getLocals() { return locals; }
    public List<Command> getCommands() { return commands; }

    @Override
    public void printTree(String prefix) {
        // Cabeçalho da função
        System.out.println(prefix + "Fun " + name + "(" + String.join(", ", parameters) + ")");

        // Locals
        if (locals != null && !locals.isEmpty()) {
            System.out.println(prefix + "  Locals:");
            for (VarDeclaration v : locals) {
                v.printTree(prefix + "    ");
            }
        } else {
            System.out.println(prefix + "  Locals (none)");
        }

        // Body
        if (commands != null && !commands.isEmpty()) {
            System.out.println(prefix + "  Body:");
            for (Command c : commands) {
                c.printTree(prefix + "    ");
            }
        } else {
            System.out.println(prefix + "  Body (empty)");
        }
    }
}
