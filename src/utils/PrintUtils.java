package utils;

import semanticalanalysis.SemanticExecutor;

public class PrintUtils {
    public static void printLexicalAnalysisHeader() {
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Análise Léxica");
        System.out.println("----------------------------------------------------------------------");
    }

    public static void printSyntacticAnalysisHeader() {
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Análise Sintática");
        System.out.println("----------------------------------------------------------------------");
    }

    public static void printSemanticAnalysisHeader(){
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Análise Semântica");
        System.out.println("----------------------------------------------------------------------");
    }

    public static void printCodeGenerationHeader(){
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Geração de Código Assembly");
        System.out.println("----------------------------------------------------------------------");
    }

    public static void printEnvironmentAndResult(SemanticExecutor.ExecResult res){
        System.out.println("Ambiente global: " + res.globals());
        System.out.println("Resultado final: " + res.value());
    }

}
