    package utils;

    import java.io.*;

    public class FileReaderUtil {
        public static String readFile(String filePath) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String linha;
                boolean primeiraLinha = true; // Para controlar a adição de '\n'
                while ((linha = br.readLine()) != null) {
                    if (!primeiraLinha) {
                        sb.append('\n'); // Adiciona '\n' apenas antes da próxima linha, se não for a primeira
                    }
                    sb.append(linha);
                    primeiraLinha = false;
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            }
            return sb.toString();
        }
    }