package lexicalanalysis;

public enum TypeEnum {
    ABRE_PARENTESES("("),
    FECHA_PARENTESES(")"),
    DIGITO("digito"),
    SOMA("+"),
    SUBTRACAO("-"),
    MULTIPLICACAO("*"),
    DIVISAO("/"),
    PONTO_VIRGULA(";"),
    IGUAL("="),
    IDENTIFICADOR("identificador"),
    EOF("endOfFile"),
    LCHAVE("{"),
    RCHAVE("}"),
    RETURN("return"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    MENOR("<"),
    MAIOR(">"),
    IGUALDADE("==");

    private final String label;

    TypeEnum(String label) {
        this.label = label;
    }

    public String getValor() {
        return label;
    }
}
