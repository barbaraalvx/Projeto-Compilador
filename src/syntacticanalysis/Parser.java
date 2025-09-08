package syntacticanalysis;

import syntacticanalysis.exceptions.SyntaxException;
import java.util.ArrayList;
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

    // Sobrecarga de expect para mensagens de erro mais detalhadas
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
                token != null ? token.getPosition() : new Position(-1, -1, -1),
                expected,
                token != null ? token.getType() : null
            );
        }
        return token;
    }

    // ===== entrada principal (agora é a análise do programa) =====
    public Program parse() throws SyntaxException {
        return parseProgram();
    }

    // Função que analisa o programa completo: <programa> ::= <decl>* <result>
    private Program parseProgram() throws SyntaxException {
        List<Declaration> declarations = new ArrayList<>();
        Token tok = lookNextToken();

        // Loop para analisar as declarações
        while (tok != null && tok.getType() == TypeEnum.IDENTIFICADOR) {
            Declaration dec = parseDeclaration();
            declarations.add(dec);
            tok = lookNextToken();
        }

        // Analisa a expressão final, que deve começar com '='
        expect(TypeEnum.IGUAL);
        Expression finalExpression = parseExpA();
        
        // Garante que o arquivo terminou após a expressão final
        if (lookNextToken().getType() != TypeEnum.EOF) {
             throw new SyntaxException(lookNextToken().getPosition(), TypeEnum.EOF, lookNextToken().getType());
        }

        return new Program(declarations, finalExpression);
    }
    
    // Função que analisa uma única declaração: <decl> ::= <ident> '=' <exp>';'
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
                    ? TypeEnum.SOMA : TypeEnum.SUBTRACAO;
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
                    ? TypeEnum.MULTIPLICACAO : TypeEnum.DIVISAO;
            left = new BinaryOperation(op, left, right);
        }
        return left;
    }

    // <prim> ::= <num> | <ident> | '('<exp>')'
    private Expression parsePrim() throws SyntaxException {
        Token tok = lookNextToken();
        if (tok == null) {
            throw new SyntaxException(new Position(-1, -1, -1), null, null);
        }

        switch (tok.getType()) {
            case DIGITO -> {
                nextToken(); // consome número
                int value = Integer.parseInt(tok.getLexeme());
                return new Const(value);
            }
            case IDENTIFICADOR -> {
                nextToken(); // consome o token do identificador
                return new VariableReference(tok.getLexeme());
            }
            case ABRE_PARENTESES -> {
                nextToken(); // consome '('
                Expression inner = parseExpA();
                expect(TypeEnum.FECHA_PARENTESES);
                return inner;
            }
            default -> {
                throw new SyntaxException(
                        tok.getPosition(),
                        "number, identifier or '('",
                        tok.getType()
                );
            }
        }
    }
}