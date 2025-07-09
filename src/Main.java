import syntacticanalysis.Position;
import syntacticanalysis.Token;
import syntacticanalysis.TypeEnum;
import utils.FileReaderUtil;

import java.util.ArrayList;
import java.util.List;
public class Main {

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

        List<Token> tokens = tokenize(content);

        tokens.forEach(System.out::println);
    }

    public static List<Token> tokenize(String content) {
        int row = 0;
        int column = 0;
        int index = 0;

        List<Token> tokens = new ArrayList<>();

        while (index < content.length()) {
            char c = content.charAt(index);

            if (c == '\n') {
                row++;
                column = 0;
                index++;
                continue;
            }

            if (Character.isWhitespace(c)) {
                column++;
                index++;
                continue;
            }

            Position currentPos = new Position(index, row, column);

            if (isSingleCharToken(c)) {
                tokens.add(addSingleCharToken(c, currentPos));
                index++;
                column++;
            } else if (Character.isDigit(c)) {
                int startIndex = index;
                int startColumn = column;
                StringBuilder number = new StringBuilder();

                while (index < content.length() && Character.isDigit(content.charAt(index))) {
                    number.append(content.charAt(index));
                    index++;
                    column++;
                }

                Position numberPos = new Position(startIndex, row, startColumn);
                tokens.add(new Token(TypeEnum.DIGITO, number.toString(), numberPos));
            } else {
                System.err.println("Erro Léxico: Caractere inesperado '" + c + "' na posição " + currentPos);
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

    private static Token addSingleCharToken(char c, Position pos) {
        return switch (c) {
            case '(' -> new Token(TypeEnum.ABRE_PARENTESES, Character.toString(c), pos);
            case ')' -> new Token(TypeEnum.FECHA_PARENTESES, Character.toString(c), pos);
            case '+' -> new Token(TypeEnum.SOMA, Character.toString(c), pos);
            case '-' -> new Token(TypeEnum.SUBTRACAO, Character.toString(c), pos);
            case '*' -> new Token(TypeEnum.MULTIPLICACAO, Character.toString(c), pos);
            case '/' -> new Token(TypeEnum.DIVISAO, Character.toString(c), pos);
            default -> throw new IllegalArgumentException("Token inesperado: " + c);
        };
    }
}