package syntacticanalysis;

public enum TypeEnum {
    ABRE_PARENTESES("("),
    FECHA_PARENTESES(")"),
    DIGITO("digito"),
    SOMA("+"),
    SUBTRACAO("-"),
    MULTIPLICACAO("*"),
    DIVISAO("/"),
    EOF("endOfFile");

    private final String label;

    TypeEnum(String label) {
        this.label = label;
    }

    public String getValor() {
        return label;
    }
}
