package syntacticanalysis.exceptions;

import syntacticanalysis.Position;
import syntacticanalysis.TypeEnum;

public class SyntaxException extends RuntimeException{
    public SyntaxException(Position pos, TypeEnum expected, TypeEnum found) {
        super("Erro de sintaxe na posição " + pos + ": esperado '" + expected.getValor() + "', encontrado '" + found.getValor() + "'");
    }
}
