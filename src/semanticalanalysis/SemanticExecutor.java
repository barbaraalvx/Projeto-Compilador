package semanticalanalysis;

import java.util.*;
import syntacticanalysis.*;
import semanticalanalysis.exceptions.SemanticException;
import syntacticanalysis.commands.AssignCmd;
import syntacticanalysis.commands.Cmd;
import syntacticanalysis.commands.IfCmd;
import syntacticanalysis.commands.WhileCmd;
import syntacticanalysis.expression.BinaryOperation;
import syntacticanalysis.expression.Const;
import syntacticanalysis.expression.Expression;

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

        // 1) declarações -> inicializam o ambiente
        for (Declaration d : program.getDeclarations()) {
            int v = eval(d.getExpression(), env);
            env.put(d.getVariableName(), v);
        }

        // 2) comandos -> aplicam efeitos (assign/if/while)
        for (Cmd c : program.getCommands()) {
            exec(c, env);
        }

        // 3) retorno -> agora sim, com env atualizado
        Expression original = program.getFinalExpression();
        Expression substituted = substitute(original, env); // só para exibir árvore sem vars
        int value = eval(original, env);                    // avalia com o ambiente atual

        return new ExecResult(env, substituted, value);
    }

    // ===== execução de comandos =====
    private void exec(Cmd c, Map<String, Integer> env) {
        if (c instanceof AssignCmd a) {
            int v = eval(a.expr, env);
            env.put(a.var, v);
            return;
        }
        if (c instanceof IfCmd i) {
            int cond = eval(i.cond, env);
            List<Cmd> branch = (cond != 0) ? i.thenPart : i.elsePart;
            for (Cmd cc : branch) exec(cc, env);
            return;
        }
        if (c instanceof WhileCmd w) {
            while (eval(w.cond, env) != 0) {
                for (Cmd cc : w.body) exec(cc, env);
            }
            return;
        }
        throw new IllegalStateException("Tipo de comando desconhecido: " + c.getClass());
    }

    // ===== avaliador de expressões =====
    private int eval(Expression e, Map<String, Integer> env) {
        if (e instanceof Const c) {
            return c.getValor();
        }
        if (e instanceof VariableReference v) {
            Integer val = env.get(v.getName());
            if (val == null) {
                throw new SemanticException("Variável não declarada/inicializada: " + v.getName());
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
                case DIVISAO -> a / c;
                case MENOR -> (a < c) ? 1 : 0;
                case MAIOR -> (a > c) ? 1 : 0;
                case IGUALDADE -> (a == c) ? 1 : 0;
                default -> throw new IllegalStateException("Operador não suportado: " + b.getOperator());
            };
        }
        throw new IllegalStateException("Tipo de expressão desconhecido: " + e.getClass());
    }

    // ===== substituição (para exibir AST sem variáveis) =====
    private Expression substitute(Expression e, Map<String, Integer> env) {
        if (e instanceof Const c) return c;

        if (e instanceof VariableReference v) {
            Integer val = env.get(v.getName());
            if (val == null) {
                throw new SemanticException("Variável não declarada/inicializada: " + v.getName());
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
