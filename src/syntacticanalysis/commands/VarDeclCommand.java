package syntacticanalysis.commands;

import syntacticanalysis.expression.Expression;

/**
 * Comando para declaração de variável dentro de blocos ou main.
 * Exemplo: var x = 5;
 */
public record VarDeclCommand(String name, Expression expr) implements Command {
    @Override
    public void printTree(String indent) {
        System.out.println(indent + "VarDecl " + name + " =");
        expr.printTree(indent + "  ");
    }
}
