import codegen.CodeGenerator;
import lexicalanalysis.Token;
import semanticalanalysis.SemanticAnalyzer;
import semanticalanalysis.SemanticExecutor;
import semanticalanalysis.exceptions.SemanticException;
import syntacticanalysis.Parser;
import syntacticanalysis.Program;
import syntacticanalysis.exceptions.SyntaxException;
import syntacticanalysis.expression.BinaryOperation;
import syntacticanalysis.expression.Expression;
import utils.FileReaderUtil;
import utils.FileWriterUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static utils.PrintUtils.*;


public class Main {

    static boolean isFailedLexicalAnalysis = false;

    static Parser parser;
    static Program parsedProgram;
    static List<Token> tokenList;
    static Expression returnExpression;

    // Analisadores
    final static SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
    final static SemanticExecutor semanticExecutor = new SemanticExecutor();
    final static CodeGenerator codeGenerator = new CodeGenerator();

    //Arquivos de entrada e saída
    static String assemblyCode;
    static String outputFileName;
    static String inputFileContent;

    public static void main(String[] args) {

        validateInputFile(args);

        printLexicalAnalysisHeader();
        tokenList = Tokenizer.tokenize(inputFileContent);
        tokenList.forEach(System.out::println);

        if (isFailedLexicalAnalysis) {
            printLexicalAnalysisFailed();
            return;
        }

        try {

            printSyntacticAnalysisHeader();

            parser = new Parser(tokenList);
            parsedProgram = parser.parse();
            parsedProgram.printTree();
            returnExpression = parsedProgram.getFinalExpression();
            System.out.println("Expressão de retorno (original): " + returnExpression);

            if (returnExpression instanceof BinaryOperation binOp) {
                System.out.println("Árvore do retorno:");
                binOp.printTree();
            }

            try {

                printSemanticAnalysisHeader();
                semanticAnalyzer.analyze(parsedProgram);
                System.out.println("Análise semântica concluída sem erros.");

            } catch (SemanticException semanticException) {

                System.err.println("Erro semântico: " + semanticException.getMessage());
                System.exit(1);

            }

            SemanticExecutor.ExecResult executionResult = semanticExecutor.execute(parsedProgram);

            if (executionResult.substitutedFinal() instanceof BinaryOperation bin2) {
                bin2.printTree();
            } else {
                System.out.println("AST (final): " + executionResult.substitutedFinal());
            }

            printEnvironmentAndResult(executionResult);
            printCodeGenerationHeader();

            assemblyCode = codeGenerator.generate(parsedProgram);
            generateFile(args);
            FileWriterUtil.writeToFile(assemblyCode, outputFileName);

        } catch (SyntaxException e) {

            System.err.println("Erro na análise sintática:" + e.getMessage());
            System.exit(1);

        }
    }

    private static void validateInputFile(String[] args) {
        if (args.length == 0) {
            System.out.println("File not specified.");
            System.exit(1);
        }

        inputFileContent = FileReaderUtil.readFile(args[0]);

        if (inputFileContent.isBlank()) {
            System.out.println("The specified file is empty.");
            System.exit(1);
        }
    }

    private static void printLexicalAnalysisFailed() {
        System.err.println("Erro na análise léxica. Verifique os tokens acima.");
        System.out.println("----------------------------------------------------------------------");
        System.exit(1);
    }

    private static void generateFile(String[] args) {
        Path inputPath = Paths.get(args[0]);                 // caminho da entrada (absoluto ou relativo)
        String baseName = stripExtension(inputPath.getFileName().toString());
        Path outPath = Paths.get("src", "assembly", "saida_" + baseName + ".s");
        outputFileName = outPath.toString();                 // mantém a sua variável global, se quiser
    }

    private static String stripExtension(String name) {
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(0, i) : name;
    }


}

