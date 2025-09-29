package syntacticanalysis.expression;

public abstract class Expression {
    public abstract int avaliar();
    /**
     * Imprime a árvore da expressão.
     * @param indent nível de indentação para melhor visualização
     */
    public abstract void printTree(String indent);

    // Versão padrão, sem indentação inicial
    public void printTree() {
        printTree("");
    }
}
