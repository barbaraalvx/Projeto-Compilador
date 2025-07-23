import syntacticanalysis.*;
import syntacticanalysis.exceptions.SyntaxException;
import utils.FileReaderUtil;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static boolean isFailedLexicalAnalysis = false;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("File not specified.");
            return;
        }

        String content = FileReaderUtil.readFile(args[0]);

        if (content.isBlank()) {
            System.out.println("The specified file is empty.");
            return;
        }
        // A função Tokenize vai receber a string do arquivo e retornar uma lista
        // com todos os tokens e o EOF ao final
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Análise Léxica");
        System.out.println("----------------------------------------------------------------------");
        List<Token> tokens = tokenize(content);

        tokens.forEach(System.out::println);
        if (isFailedLexicalAnalysis) {
            System.err.println("Erro na análise léxica. Verifique os tokens acima.");
            System.out.println("----------------------------------------------------------------------");
            return;
        }

        try {
            System.out.println("----------------------------------------------------------------------");
            System.out.println("Análise Sintática");
            System.out.println("----------------------------------------------------------------------");
            Parser parser = new Parser(tokens);
            BinaryOperation expr = (BinaryOperation) parser.parseExpression();
            System.out.println("AST:");
            expr.printTree();
            System.out.println("Expressão: " + expr);
            System.out.println("Resultado: " + expr.avaliar());
        } catch (SyntaxException e) {
            System.err.println("Erro na análise sintática:");
            System.err.println(e.getMessage());
        }


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

            // Caso seja um caracter sozinho ( ) + - * /, usa a função addSingleChar e aumenta a posição
            if (isSingleCharToken(c)) {
                //verifica se não é um comentario e só então adiciona ao conjunto de tokens
                if (!isComment) {
                    tokens.add(addSingleCharToken(c, currentPos));
                }
                index++;
                column++;
            } else if (Character.isDigit(c)) {
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

                Position numberPos = new Position(startIndex, row, startColumn);
                //verifica se o número não é um comentario e só então adiciona ao conjunto de tokens
                if (!isComment) {
                    tokens.add(new Token(TypeEnum.DIGITO, number.toString(), numberPos));
                }
            } else {
                // Esse else considera qualquer caracter que nao seja singleCharToken e nao seja numero, porém desconsidera comentário
                if (!isComment) {
                    System.err.println("Erro Léxico: Caractere inesperado '" + c + "' na posição " + currentPos);
                    isFailedLexicalAnalysis = true;
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
            case '(', ')', '+', '-', '*', '/' -> true;
            default -> false;
        };
    }

    // Quando o lexema for apenas um operador, adiciona o token na lista passando só a posição
    // E o tipo do enum, tem um construtor da classe Token para esse caso
    private static Token addSingleCharToken(char c, Position pos) {
        return switch (c) {
            case '(' -> new Token(TypeEnum.ABRE_PARENTESES, pos);
            case ')' -> new Token(TypeEnum.FECHA_PARENTESES, pos);
            case '+' -> new Token(TypeEnum.SOMA, pos);
            case '-' -> new Token(TypeEnum.SUBTRACAO, pos);
            case '*' -> new Token(TypeEnum.MULTIPLICACAO, pos);
            case '/' -> new Token(TypeEnum.DIVISAO, pos);
            default -> throw new IllegalArgumentException("Token inesperado: " + c);
        };
    }
}