import java.util.ArrayList;
import java.util.List;
import lexicalanalysis.Position;
import lexicalanalysis.Token;
import lexicalanalysis.TypeEnum;

public class Tokenizer {

    public Tokenizer() {
    }

    public static List<Token> tokenize(String content) {
        int row = 0;
        int column = 0;
        int index = 0;
        boolean startComment = false;
        boolean endComment = false;
        boolean isComment = false;

        List<Token> tokens = new ArrayList<>();

        while (index < content.length()) {
            char c = content.charAt(index);

            //reseta as flags de comentario quando um comentario é finalizado
            if (startComment && endComment) {
                isComment = false;
                startComment = false;
                endComment = false;
            }

            // se o caracter atual for um # ele sinaliza o inicio ou fim de uma sessão de comentário
            if (c == '#') {
                isComment = true;
                if (!startComment) {
                    startComment = true;
                } else {
                    endComment = true;
                }
                column++;
                index++;
                continue;
            }

            // Se for uma quebra de linha, pula a linha, reseta a coluna pra 0 e aumenta o índice//
            // Considerei a quebra de linha como um caracter
            if (c == '\n') {
                row++;
                column = 0;
                index++;
                continue;
            }

            //Ignora caso seja espaço em branco
            if (Character.isWhitespace(c)) {
                column++;
                index++;
                continue;
            }

            // Instancia uma posição que vai ser inserida com o lexema
            Position currentPos = new Position(index, row, column);

            // ... dentro do while, depois de calcular 'c' e antes de isSingleCharToken(c)
            if (c == '=') {
                // olha próximo char para operadores compostos
                if ((index + 1) < content.length()) {
                    char nextChar = content.charAt(index + 1);
                    if (!isComment) {
                        Position pos = new Position(index, row, column);
                        switch (nextChar) {
                            case '+' -> {
                                tokens.add(new Token(TypeEnum.ATRIB_SOMA, "=+", pos));
                                index += 2;
                                column += 2;
                                continue;
                            }
                            case '-' -> {
                                tokens.add(new Token(TypeEnum.ATRIB_SUBTRACAO, "=-", pos));
                                index += 2;
                                column += 2;
                                continue;
                            }
                            case '*' -> {
                                tokens.add(new Token(TypeEnum.ATRIB_MULTIPLICACAO, "=*", pos));
                                index += 2;
                                column += 2;
                                continue;
                            }
                            case '/' -> {
                                tokens.add(new Token(TypeEnum.ATRIB_DIVISAO, "=/", pos));
                                index += 2;
                                column += 2;
                                continue;
                            }
                            case '=' -> {
                                tokens.add(new Token(TypeEnum.IGUALDADE, "==", pos));
                                index += 2;
                                column += 2;
                                continue;
                            }
                        }
                    }
                }
                // se não for operador composto, cai no fluxo de 1 char: '=' (atribuição/declaração)
            }

            // ... dentro do while, depois de calcular 'c' e antes de isSingleCharToken(c)
            if (c == '<') {
                // olha próximo char para operadores compostos
                if ((index + 1) < content.length()) {
                    char nextChar = content.charAt(index + 1);
                    if (!isComment) {
                        Position pos = new Position(index, row, column);
                        switch (nextChar) {
                            case '=' -> {
                                tokens.add(new Token(TypeEnum.MENOR_IGUAL, "<=", pos));
                                index += 2;
                                column += 2;
                                continue;
                            }
                        }
                    }
                }
                // se não for operador composto, cai no fluxo de 1 char: '<' (MENOR)
            }

            // ... dentro do while, depois de calcular 'c' e antes de isSingleCharToken(c)
            if (c == '>') {
                // olha próximo char para operadores compostos
                if ((index + 1) < content.length()) {
                    char nextChar = content.charAt(index + 1);
                    if (!isComment) {
                        Position pos = new Position(index, row, column);
                        switch (nextChar) {
                            case '=' -> {
                                tokens.add(new Token(TypeEnum.MAIOR_IGUAL, ">=", pos));
                                index += 2;
                                column += 2;
                                continue;
                            }
                        }
                    }
                }
                // se não for operador composto, cai no fluxo de 1 char: '<' (MAIOR)
            }

            if (c == '!') {
                // olha próximo char para operadores compostos
                if ((index + 1) < content.length()) {
                    char nextChar = content.charAt(index + 1);
                    if (!isComment) {
                        Position pos = new Position(index, row, column);
                        switch (nextChar) {
                            case '=' -> {
                                tokens.add(new Token(TypeEnum.DIFERENTE, "!=", pos));
                                index += 2;
                                column += 2;
                                continue;
                            }
                        }
                    }
                }
                // se não for operador composto, cai no fluxo de 1 char: '<' (MAIOR)
            }

            // Operadores aritméticos compostos
            if (c == '/') {
                if ((index + 1) < content.length()) {
                    char nextChar = content.charAt(index + 1);
                    if (!isComment && nextChar == '/') {
                        Position pos = new Position(index, row, column);
                        tokens.add(new Token(TypeEnum.DIVISAO_INTEIRA, "//", pos));
                        index += 2;
                        column += 2;
                        continue;
                    }
                }
            }

            if (c == '*') {
                if ((index + 1) < content.length()) {
                    char nextChar = content.charAt(index + 1);
                    if (!isComment && nextChar == '*') {
                        Position pos = new Position(index, row, column);
                        tokens.add(new Token(TypeEnum.EXPONENCIACAO, "**", pos));
                        index += 2;
                        column += 2;
                        continue;
                    }
                }
            }

            if (c == '%') {
                if (!isComment) {
                    Position pos = new Position(index, row, column);
                    tokens.add(new Token(TypeEnum.MODULO, "%", pos));
                }
                index++;
                column++;
                continue;
            }


            // Caso seja um caracter sozinho ( ) + - * /, usa a função addSingleChar e aumenta a posição
            if (isSingleCharToken(c)) {
                //verifica se não é um comentario e só então adiciona ao conjunto de tokens
                if (!isComment) {
                    tokens.add(addSingleCharToken(c, currentPos));
                }
                index++;
                column++;
            }

            // Lógica para identificadores
            else if (Character.isLetter(c)) {
                int startIndex = index;
                int startColumn = column;
                StringBuilder identifier = new StringBuilder();

                while (index < content.length() && Character.isLetterOrDigit(content.charAt(index))) {
                    identifier.append(content.charAt(index));
                    index++;
                    column++;
                }

                if (!isComment) {
                    Position identifierPos = new Position(startIndex, row, startColumn);
                    String lex = identifier.toString(); // case-sensitive (igual ao enunciado)

                    switch (lex) {
                        case "return" -> tokens.add(new Token(TypeEnum.RETURN, lex, identifierPos));
                        case "if" -> tokens.add(new Token(TypeEnum.IF, lex, identifierPos));
                        case "else" -> tokens.add(new Token(TypeEnum.ELSE, lex, identifierPos));
                        case "while" -> tokens.add(new Token(TypeEnum.WHILE, lex, identifierPos));
                        case "fun" -> tokens.add(new Token(TypeEnum.FUN, lex, identifierPos));
                        case "var" -> tokens.add(new Token(TypeEnum.VAR, lex, identifierPos));
                        case "main" -> tokens.add(new Token(TypeEnum.MAIN, lex, identifierPos));
                        default -> tokens.add(new Token(TypeEnum.IDENTIFICADOR, lex, identifierPos));
                    }
                }
            }

            // Lógica para números
            else if (Character.isDigit(c)) {
                // Enquanto for numero, percorre a string e aumenta os indices e colunas
                // Adicionando o valor no StringBuilder q vai ser adicionado no lexema
                int startIndex = index;
                int startColumn = column;
                StringBuilder number = new StringBuilder();

                while (index < content.length() && Character.isDigit(content.charAt(index))) {
                    number.append(content.charAt(index));
                    index++;
                    column++;
                }

                if (index < content.length() && Character.isLetter(content.charAt(index)) && !isComment) {
                    Position errPos = new Position(startIndex, row, startColumn);
                    System.err.println("Erro Léxico: número seguido de letras na posição " + errPos);
                    Main.isFailedLexicalAnalysis = true;

                    while (index < content.length() && Character.isLetterOrDigit(content.charAt(index))) {
                        index++;
                        column++;
                    }
                    continue;
                }

                Position numberPos = new Position(startIndex, row, startColumn);
                if (!isComment) {
                    tokens.add(new Token(TypeEnum.DIGITO, number.toString(), numberPos));
                }
            } else {
                // Esse else considera qualquer caracter que nao seja singleCharToken e nao seja numero, porém desconsidera comentário
                if (!isComment) {
                    System.err.println("Erro Léxico: Caractere inesperado '" + c + "' na posição " + currentPos);
                    Main.isFailedLexicalAnalysis = true;
                }
                index++;
                column++;
            }
        }
        tokens.add(new Token(TypeEnum.EOF, new Position(index, row, column)));

        return tokens;
    }

    private static boolean isSingleCharToken(char c) {
        return switch (c) {
            case '(', ')', '+', '-', '*', '/', ';', '=', '{', '}', '<', '>', ',', '%' -> true;
            default -> false;
        };
    }

    private static Token addSingleCharToken(char c, Position pos) {
        return switch (c) {
            case '(' -> new Token(TypeEnum.ABRE_PARENTESES, pos);
            case ')' -> new Token(TypeEnum.FECHA_PARENTESES, pos);
            case '+' -> new Token(TypeEnum.SOMA, pos);
            case '-' -> new Token(TypeEnum.SUBTRACAO, pos);
            case '*' -> new Token(TypeEnum.MULTIPLICACAO, pos);
            case ';' -> new Token(TypeEnum.PONTO_VIRGULA, pos);
            case '=' -> new Token(TypeEnum.IGUAL, pos);
            case '/' -> new Token(TypeEnum.DIVISAO, pos);
            case '{' -> new Token(TypeEnum.LCHAVE, pos);
            case '}' -> new Token(TypeEnum.RCHAVE, pos);
            case '<' -> new Token(TypeEnum.MENOR, pos);
            case '>' -> new Token(TypeEnum.MAIOR, pos);
            case ',' -> new Token(TypeEnum.VIRGULA, pos);
            case '%' -> new Token(TypeEnum.MODULO, pos);
            default -> throw new IllegalArgumentException("Token inesperado: " + c);
        };
    }
}
