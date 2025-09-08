package syntacticanalysis;

public class BinaryOperation extends Expression {
    private final TypeEnum operator;
    private final Expression leftExpression;
    private final Expression rightExpression;

    public BinaryOperation(TypeEnum operator, Expression leftExpression, Expression rightExpression) {
        this.operator = operator;
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    @Override
    public int avaliar() {

        int leftValue = leftExpression.avaliar();
        int rightValue = rightExpression.avaliar();

        switch (operator) {
            case SOMA -> {
                return leftValue + rightValue;
            }
            case SUBTRACAO -> {
                return leftValue - rightValue;
            }
            case MULTIPLICACAO -> {
                return leftValue * rightValue;
            }
            case DIVISAO -> {
                return leftValue / rightValue;
            }
            default -> {
                return 0;
            }
        }

    }

    public TypeEnum getOperator() {
        return operator;
    }

    public Expression getLeft() {
        return leftExpression;
    }

    public Expression getRight() {
        return rightExpression;
    }


    @Override
    public String toString() {
        return "(" + leftExpression + " " + operator.getValor() + " " + rightExpression + ")";
    }

    public void printTree() {
        printTree("", true);
    }

    private void printTree(String prefix, boolean isLast) {
        System.out.println(prefix + (isLast ? "└── " : "├── ") + operator.getValor());

        String newPrefix = prefix + (isLast ? "    " : "│   ");

        if (leftExpression instanceof BinaryOperation leftBinOp) {
            leftBinOp.printTree(newPrefix, false);
        } else {
            System.out.println(newPrefix + "├── " + leftExpression);
        }

        if (rightExpression instanceof BinaryOperation rightBinOp) {
            rightBinOp.printTree(newPrefix, true);
        } else {
            System.out.println(newPrefix + "└── " + rightExpression);
        }
    }

    public void printTree(String prefix) {
        printTree(prefix, true);
    }
}
