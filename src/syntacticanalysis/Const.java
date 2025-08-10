package syntacticanalysis;

public class Const extends Expression {

    private final int value;

    public Const(int value) {
        this.value = value;
    }

    @Override
    public int avaliar() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
    public int getValor() {
        return value;
    }
}
