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

    // ===== utilitários de leitura =====
    // olha o próximo sem consumir
    private Token lookNextToken() {
        return position < tokens.size() ? tokens.get(position) : null;
    }

    // consome o próximo token
    private Token nextToken() {
        return position < tokens.size() ? tokens.get(position++) : null;
    }

    // true se próximo token é do tipo t (não consome)
    private boolean peek(TypeEnum t) {
        Token tok = lookNextToken();
        return tok != null && tok.getType() == t;
    }

    // consome e garante tipo esperado
    private Token expect(TypeEnum expected) throws SyntaxException {
        Token token = nextToken();
        if (token == null || token.getType() != expected) {
            throw new SyntaxException(
                    token != null ? token.getPosition() : new Position(-1, -1, -1),
                    expected,
                    token != null ? token.getType() : null
            );
        }
        return token;
    }

    private void expect() throws SyntaxException {
        expect(TypeEnum.FECHA_PARENTESES);
    }

    // ===== entrada principal =====
    public Expression parse() {
        Expression ast = parseExpA();
        Token tok = lookNextToken();
        if (tok != null && tok.getType() != TypeEnum.EOF) {
            throw new RuntimeException(String.format(
                    "Erro sintático em %s: tokens restantes a partir de '%s'",
                    tok.getPosition(), tok.getLexeme()
            ));
        }
        return ast;
    }

    // <exp_a> ::= <exp_m> ((+|-) <exp_m>)*
    private Expression parseExpA() {
        Expression left = parseExpM();
        while (peek(TypeEnum.SOMA) || peek(TypeEnum.SUBTRACAO)) {
            Token opTok = nextToken();          // consome + ou -
            Expression right = parseExpM();     // lê próximo termo
            TypeEnum op = (opTok.getType() == TypeEnum.SOMA)
                    ? TypeEnum.SOMA : TypeEnum.SUBTRACAO;
            left = new BinaryOperation(op, left, right); // associativo à esquerda
        }
        return left;
    }

    // <exp_m> ::= <prim> ((*|/) <prim>)*
    private Expression parseExpM() {
        Expression left = parsePrim();
        while (peek(TypeEnum.MULTIPLICACAO) || peek(TypeEnum.DIVISAO)) {
            Token opTok = nextToken();          // consome * ou /
            Expression right = parsePrim();
            TypeEnum op = (opTok.getType() == TypeEnum.MULTIPLICACAO)
                    ? TypeEnum.MULTIPLICACAO : TypeEnum.DIVISAO;
            left = new BinaryOperation(op, left, right); // associativo à esquerda
        }
        return left;
    }

    // <prim> ::= <num> | '(' <exp_a> ')'
    private Expression parsePrim() {
        Token tok = lookNextToken();
        if (tok == null) {
            throw new RuntimeException("Erro sintático: fim inesperado de entrada");
        }

        switch (tok.getType()) {
            case DIGITO -> {
                nextToken(); // consome número
                int value = Integer.parseInt(tok.getLexeme());
                return new Const(value);
            }
            case ABRE_PARENTESES -> {
                nextToken();             // consome '('
                Expression inner = parseExpA();
                try {
                    expect(TypeEnum.FECHA_PARENTESES);
                } catch (SyntaxException e) {
                    throw new RuntimeException(e);
                }
                return inner;
            }
            default -> {
                throw new RuntimeException(String.format(
                        "Erro sintático em %s: esperado número ou '(', mas veio '%s'",
                        tok.getPosition(), tok.getLexeme()
                ));
            }
        }
    }
}
