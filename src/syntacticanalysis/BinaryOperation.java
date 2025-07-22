package syntacticanalysis;

public class BinaryOperation extends Expression {
    private TypeOperator operator;
    private Expression leftExpression;
    private Expression rightExpression;

    public BinaryOperation(TypeOperator operator, Expression leftExpression, Expression rightExpression) {
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

    };

    @Override
    public String toString() {
        return "(" + leftExpression + " " + operator + " " + rightExpression + ")";
    }
}
