package syntacticanalysis.commands;

import syntacticanalysis.expression.Expression;

/**
 * Comando para declaração de variável dentro de blocos ou main.
 * Exemplo: var x = 5;
 */
public record VarDeclCommand(String name, Expression expr) implements Command {
    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "VarDecl " + name + " = " + expr.toTreeString());
    }
}
