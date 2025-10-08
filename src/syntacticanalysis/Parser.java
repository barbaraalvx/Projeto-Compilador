package syntacticanalysis;

import java.util.ArrayList;
import java.util.List;
import lexicalanalysis.Position;
import lexicalanalysis.Token;
import lexicalanalysis.TypeEnum;
import syntacticanalysis.commands.*;
import syntacticanalysis.exceptions.SyntaxException;
import syntacticanalysis.expression.BinaryOperation;
import syntacticanalysis.expression.Const;
import syntacticanalysis.expression.Expression;
import syntacticanalysis.expression.FunCall;
import syntacticanalysis.expression.UnaryOperation;

public class Parser {

    private final List<Token> tokens;
    private int position;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    private Token lookNextToken() {
        return position < tokens.size() ? tokens.get(position) : null;
    }

    private Token nextToken() {
        return position < tokens.size() ? tokens.get(position++) : null;
    }

    private boolean peek(TypeEnum t) {
        Token tok = lookNextToken();
        return tok != null && tok.getType() == t;
    }

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

    public Program parse() throws SyntaxException {
        return parseProgram();
    }

    private Program parseProgram() throws SyntaxException {
        List<Declaration> declarations = new ArrayList<>();
        while (peek(TypeEnum.VAR) || peek(TypeEnum.FUN)) {
            declarations.add(parseDeclaration());
        }

        expect(TypeEnum.MAIN);
        expect(TypeEnum.LCHAVE);

        List<Command> commands = new ArrayList<>();
        while (!peek(TypeEnum.RCHAVE)) {
            commands.add(parseCmd());
        }

        expect(TypeEnum.RCHAVE);
        expect(TypeEnum.EOF);

        return new Program(declarations, commands);
    }

    private VarDeclaration parseVarDeclaration() throws SyntaxException {
        expect(TypeEnum.VAR);
        Token identToken = expect(TypeEnum.IDENTIFICADOR);
        expect(TypeEnum.IGUAL);
        Expression expression = parseExpL();
        expect(TypeEnum.PONTO_VIRGULA);
        return new VarDeclaration(identToken.getLexeme(), expression);
    }

    private FunDeclaration parseFunDeclaration() throws SyntaxException {
        expect(TypeEnum.FUN);
        Token identToken = expect(TypeEnum.IDENTIFICADOR);

        expect(TypeEnum.ABRE_PARENTESES);
        List<String> params = new ArrayList<>();
        if (!peek(TypeEnum.FECHA_PARENTESES)) {
            params.add(expect(TypeEnum.IDENTIFICADOR).getLexeme());
            while (peek(TypeEnum.VIRGULA)) {
                nextToken();
                params.add(expect(TypeEnum.IDENTIFICADOR).getLexeme());
            }
        }
        expect(TypeEnum.FECHA_PARENTESES);

        expect(TypeEnum.LCHAVE);

        List<VarDeclaration> locals = new ArrayList<>();
        while (peek(TypeEnum.VAR)) {
            locals.add(parseVarDeclaration());
        }

        List<Command> body = new ArrayList<>();
        while (!peek(TypeEnum.RCHAVE)) {
            body.add(parseCmd());
        }

        expect(TypeEnum.RCHAVE);

        return new FunDeclaration(identToken.getLexeme(), params, locals, body);
    }

    private Declaration parseDeclaration() throws SyntaxException {
        if (peek(TypeEnum.VAR)) return parseVarDeclaration();
        if (peek(TypeEnum.FUN)) return parseFunDeclaration();
        throw new SyntaxException(lookNextToken().getPosition(), null, lookNextToken().getType());
    }

    private Expression parseExpL() throws SyntaxException {
        Expression left = parseExpC();
        while (peek(TypeEnum.E_LOGICO) || peek(TypeEnum.OU_LOGICO)) {
            Token opTok = nextToken();
            Expression right = parseExpC();
            left = new BinaryOperation(opTok.getType(), left, right);
        }
        return left;
    }

    private Expression parseExpC() throws SyntaxException {
        Expression left = parseExpA();
        while (peek(TypeEnum.MENOR) ||
                peek(TypeEnum.MAIOR) ||
                peek(TypeEnum.IGUALDADE) ||
                peek(TypeEnum.MAIOR_IGUAL) ||
                peek(TypeEnum.MENOR_IGUAL) ||
                peek(TypeEnum.DIFERENTE)) {
            Token opTok = nextToken();
            Expression right = parseExpA();
            left = new BinaryOperation(opTok.getType(), left, right);
        }
        return left;
    }

    private Expression parseExpA() throws SyntaxException {
        Expression left = parseExpM();
        while (peek(TypeEnum.SOMA) || peek(TypeEnum.SUBTRACAO)) {
            Token opTok = nextToken();
            Expression right = parseExpM();
            left = new BinaryOperation(opTok.getType(), left, right);
        }
        return left;
    }

    private Expression parseExpM() throws SyntaxException {
        Expression left = parseExpP();
        while (peek(TypeEnum.MULTIPLICACAO) ||
               peek(TypeEnum.DIVISAO) ||
               peek(TypeEnum.DIVISAO_INTEIRA) ||
               peek(TypeEnum.MODULO)) {
            Token opTok = nextToken();
            Expression right = parseExpP();
            left = new BinaryOperation(opTok.getType(), left, right);
        }
        return left;
    }

    private Expression parseExpP() throws SyntaxException {
        Expression left = parsePrim();
        while (peek(TypeEnum.EXPONENCIACAO)) {
            Token opTok = nextToken();
            Expression right = parsePrim();
            left = new BinaryOperation(opTok.getType(), left, right);
        }
        return left;
    }

    private Expression parsePrim() throws SyntaxException {
        Token token = lookNextToken();
        if (token == null) {
            throw new SyntaxException(new Position(-1, -1, -1), null, null);
        }

        switch (token.getType()) {
            case NEGACAO -> {
                nextToken();
                Expression inner = parsePrim();
                return new UnaryOperation(TypeEnum.NEGACAO, inner);
            }
            case DIGITO -> {
                nextToken();
                return new Const(Integer.parseInt(token.getLexeme()));
            }
            case IDENTIFICADOR -> {
                Token id = nextToken();
                if (peek(TypeEnum.ABRE_PARENTESES)) {
                    nextToken();
                    List<Expression> args = new ArrayList<>();
                    if (!peek(TypeEnum.FECHA_PARENTESES)) {
                        args.add(parseExpL());
                        while (peek(TypeEnum.VIRGULA)) {
                            nextToken();
                            args.add(parseExpL());
                        }
                    }
                    expect(TypeEnum.FECHA_PARENTESES);
                    return new FunCall(id.getLexeme(), args);
                } else {
                    return new VariableReference(id.getLexeme());
                }
            }
            case ABRE_PARENTESES -> {
                nextToken();
                Expression inner = parseExpL();
                expect(TypeEnum.FECHA_PARENTESES);
                return inner;
            }
            default -> throw new SyntaxException(token.getPosition(), TypeEnum.DIGITO, token.getType());
        }
    }

    private Command parseCmd() throws SyntaxException {
        if (peek(TypeEnum.IF)) return parseIf();
        if (peek(TypeEnum.WHILE)) return parseWhile();
        if (peek(TypeEnum.RETURN)) {
            nextToken();
            Expression e = parseExpL();
            expect(TypeEnum.PONTO_VIRGULA);
            return new ReturnCommand(e);
        }
        if (peek(TypeEnum.VAR)) {
            VarDeclaration vd = parseVarDeclaration();
            return new VarDeclCommand(vd.getName(), vd.getExpression());
        }
        if (peek(TypeEnum.PRINT)) {
            nextToken();
            expect(TypeEnum.ABRE_PARENTESES);
            Expression expr = parseExpL();
            expect(TypeEnum.FECHA_PARENTESES);
            expect(TypeEnum.PONTO_VIRGULA);
            return new PrintCommand(expr);
        }

        Token id = expect(TypeEnum.IDENTIFICADOR);

        if (peek(TypeEnum.ATRIB_SOMA) || peek(TypeEnum.ATRIB_SUBTRACAO) ||
            peek(TypeEnum.ATRIB_MULTIPLICACAO) || peek(TypeEnum.ATRIB_DIVISAO)) {
            Token opToken = nextToken();
            Expression e = parseExpL();
            expect(TypeEnum.PONTO_VIRGULA);
            return new CompoundAssignCommand(id.getLexeme(), opToken.getType(), e);
        }

        expect(TypeEnum.IGUAL);
        Expression e = parseExpL();
        expect(TypeEnum.PONTO_VIRGULA);
        return new AssignCommand(id.getLexeme(), e);
    }

    private IfCommand parseIf() throws SyntaxException {
        expect(TypeEnum.IF);
        Expression cond = parseExpL();
        List<Command> thenPart = parseBracedCmds();
        expect(TypeEnum.ELSE);
        List<Command> elsePart = parseBracedCmds();
        return new IfCommand(cond, thenPart, elsePart);
    }

    private WhileCommand parseWhile() throws SyntaxException {
        expect(TypeEnum.WHILE);
        Expression cond = parseExpL();
        List<Command> body = parseBracedCmds();
        return new WhileCommand(cond, body);
    }

    private List<Command> parseBracedCmds() throws SyntaxException {
        expect(TypeEnum.LCHAVE);
        List<Command> xs = new ArrayList<>();
        while (!peek(TypeEnum.RCHAVE)) {
            xs.add(parseCmd());
        }
        expect(TypeEnum.RCHAVE);
        return xs;
    }
}