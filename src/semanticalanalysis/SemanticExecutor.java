package semanticalanalysis;

import java.util.*;
import syntacticanalysis.*;
import semanticalanalysis.exceptions.SemanticException;
import syntacticanalysis.commands.*;
import syntacticanalysis.expression.*;

/**
 * Executor semântico para a linguagem Fun.
 * Suporta variáveis globais, funções com parâmetros, variáveis locais e recursão.
 */
public class SemanticExecutor {
    private final Map<String,Integer> globals = new LinkedHashMap<>();
    private final Map<String,FunDeclaration> functions = new HashMap<>();

    public record ExecResult(Map<String,Integer> globals, int value) {}

    public ExecResult execute(Program program) {
        // 1) registrar funções
        for (Declaration d : program.getDeclarations()) {
            if (d instanceof FunDeclaration f) {
                functions.put(f.getName(), f);
            }
        }

        // 2) inicializar variáveis globais
        for (Declaration d : program.getDeclarations()) {
            if (d instanceof VarDeclaration v) {
                int val = eval(v.getExpression(), globals, null);
                globals.put(v.getName(), val);
            }
        }

        // 3) executar corpo do main
        int value = 0;
        try {
            for (Command c : program.getCommands()) {
                exec(c, globals, null);
            }
        } catch (ReturnValue r) {
            value = r.value();
        }

        return new ExecResult(globals, value);
    }

    // ===== execução de comandos =====
    private void exec(Command c, Map<String,Integer> globals, Map<String,Integer> locals) {
        if (c instanceof AssignCommand a) {
            int val = eval(a.expr(), globals, locals);
            String name = a.var();
            if (locals != null && locals.containsKey(name)) {
                locals.put(name, val);
            } else if (globals.containsKey(name)) {
                globals.put(name, val);
            } else {
                throw new SemanticException("Variável não declarada: " + name);
            }
            return;
        }

        if (c instanceof CompoundAssignCommand ca) {
            String name = ca.var();
            int currentVal;
            if (locals != null && locals.containsKey(name)) {
                currentVal = locals.get(name);
            } else if (globals.containsKey(name)) {
                currentVal = globals.get(name);
            } else {
                throw new SemanticException("Variável não declarada: " + name);
            }
            
            int exprVal = eval(ca.expr(), globals, locals);
            int newVal = switch (ca.operator()) {
                case ATRIB_SOMA -> currentVal + exprVal;
                case ATRIB_SUBTRACAO -> currentVal - exprVal;
                case ATRIB_MULTIPLICACAO -> currentVal * exprVal;
                case ATRIB_DIVISAO -> currentVal / exprVal;
                default -> throw new SemanticException("Operador composto não suportado: " + ca.operator());
            };
            
            if (locals != null && locals.containsKey(name)) {
                locals.put(name, newVal);
            } else {
                globals.put(name, newVal);
            }
            return;
        }

        if (c instanceof VarDeclCommand v) {
            int val = eval(v.expr(), globals, locals);
            if (locals != null) {
                locals.put(v.name(), val);
            } else {
                globals.put(v.name(), val);
            }
            return;
        }

        if (c instanceof IfCommand i) {
            int cond = eval(i.cond(), globals, locals);
            List<Command> branch = (cond != 0) ? i.thenPart() : i.elsePart();
            for (Command cc : branch) exec(cc, globals, locals);
            return;
        }

        if (c instanceof WhileCommand w) {
            while (eval(w.cond(), globals, locals) != 0) {
                for (Command cc : w.body()) exec(cc, globals, locals);
            }
            return;
        }

        if (c instanceof ReturnCommand r) {
            // interrompe execução e devolve valor
            throw new ReturnValue(eval(r.expr(), globals, locals));
        }

        throw new IllegalStateException("Comando desconhecido: " + c.getClass());
    }

    // ===== avaliação de expressões =====
    private int eval(Expression e, Map<String,Integer> globals, Map<String,Integer> locals) {
        if (e instanceof Const c) return c.getValor();

        if (e instanceof VariableReference v) {
            String name = v.getName();
            if (locals != null && locals.containsKey(name)) {
                return locals.get(name);
            } else if (globals.containsKey(name)) {
                return globals.get(name);
            }
            throw new SemanticException("Variável não declarada/inicializada: " + name);
        }

        if (e instanceof BinaryOperation b) {
            int a = eval(b.getLeft(), globals, locals);
            int c = eval(b.getRight(), globals, locals);
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

        if (e instanceof FunCall call) {
            FunDeclaration fun = functions.get(call.getName());
            if (fun == null) throw new SemanticException("Função não declarada: " + call.getName());

            List<Integer> argVals = new ArrayList<>();
            for (Expression arg : call.getArgs()) {
                argVals.add(eval(arg, globals, locals));
            }

            Map<String,Integer> newLocals = new HashMap<>();
            List<String> params = fun.getParameters(); // ✅ ajustado
            for (int i = 0; i < params.size(); i++) {
                newLocals.put(params.get(i), argVals.get(i));
            }

            for (VarDeclaration v : fun.getLocals()) {
                int val = eval(v.getExpression(), globals, newLocals);
                newLocals.put(v.getName(), val);
            }

            int ret = 0;
            try {
                for (Command c2 : fun.getCommands()) {
                    exec(c2, globals, newLocals);
                }
            } catch (ReturnValue r) {
                ret = r.value();
            }

            return ret;
        }

        throw new IllegalStateException("Expressão desconhecida: " + e.getClass());
    }

    // ===== exceção especial para retorno =====
    private static class ReturnValue extends RuntimeException {
        private final int value;
        public ReturnValue(int value) { this.value = value; }
        public int value() { return value; }
    }
}
