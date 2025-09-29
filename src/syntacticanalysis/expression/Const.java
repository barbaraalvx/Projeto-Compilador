package syntacticanalysis.expression;

public class Const extends Expression {
    private final int valor;

    public Const(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "Const " + valor);
    }
}
