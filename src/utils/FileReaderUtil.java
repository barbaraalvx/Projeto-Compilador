package utils;

import java.io.*;

public class FileReaderUtil {
    public static String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                sb.append(linha.trim());
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
        return sb.toString();
    }
}
