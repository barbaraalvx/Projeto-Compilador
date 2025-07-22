package syntacticanalysis;

import java.util.List;
import java.util.Objects;

public class Parser {

    private final List<Token> tokens;
    private int position;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    //olha o proximo sem consumir
    private Token lookNextToken() {
        return position < tokens.size() ? tokens.get(position) : null;
    }

    //consome o proximo token
    private Token nextToken() {
        return position < tokens.size() ? tokens.get(position++) : null;
    }

    //faz a verificação do tipo de token esperado
    private void expect(TypeEnum expected) {
        Token t = nextToken();
        if (Objects.requireNonNull(t).getType() != expected) {
            throw new RuntimeException(
                    String.format("Erro sintático em %s: esperado %s, mas veio %s", t.getPosition(), expected, t.getType())
            );
        }
    }

    private TypeOperator parseOperator() {
        Token tok = nextToken();
        return switch (Objects.requireNonNull(tok).getType()) {
            case SOMA -> TypeOperator.SOMA;
            case SUBTRACAO -> TypeOperator.SUBTRACAO;
            case MULTIPLICACAO -> TypeOperator.MULTIPLICACAO;
            case DIVISAO -> TypeOperator.DIVISAO;
            default -> throw new RuntimeException(
                    String.format("Erro sintático em %s: operador esperado, mas veio '%s'", tok.getPosition(), tok.getLexeme())
            );
        };
    }

    public Expression parseExpression() {
        Token tok = lookNextToken();

        switch (Objects.requireNonNull(tok).getType()) {
            case DIGITO:
                nextToken();
                int value = Integer.parseInt(tok.getLexeme());
                return new Const(value);

            case ABRE_PARENTESES:
                nextToken(); // consome '('
                Expression left = parseExpression();
                TypeOperator operator = parseOperator();
                Expression right = parseExpression();
                expect(TypeEnum.FECHA_PARENTESES);
                return new BinaryOperation(operator, left, right);

            default:
                throw new RuntimeException(
                        String.format("Erro sintático em %s: token inesperado '%s'", tok.getPosition(), tok.getLexeme())
                );
        }
    }
}
