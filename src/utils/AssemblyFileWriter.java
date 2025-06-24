package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class AssemblyFileWriter {
    public static void generate(String outputFileName, String digits) {

        String instruction = String.format("mov $%s, %%rax", digits);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            writer.write("#\n");
            writer.write("# modelo de saida para o compilador\n");
            writer.write("#\n");
            writer.write(".section .text\n");
            writer.write(".globl _start\n");
            writer.write("_start:\n");
            writer.write(instruction + "\n");
            writer.write("call imprime_num\n");
            writer.write("call sair\n");
            writer.write(".include \"runtime.s\"\n");
        } catch (IOException e) {
            System.err.println("Erro ao escrever o arquivo assembly: " + e.getMessage());
        }
    }
}
