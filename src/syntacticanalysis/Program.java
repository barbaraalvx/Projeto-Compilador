package syntacticanalysis;

import java.util.List;
import syntacticanalysis.commands.Command;

public class Program {
    private final List<Declaration> declarations;
    private final List<Command> commands;

    // 🔹 NOVO: mantém compatibilidade com Parser
    public Program(List<Declaration> declarations, List<Command> commands) {
        this(declarations, commands, null);
    }

    // 🔹 Versão usada internamente (3 args)
    public Program(List<Declaration> declarations, List<Command> commands, Object ignored) {
        this.declarations = (declarations != null) ? declarations : List.of();
        this.commands     = (commands != null)     ? commands     : List.of();
    }

    public List<Declaration> getDeclarations() { return declarations; }
    public List<Command> getCommands() { return commands; }

    public void printTree() {
        System.out.println("Program");

        // Declarations
        if (!declarations.isEmpty()) {
            System.out.println("├── Declarations");
            for (int i = 0; i < declarations.size(); i++) {
                String prefix = (i == declarations.size() - 1 && commands.isEmpty()) ? "    " : "│   ";
                declarations.get(i).printTree(prefix);
            }
        } else {
            System.out.println("├── Declarations (none)");
        }

        // Commands (main)
        if (!commands.isEmpty()) {
            System.out.println("├── Commands");
            for (int i = 0; i < commands.size(); i++) {
                String prefix = (i == commands.size() - 1) ? "    " : "│   ";
                commands.get(i).printTree(prefix);
            }
        } else {
            System.out.println("├── Commands (none)");
        }

        System.out.println("└── End");
    }
}
