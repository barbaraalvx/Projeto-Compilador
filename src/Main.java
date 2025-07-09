import syntacticanalysis.Position;
import syntacticanalysis.Token;
import syntacticanalysis.TypeEnum;
import utils.FileReaderUtil;

import java.util.ArrayList;
import java.util.List;
public class Main {

    private static int linhaAtual = 0;
    private static int colunaAtual = 0;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("File not specified.");
            return;
        }

        String content = FileReaderUtil.readFile(args[0]);

        if (content.isBlank()) {
            System.out.println("he specified file is empty.");
            return;
        }

        List<Token> tokens = tokenize(content);
        tokens.add(new Token(TypeEnum.EOF, new Position(content.length(), linhaAtual, colunaAtual)));

        tokens.forEach(System.out::println);
    }




    public static List<Token> tokenize(String content) {
        List<Token> tokens = new ArrayList<>();

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '\n') {
                linhaAtual++;
                colunaAtual = 0;
                continue;
            }

            if (c == ' ') {
                colunaAtual++;
                continue;
            }

            Position pos = new Position(i, linhaAtual, colunaAtual);

            if (isSingleCharToken(c)) {
                tokens.add(addSingleCharToken(c, pos));
            } else if (Character.isDigit(c)) {
                int startIndex = i;
                int startColuna = colunaAtual;
                StringBuilder number = new StringBuilder();

                while (i < content.length() && Character.isDigit(content.charAt(i))) {
                    number.append(content.charAt(i));
                    i++;
                    colunaAtual++;
                }
                i--;

                Position numberPos = new Position(startIndex, linhaAtual, startColuna);
                tokens.add(new Token(TypeEnum.DIGITO, number.toString(), numberPos));
                continue;
            }

            colunaAtual++;
        }

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
