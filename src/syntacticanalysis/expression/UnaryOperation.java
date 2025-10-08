package syntacticanalysis.expression;

import lexicalanalysis.TypeEnum;

public class UnaryOperation extends Expression {
    private final TypeEnum operator;
    private final Expression operand;

    public UnaryOperation(TypeEnum operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }

    public TypeEnum getOperator() {
        return operator;
    }

    public Expression getOperand() {
        return operand;
    }

    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "(" + operator + ")");
        operand.printTree(prefix + "  ");
    }
}