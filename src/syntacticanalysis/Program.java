package syntacticanalysis;

import java.util.List;
import syntacticanalysis.commands.Command;

public class Program {
    private final List<Declaration> declarations;
    private final List<Command> commands;

    public Program(List<Declaration> declarations, List<Command> commands) {
        this.declarations = declarations;
        this.commands = commands;
    }

    public List<Declaration> getDeclarations() {
        return declarations;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void printTree() {
        System.out.println("Program");

        if (!declarations.isEmpty()) {
            System.out.println("├── Declarations");
            for (Declaration d : declarations) {
                d.printTree("│   ");
            }
        } else {
            System.out.println("├── Declarations (none)");
        }

        if (!commands.isEmpty()) {
            System.out.println("├── Commands");
            for (Command c : commands) {
                c.printTree("│   ");
            }
        } else {
            System.out.println("├── Commands (none)");
        }

        System.out.println("└── End");
    }
}
