package syntacticanalysis.commands;

import lexicalanalysis.TypeEnum;
import syntacticanalysis.expression.Expression;

public record CompoundAssignCommand(String var, TypeEnum operator, Expression expr) implements Command {
    public void print(String prefix) {
        System.out.println(prefix + "CompoundAssign " + var + " " + operator.getValor() + " =");
        expr.printTree(prefix + "  ");
    }

    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "CompoundAssign " + var + " " + operator.getValor() + " =");
        
        // Mostra a operação binária equivalente de forma mais legível
        String opSymbol = getOperatorSymbol(operator);
        System.out.println(prefix + "  (" + opSymbol + ")");
        System.out.println(prefix + "    ├── VarRef " + var);
        System.out.println(prefix + "    └──");
        expr.printTree(prefix + "        ");
    }
    
    private String getOperatorSymbol(TypeEnum op) {
        return switch (op) {
            case ATRIB_SOMA -> "+";
            case ATRIB_SUBTRACAO -> "-";
            case ATRIB_MULTIPLICACAO -> "*";
            case ATRIB_DIVISAO -> "/";
            default -> "?";
        };
    }
}
