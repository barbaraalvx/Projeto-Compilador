import codegen.CodeGenerator;
import java.util.ArrayList;
import java.util.List;
import semanticalanalysis.*;
import semanticalanalysis.exceptions.SemanticException;
import syntacticanalysis.*;
import syntacticanalysis.exceptions.SyntaxException;
import utils.FileReaderUtil;
import utils.FileWriterUtil;


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
            System.exit(1);
            return;
        }

        try {
            System.out.println("----------------------------------------------------------------------");
            System.out.println("Análise Sintática");
            System.out.println("----------------------------------------------------------------------");
            Parser parser = new Parser(tokens);
            Program program = parser.parse();
            Expression expr = program.getFinalExpression();
            System.out.println("AST (original):");
            if (expr instanceof BinaryOperation binOp) {
                binOp.printTree();
            } else {
                System.out.println("AST (final): " + expr);
            }
            System.out.println("Expressão (original): " + expr);

            System.out.println("----------------------------------------------------------------------");
            System.out.println("Análise Semântica");
            System.out.println("----------------------------------------------------------------------");
            try {
                semanticalanalysis.SemanticAnalyzer analyzer = new semanticalanalysis.SemanticAnalyzer();
                analyzer.analyze(program);
                System.out.println("Análise semântica concluída sem erros.");
            } catch (SemanticException se) {
                System.err.println("Erro semântico: " + se.getMessage());
                System.exit(1);
            }

            SemanticExecutor executor = new semanticalanalysis.SemanticExecutor();
            SemanticExecutor.ExecResult res = executor.execute(program);

            System.out.println("AST (substituída):");
            if (res.substitutedFinal instanceof BinaryOperation bin2) {
                bin2.printTree();
            } else {
                System.out.println("AST (final): " + res.substitutedFinal);
            }

            System.out.println("Expressão (sem variáveis): " + res.substitutedFinal);
            System.out.println("Ambiente (variáveis): " + res.env);
            System.out.println("Resultado: " + res.value);
            CodeGenerator generator = new CodeGenerator();
            String assemblyCode = generator.generate(expr);
            String inputName = args[0].substring(args[0].lastIndexOf('/') + 1);
            String baseName = inputName.replace(".txt", "");
            String outputFile = "src/assembly/saida_" + baseName + ".s";

            FileWriterUtil.writeToFile(assemblyCode, outputFile);
        } catch (SyntaxException e) {
            System.err.println("Erro na análise sintática:");
            System.err.println(e.getMessage());
            System.exit(1);
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
                    tokens.add(new Token(TypeEnum.IDENTIFICADOR, identifier.toString(), identifierPos));
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
                    isFailedLexicalAnalysis = true;
                
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
            case '(', ')', '+', '-', '*', '/', ';', '=' -> true;
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
            case ';' -> new Token(TypeEnum.PONTO_VIRGULA, pos);
            case '=' -> new Token(TypeEnum.IGUAL, pos);
            case '/' -> new Token(TypeEnum.DIVISAO, pos);
            default -> throw new IllegalArgumentException("Token inesperado: " + c);
        };
    }
}