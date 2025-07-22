package syntacticanalysis;

public enum TypeOperator {
    SOMA("+"),
    SUBTRACAO("-"),
    MULTIPLICACAO("*"),
    DIVISAO("/");

    private final String label;

    TypeOperator(String label) {
        this.label = label;
    }

    public String getValor() {
        return label;
    }
}
