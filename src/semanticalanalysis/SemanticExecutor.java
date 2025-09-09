package semanticalanalysis;

import java.util.LinkedHashMap;
import java.util.Map;

import syntacticanalysis.*;
import semanticalanalysis.exceptions.*;

public class SemanticExecutor {
    public static class ExecResult {
        public final Map<String, Integer> env;
        public final Expression substitutedFinal;
        public final int value;

        public ExecResult(Map<String, Integer> env, Expression substitutedFinal, int value) {
            this.env = env;
            this.substitutedFinal = substitutedFinal;
            this.value = value;
        }
    }

    public ExecResult execute(Program program) {
        Map<String, Integer> env = new LinkedHashMap<>();

        // 1) avalia declarações em ordem e preenche env
        for (Declaration d : program.getDeclarations()) {
            int v = eval(d.getExpression(), env);
            env.put(d.getVariableName(), v);
        }

        // 2) substitui variáveis na expressão final
        Expression original = program.getFinalExpression();
        Expression substituted = substitute(original, env);

        // 3) avalia a expressão final usando o env
        int value = eval(original, env);

        return new ExecResult(env, substituted, value);
    }

    // --- Avaliador "externo" (não usa Expression.avaliar()) ---
    private int eval(Expression e, Map<String, Integer> env) {
        if (e instanceof Const c) {
            return c.getValor();
        }
        if (e instanceof VariableReference v) {
            Integer val = env.get(v.getName());
            if (val == null) {
                throw new SemanticException(
                        "Variável não declarada/inicializada: " + v.getName());
            }
            return val;
        }
        if (e instanceof BinaryOperation b) {
            int a = eval(b.getLeft(), env);
            int c = eval(b.getRight(), env);
            return switch (b.getOperator()) {
                case SOMA -> a + c;
                case SUBTRACAO -> a - c;
                case MULTIPLICACAO -> a * c;
                case DIVISAO -> a / c; // divisão inteira
                default -> throw new IllegalStateException("Operador não suportado: " + b.getOperator());
            };
        }
        throw new IllegalStateException("Tipo de expressão desconhecido: " + e.getClass());
    }

    // --- Substituição de variáveis por Const (com folding opcional) ---
    private Expression substitute(Expression e, Map<String, Integer> env) {
        if (e instanceof Const c) return c;

        if (e instanceof VariableReference v) {
            Integer val = env.get(v.getName());
            if (val == null) {
                throw new semanticalanalysis.exceptions.SemanticException(
                    "Variável não declarada/inicializada: " + v.getName());
            }
            return new Const(val);
        }

        if (e instanceof BinaryOperation b) {
            Expression left  = substitute(b.getLeft(), env);
            Expression right = substitute(b.getRight(), env);
       
            return new BinaryOperation(b.getOperator(), left, right);
        }

        throw new IllegalStateException("Tipo de expressão desconhecido: " + e.getClass());
    }
}
