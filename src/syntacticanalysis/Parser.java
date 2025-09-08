package syntacticanalysis;

import java.util.ArrayList;
import java.util.List;
import syntacticanalysis.exceptions.SyntaxException;

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

    // Sobrecarga de expect para mensagens de erro mais detalhadas (se quiser usar)
    private Token expect(String expected, TypeEnum... possibleTypes) throws SyntaxException {
        Token token = nextToken();
        boolean matches = false;
        if (token != null) {
            for (TypeEnum type : possibleTypes) {
                if (token.getType() == type) {
                    matches = true;
                    break;
                }
            }
        }
        if (!matches) {
            throw new SyntaxException(
                    token.getPosition(),
                    TypeEnum.DIGITO,
                    token.getType()
            );
        }
        return token;
    }

    // ===== entrada principal (agora é a análise do programa) =====
    public Program parse() throws SyntaxException {
        return parseProgram();
    }

    // <programa> ::= <decl>* <result>
    private Program parseProgram() throws SyntaxException {
        List<Declaration> declarations = new ArrayList<>();
        Token tok = lookNextToken();

        // declarações enquanto iniciar por IDENTIFICADOR
        while (tok != null && tok.getType() == TypeEnum.IDENTIFICADOR) {
            Declaration dec = parseDeclaration();
            declarations.add(dec);
            tok = lookNextToken();
        }

        // expressão final deve começar com '='
        expect(TypeEnum.IGUAL);
        Expression finalExpression = parseExpA();

        // arquivo deve terminar após a expressão final
        expect(TypeEnum.EOF);

        return new Program(declarations, finalExpression);
    }

    // <decl> ::= <ident> '=' <exp> ';'
    private Declaration parseDeclaration() throws SyntaxException {
        Token identToken = expect(TypeEnum.IDENTIFICADOR);
        expect(TypeEnum.IGUAL);
        Expression expression = parseExpA();
        expect(TypeEnum.PONTO_VIRGULA);
        return new Declaration(identToken.getLexeme(), expression);
    }

    // <exp> ::= <exp_m> (('+' | '-') <exp_m>)*
    private Expression parseExpA() throws SyntaxException {
        Expression left = parseExpM();
        while (peek(TypeEnum.SOMA) || peek(TypeEnum.SUBTRACAO)) {
            Token opTok = nextToken();
            Expression right = parseExpM();
            TypeEnum op = (opTok.getType() == TypeEnum.SOMA)
                ? TypeEnum.SOMA
                : TypeEnum.SUBTRACAO;
            left = new BinaryOperation(op, left, right);
        }
        return left;
    }

    // <exp_m> ::= <prim> (('*' | '/') <prim>)*
    private Expression parseExpM() throws SyntaxException {
        Expression left = parsePrim();
        while (peek(TypeEnum.MULTIPLICACAO) || peek(TypeEnum.DIVISAO)) {
            Token opTok = nextToken();
            Expression right = parsePrim();
            TypeEnum op = (opTok.getType() == TypeEnum.MULTIPLICACAO)
                ? TypeEnum.MULTIPLICACAO
                : TypeEnum.DIVISAO;
            left = new BinaryOperation(op, left, right);
        }
        return left;
    }

    // <prim> ::= <num> | <ident> | '(' <exp> ')'
    private Expression parsePrim() throws SyntaxException {
        Token token = lookNextToken();
        if (token == null) {
            throw new SyntaxException(new Position(-1, -1, -1), null, null);
        }

        switch (token.getType()) {
            case DIGITO -> {
                nextToken();
                int value = Integer.parseInt(token.getLexeme());
                return new Const(value);
            }
            case IDENTIFICADOR -> {
                nextToken();
                return new VariableReference(token.getLexeme());
            }
            case ABRE_PARENTESES -> {
                nextToken();
                Expression inner = parseExpA();
                expect(TypeEnum.FECHA_PARENTESES);
                return inner;
            }
            default -> {
                throw new SyntaxException(
                        token.getPosition(),
                        TypeEnum.DIGITO,
                        token.getType()
                );
            }
        }
    }
}
