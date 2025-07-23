package syntacticanalysis;

import syntacticanalysis.exceptions.SyntaxException;

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
    private void expect() throws SyntaxException {
        Token token = nextToken();
        if (token == null || token.getType() != TypeEnum.FECHA_PARENTESES) {
            throw new SyntaxException(
                    token != null ? token.getPosition() : new Position(-1, -1, -1),
                    TypeEnum.FECHA_PARENTESES,
                    (token != null) ? token.getType() : null
            );
        }
    }

    private TypeEnum parseOperator() {
        Token tok = nextToken();
        return switch (Objects.requireNonNull(tok).getType()) {
            case SOMA -> TypeEnum.SOMA;
            case SUBTRACAO -> TypeEnum.SUBTRACAO;
            case MULTIPLICACAO -> TypeEnum.MULTIPLICACAO;
            case DIVISAO -> TypeEnum.DIVISAO;
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
                TypeEnum operator = parseOperator();
                Expression right = parseExpression();
                expect();
                return new BinaryOperation(operator, left, right);

            default:
                throw new RuntimeException(
                        String.format("Erro sintático em %s: token inesperado '%s'", tok.getPosition(), tok.getLexeme())
                );
        }
    }
}
