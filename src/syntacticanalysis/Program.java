package syntacticanalysis;

import syntacticanalysis.commands.AssignCommand;
import syntacticanalysis.commands.Command;
import syntacticanalysis.commands.IfCommand;
import syntacticanalysis.commands.WhileCommand;
import syntacticanalysis.expression.BinaryOperation;
import syntacticanalysis.expression.Expression;

import java.util.List;

public class Program {
    private final List<Declaration> declarations;
    private final Expression finalExpression;
    private final List<Command> commands;

    public Program(List<Declaration> decls, List<Command> commands, Expression ret) {
        this.declarations = decls;
        this.commands = commands;
        this.finalExpression = ret;
    }

    public List<Command> getCommands() { return commands; }
    public List<Declaration> getDeclarations() { return declarations; }
    public Expression getFinalExpression() { return finalExpression; }

    public void printTree() {
        System.out.println("Program");

        // 1) Declarações
        if (!declarations.isEmpty()) {
            System.out.println("├── Declarations");
            for (int i = 0; i < declarations.size(); i++) {
                Declaration d = declarations.get(i);
                boolean lastDecl = (i == declarations.size() - 1);

                String branch = lastDecl ? "└── " : "├── ";
                String childPrefix = lastDecl ? "    " : "│   ";

                System.out.println("│   " + branch + "Decl " + d.getVariableName());
                printExpr(d.getExpression(), "│   " + childPrefix, true);
            }
        } else {
            System.out.println("├── Declarations (none)");
        }

        // 2) Comandos
        if (!commands.isEmpty()) {
            System.out.println("├── Commands");
            for (int i = 0; i < commands.size(); i++) {
                Command c = commands.get(i);
                boolean lastCmd = (i == commands.size() - 1);
                String branch = lastCmd ? "└── " : "├── ";
                String childPrefix = lastCmd ? "    " : "│   ";
                printCmd(c, "│   " + branch, "│   " + childPrefix);
            }
        } else {
            System.out.println("├── Commands (none)");
        }

        System.out.println("└── Return");
        printExpr(finalExpression, "    ", true);
    }

    private void printCmd(Command command, String branch, String childPrefix) {
        if (command instanceof AssignCommand(String var, Expression expr)) {
            System.out.println(branch + "Assign " + var);
            printExpr(expr, childPrefix, true);

        } else if (command instanceof IfCommand(Expression cond, List<Command> thenPart, List<Command> elsePart)) {
            System.out.println(branch + "If");
            System.out.println(childPrefix + "├── Cond");
            printExpr(cond, childPrefix + "│   ", true);

            System.out.println(childPrefix + "├── Then");
            if (thenPart.isEmpty()) {
                System.out.println(childPrefix + "│   (empty)");
            } else {
                for (int k = 0; k < thenPart.size(); k++) {
                    boolean last = (k == thenPart.size() - 1);
                    String b = last ? "└── " : "├── ";
                    String cp = last ? "    " : "│   ";
                    printCmd(thenPart.get(k), childPrefix + "│   " + b,
                            childPrefix + "│   " + cp);
                }
            }

            System.out.println(childPrefix + "└── Else");
            if (elsePart.isEmpty()) {
                System.out.println(childPrefix + "    (empty)");
            } else {
                for (int k = 0; k < elsePart.size(); k++) {
                    boolean last = (k == elsePart.size() - 1);
                    String b = last ? "└── " : "├── ";
                    String cp = last ? "    " : "│   ";
                    printCmd(elsePart.get(k), childPrefix + "    " + b,
                            childPrefix + "    " + cp);
                }
            }

        } else if (command instanceof WhileCommand(Expression cond, List<Command> body)) {
            System.out.println(branch + "While");
            System.out.println(childPrefix + "├── Cond");
            printExpr(cond, childPrefix + "│   ", true);

            System.out.println(childPrefix + "└── Body");
            if (body.isEmpty()) {
                System.out.println(childPrefix + "    (empty)");
            } else {
                for (int k = 0; k < body.size(); k++) {
                    boolean last = (k == body.size() - 1);
                    String b = last ? "└── " : "├── ";
                    String cp = last ? "    " : "│   ";
                    printCmd(body.get(k), childPrefix + "    " + b,
                            childPrefix + "    " + cp);
                }
            }
        }
    }
    private void printExpr(Expression e, String prefix, boolean isLast) {

        if (e instanceof BinaryOperation bin) {
            String branch = isLast ? "└── " : "├── ";
            System.out.println(prefix + branch + bin.getOperator().getValor());
            String nextPrefix = prefix + (isLast ? "    " : "│   ");
            printExpr(bin.getLeft(),  nextPrefix, false);
            printExpr(bin.getRight(), nextPrefix, true);

        } else {

            String branch = isLast ? "└── " : "├── ";
            System.out.println(prefix + branch + e);

        }
    }
}
