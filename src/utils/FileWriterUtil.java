package utils;

import java.io.FileWriter;
import java.io.IOException;

public class FileWriterUtil {

    public static void writeToFile(String content, String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(content);
            System.out.println("Arquivo salvo em: " + outputPath);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }
}
