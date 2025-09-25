package syntacticanalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import syntacticanalysis.commands.AssignCommand;
import syntacticanalysis.commands.Command;
import syntacticanalysis.commands.IfCommand;
import syntacticanalysis.commands.WhileCommand;
import syntacticanalysis.exceptions.SyntaxException;
import syntacticanalysis.expression.BinaryOperation;
import syntacticanalysis.expression.Const;
import syntacticanalysis.expression.Expression;
import lexicalanalysis.Position;
import lexicalanalysis.Token;
import lexicalanalysis.TypeEnum;

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
    private Token expect(String expectedMsg, TypeEnum... possibleTypes) throws SyntaxException {
        Token token = nextToken();
        boolean matches = false;
        if (token != null) {
            for (TypeEnum t : possibleTypes) {
                if (token.getType() == t) {
                    matches = true;
                    break;
                }
            }
        }
        if (!matches) {
            throw new SyntaxException(
                    token != null ? token.getPosition() : new Position(-1, -1, -1),
                    possibleTypes.length > 0 ? possibleTypes[0] : null,
                    token != null ? token.getType() : null
            );
        }
        return token;
    }

    // ===== entrada principal (agora é a análise do programa) =====
    public Program parse() throws SyntaxException {
        return parseProgram();
    }

    // <programa> ::= <decl>* '{' 'return' <exp> ';' '}'
    private Program parseProgram() throws SyntaxException {
        List<Declaration> declarations = new ArrayList<>();
        while (peek(TypeEnum.IDENTIFICADOR)) {
            declarations.add(parseDeclaration());
        }
        expect(TypeEnum.LCHAVE);

        List<Command> commands = parseCmdsUntilReturn();

        expect(TypeEnum.RETURN);
        Expression result = parseExpC();
        expect(TypeEnum.PONTO_VIRGULA);
        expect(TypeEnum.RCHAVE);
        expect(TypeEnum.EOF);

        return new Program(declarations, commands, result);
    }

    // <decl> ::= <ident> '=' <exp> ';'
    private Declaration parseDeclaration() throws SyntaxException {

        Token identToken = expect(TypeEnum.IDENTIFICADOR);
        expect(TypeEnum.IGUAL);
        Expression expression = parseExpC();
        expect(TypeEnum.PONTO_VIRGULA);

        return new Declaration(identToken.getLexeme(), expression);
    }

    // expC ::= expA ( (MENOR | MAIOR | IGUAL_IGUAL) expA )*
    private Expression parseExpC() throws SyntaxException {
        Expression left = parseExpA(); // já existente

        while (peek(TypeEnum.MENOR) || peek(TypeEnum.MAIOR) || peek(TypeEnum.IGUALDADE)) {
            Token opTok = nextToken();
            Expression right = parseExpA();
            TypeEnum op = opTok.getType(); // MENOR, MAIOR, IGUAL_IGUAL
            left = new BinaryOperation(op, left, right);
        }

        return left;
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
                Expression inner = parseExpC(); // <-- em vez de parseExpA()
                expect(TypeEnum.FECHA_PARENTESES);
                return inner;
            }
            default -> throw new SyntaxException(
                    token.getPosition(),
                    TypeEnum.DIGITO,
                    token.getType()
            );
        }
    }

    private List<Command> parseCmdsUntilReturn() throws SyntaxException {
        List<Command> commands = new ArrayList<>();
        while (!peek(TypeEnum.RETURN)) {
            commands.add(parseCmd());
        }
        return commands;
    }

    private Command parseCmd() throws SyntaxException {
        if (peek(TypeEnum.IF)) return parseIf();
        if (peek(TypeEnum.WHILE)) return parseWhile();

        Token id = expect(TypeEnum.IDENTIFICADOR);
        expect(TypeEnum.IGUAL);
        Expression e = parseExpC();
        expect(TypeEnum.PONTO_VIRGULA);
        return new AssignCommand(id.getLexeme(), e);
    }

    private IfCommand parseIf() throws SyntaxException {
        expect(TypeEnum.IF);
        Expression cond = parseExpC();
        List<Command> thenPart = parseBracedCmds();
        expect(TypeEnum.ELSE);
        List<Command> elsePart = parseBracedCmds();
        return new IfCommand(cond, thenPart, elsePart);
    }

    private WhileCommand parseWhile() throws SyntaxException {
        expect(TypeEnum.WHILE);
        Expression cond = parseExpC();
        List<Command> body = parseBracedCmds();
        return new WhileCommand(cond, body);
    }

    private List<Command> parseBracedCmds() throws SyntaxException {
        expect(TypeEnum.LCHAVE);
        List<Command> xs = new ArrayList<>();
        while (!peek(TypeEnum.RCHAVE)) {
            if (peek(TypeEnum.RETURN)) {
                throw new SyntaxException(Objects.requireNonNull(lookNextToken()).getPosition(), TypeEnum.IDENTIFICADOR, TypeEnum.RETURN);
            }
            xs.add(parseCmd());
        }
        expect(TypeEnum.RCHAVE);
        return xs;
    }

}
