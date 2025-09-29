package syntacticanalysis.expression;

import lexicalanalysis.TypeEnum;

public class BinaryOperation extends Expression {
    private final TypeEnum operator;
    private final Expression left;
    private final Expression right;

    public BinaryOperation(TypeEnum operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public TypeEnum getOperator() {
        return operator;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public void printTree(String prefix) {
        System.out.println(prefix + "(" + operator.getValor() + ")");
        left.printTree(prefix + "  ├── ");
        right.printTree(prefix + "  └── ");
    }
}
