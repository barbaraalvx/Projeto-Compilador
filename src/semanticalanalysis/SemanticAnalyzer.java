package semanticalanalysis;

import syntacticanalysis.*;
import semanticalanalysis.exceptions.SemanticException;
import syntacticanalysis.expression.BinaryOperation;
import syntacticanalysis.expression.Const;
import syntacticanalysis.expression.Expression;

public class SemanticAnalyzer {
    private final SymbolTable symbols = new SymbolTable();

    public void analyze(Program program) {
        for (Declaration decl : program.getDeclarations()) {
            checkExpression(decl.getExpression());
            symbols.declare(decl.getVariableName());
        }
        checkExpression(program.getFinalExpression());
    }

    private void checkExpression(Expression expression) {
        if (expression instanceof Const) {
            return;

        } else if (expression instanceof VariableReference variableReference) {

            if (!symbols.isDeclared(variableReference.getName())) {
                throw new SemanticException("Variável não declarada: " + variableReference.getName());
            }

        } else if (expression instanceof BinaryOperation bin) {
            checkExpression(bin.getLeft());
            checkExpression(bin.getRight());
        }
    }
}