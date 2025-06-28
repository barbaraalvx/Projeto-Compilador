import utils.AssemblyFileWriter;
import utils.FileReaderUtil;

public class Main {
    public static void main(String[] args) {

        // Passar o nome do arquivo como argumento da linha de comando
        if (args.length == 0) {
            System.out.println("Arquivo não especificado.");
            return;
        }

        String content = FileReaderUtil.readFile(args[0]);

        if (content.isBlank()) {
            System.out.println("O arquivo informado está vazio");
            return;
        }

        // Usa uma função static da classe DigitValidator para verificar se contém apenas digitos
        if (!DigitValidator.containsOnlyDigits(content)) {
            System.out.println("O arquivo deve conter apenas caracteres numéricos");
            return;
        }

        //Gera o arquivo de saída passando o conteudo do arquivo
        AssemblyFileWriter.generate("p1.s", content);

        System.out.println("Compilação concluída. Arquivo gerado: p1.s");
    }

    public class DigitValidator {
        public static boolean containsOnlyDigits(String input) {
            return input.chars().allMatch(Character::isDigit);
        }
    }
}

