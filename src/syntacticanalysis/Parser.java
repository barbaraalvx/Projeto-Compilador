package syntacticanalysis;

import java.util.ArrayList;
import java.util.List;

import syntacticanalysis.commands.AssignCmd;
import syntacticanalysis.commands.Cmd;
import syntacticanalysis.commands.IfCmd;
import syntacticanalysis.commands.WhileCmd;
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
                if (token.getType() == t) { matches = true; break; }
            }
        }
        if (!matches) {
            throw new SyntaxException(
                    token != null ? token.getPosition() : new Position(-1,-1,-1),
                    // aqui você pode passar null e logar expectedMsg, ou escolher o 1º possível:
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

        // NOVO: zero ou mais comandos até encontrarmos 'return'
        List<Cmd> commands = parseCmdsUntilReturn();

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
        Expression expression = parseExpC();            // <- e aqui
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
            default -> {
                throw new SyntaxException(
                        token.getPosition(),
                        TypeEnum.DIGITO,
                        token.getType()
                );
            }
        }
    }

    private List<Cmd> parseCmdsUntilReturn() throws SyntaxException {
        List<Cmd> cmds = new ArrayList<>();
        while (!peek(TypeEnum.RETURN)) {
            cmds.add(parseCmd());
        }
        return cmds;
    }

    private Cmd parseCmd() throws SyntaxException {
        if (peek(TypeEnum.IF))    return parseIf();
        if (peek(TypeEnum.WHILE)) return parseWhile();

        // atribuição: IDENT '=' <expC> ';'
        Token id = expect(TypeEnum.IDENTIFICADOR);
        expect(TypeEnum.IGUAL);
        Expression e = parseExpC();
        expect(TypeEnum.PONTO_VIRGULA);
        return new AssignCmd(id.getLexeme(), e);
    }

    private IfCmd parseIf() throws SyntaxException {
        expect(TypeEnum.IF);
        Expression cond = parseExpC();
        List<Cmd> thenPart = parseBracedCmds();
        expect(TypeEnum.ELSE);
        List<Cmd> elsePart = parseBracedCmds();
        return new IfCmd(cond, thenPart, elsePart);
    }

    private WhileCmd parseWhile() throws SyntaxException {
        expect(TypeEnum.WHILE);
        Expression cond = parseExpC();
        List<Cmd> body = parseBracedCmds();
        return new WhileCmd(cond, body);
    }

    private List<Cmd> parseBracedCmds() throws SyntaxException {
        expect(TypeEnum.LCHAVE);
        List<Cmd> xs = new ArrayList<>();
        while (!peek(TypeEnum.RCHAVE)) {
            // importante: 'return' não é comando — se aparecer aqui é erro sintático da sua linguagem
            if (peek(TypeEnum.RETURN)) {
                throw new SyntaxException(lookNextToken().getPosition(), TypeEnum.IDENTIFICADOR, TypeEnum.RETURN);
            }
            xs.add(parseCmd());
        }
        expect(TypeEnum.RCHAVE);
        return xs;
    }

}
