package syntacticanalysis;

import syntacticanalysis.commands.AssignCmd;
import syntacticanalysis.commands.Cmd;
import syntacticanalysis.commands.IfCmd;
import syntacticanalysis.commands.WhileCmd;
import syntacticanalysis.expression.BinaryOperation;
import syntacticanalysis.expression.Expression;

import java.util.List;

public class Program {
    private final List<Declaration> declarations;
    private final Expression finalExpression;
    private final List<Cmd> commands;

    public Program(List<Declaration> decls, List<Cmd> cmds, Expression ret) {
        this.declarations = decls;
        this.commands = cmds;
        this.finalExpression = ret;
    }

    public List<Cmd> getCommands() { return commands; }
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
                Cmd c = commands.get(i);
                boolean lastCmd = (i == commands.size() - 1);
                String branch = lastCmd ? "└── " : "├── ";
                String childPrefix = lastCmd ? "    " : "│   ";
                printCmd(c, "│   " + branch, "│   " + childPrefix);
            }
        } else {
            System.out.println("├── Commands (none)");
        }

        // 3) Return
        System.out.println("└── Return");
        printExpr(finalExpression, "    ", true);
    }

    private void printCmd(Cmd cmd, String branch, String childPrefix) {
        if (cmd instanceof AssignCmd a) {
            System.out.println(branch + "Assign " + a.var);
            printExpr(a.expr, childPrefix, true);

        } else if (cmd instanceof IfCmd i) {
            System.out.println(branch + "If");
            System.out.println(childPrefix + "├── Cond");
            printExpr(i.cond, childPrefix + "│   ", true);

            System.out.println(childPrefix + "├── Then");
            if (i.thenPart.isEmpty()) {
                System.out.println(childPrefix + "│   (empty)");
            } else {
                for (int k = 0; k < i.thenPart.size(); k++) {
                    boolean last = (k == i.thenPart.size() - 1);
                    String b = last ? "└── " : "├── ";
                    String cp = last ? "    " : "│   ";
                    printCmd(i.thenPart.get(k), childPrefix + "│   " + b,
                            childPrefix + "│   " + cp);
                }
            }

            System.out.println(childPrefix + "└── Else");
            if (i.elsePart.isEmpty()) {
                System.out.println(childPrefix + "    (empty)");
            } else {
                for (int k = 0; k < i.elsePart.size(); k++) {
                    boolean last = (k == i.elsePart.size() - 1);
                    String b = last ? "└── " : "├── ";
                    String cp = last ? "    " : "│   ";
                    printCmd(i.elsePart.get(k), childPrefix + "    " + b,
                            childPrefix + "    " + cp);
                }
            }

        } else if (cmd instanceof WhileCmd w) {
            System.out.println(branch + "While");
            System.out.println(childPrefix + "├── Cond");
            printExpr(w.cond, childPrefix + "│   ", true);

            System.out.println(childPrefix + "└── Body");
            if (w.body.isEmpty()) {
                System.out.println(childPrefix + "    (empty)");
            } else {
                for (int k = 0; k < w.body.size(); k++) {
                    boolean last = (k == w.body.size() - 1);
                    String b = last ? "└── " : "├── ";
                    String cp = last ? "    " : "│   ";
                    printCmd(w.body.get(k), childPrefix + "    " + b,
                            childPrefix + "    " + cp);
                }
            }
        }
    }
    // --------- helpers para imprimir qualquer Expression como árvore ---------

    private void printExpr(Expression e, String prefix, boolean isLast) {
        // Se for BinaryOperation, imprime o operador e desce pros filhos
        if (e instanceof BinaryOperation bin) {
            String branch = isLast ? "└── " : "├── ";
            System.out.println(prefix + branch + bin.getOperator().getValor());
            // filhos
            String nextPrefix = prefix + (isLast ? "    " : "│   ");
            // left (não-último)
            printExprChild(bin.getLeft(),  nextPrefix, false);
            // right (último)
            printExprChild(bin.getRight(), nextPrefix, true);
        } else {
            // Folhas: Const, VariableReference, etc.
            String branch = isLast ? "└── " : "├── ";
            System.out.println(prefix + branch + e);
        }
    }

    private void printExprChild(Expression e, String prefix, boolean isLast) {
        if (e instanceof BinaryOperation bin) {
            String branch = isLast ? "└── " : "├── ";
            System.out.println(prefix + branch + bin.getOperator().getValor());
            String nextPrefix = prefix + (isLast ? "    " : "│   ");
            printExprChild(bin.getLeft(),  nextPrefix, false);
            printExprChild(bin.getRight(), nextPrefix, true);
        } else {
            String branch = isLast ? "└── " : "├── ";
            System.out.println(prefix + branch + e);
        }
    }
}
