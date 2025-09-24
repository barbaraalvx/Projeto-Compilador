package utils;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import static java.nio.file.StandardOpenOption.*;

public class FileWriterUtil {
    public static void writeToFile(String content, String outputPath) {
        try {
            Path p = Paths.get(outputPath);
            Files.createDirectories(p.getParent()); // garante as pastas
            Files.writeString(
                    p,
                    content,
                    StandardCharsets.UTF_8,
                    CREATE, TRUNCATE_EXISTING
            );
            System.out.println("Arquivo salvo em: " + p.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo: " + outputPath + " — " + e.getMessage());
        }
    }
}
