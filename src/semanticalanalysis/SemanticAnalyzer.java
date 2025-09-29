package semanticalanalysis;

import syntacticanalysis.*;
import semanticalanalysis.exceptions.SemanticException;
import syntacticanalysis.commands.AssignCommand;
import syntacticanalysis.commands.Command;
import syntacticanalysis.commands.IfCommand;
import syntacticanalysis.commands.WhileCommand;
import syntacticanalysis.expression.BinaryOperation;
import syntacticanalysis.expression.Const;
import syntacticanalysis.expression.Expression;
import syntacticanalysis.expression.FunCall;

import java.util.*;

public class SemanticAnalyzer {
    private final SymbolTable globals = new SymbolTable();
    private final Map<String, Integer> functionArity = new HashMap<>();

    public void analyze(Program program) {
        // 1. Registrar funções
        for (Declaration d : program.getDeclarations()) {
            if (d instanceof FunDeclaration f) {
                if (functionArity.containsKey(f.getName())) {
                    throw new SemanticException("Função já declarada: " + f.getName());
                }
                functionArity.put(f.getName(), f.getParameters().size()); // ✅ ajustado
            }
        }

        // 2. Variáveis globais
        for (Declaration d : program.getDeclarations()) {
            if (d instanceof VarDeclaration v) {
                globals.declare(v.getName());
                checkExpression(v.getExpression(), null);
            }
        }

        // 3. Corpo principal (usa só globais)
        for (Command c : program.getCommands()) {
            checkCommand(c, null);
        }
        // ❌ REMOVIDO: não há mais program.getFinalExpression()

        // 4. Funções
        for (Declaration d : program.getDeclarations()) {
            if (d instanceof FunDeclaration f) {
                analyzeFunction(f);
            }
        }
    }

    private void analyzeFunction(FunDeclaration f) {
        Set<String> locals = new HashSet<>(f.getParameters()); // ✅ ajustado
        for (VarDeclaration v : f.getLocals()) {
            checkExpression(v.getExpression(), locals);
            locals.add(v.getName());
        }
        for (Command c : f.getCommands()) {
            checkCommand(c, locals);
        }
        // ❌ REMOVIDO: não usar mais f.getReturnExpression()
    }

    private void checkCommand(Command c, Set<String> locals) {
        if (c instanceof AssignCommand a) {
            String name = a.var();
            boolean isLocal = locals != null && locals.contains(name);
            if (!isLocal && !globals.isDeclared(name)) {
                throw new SemanticException("Variável não declarada: " + name);
            }
            checkExpression(a.expr(), locals);
        }
        else if (c instanceof IfCommand i) {
            checkExpression(i.cond(), locals);
            for (Command cc : i.thenPart()) checkCommand(cc, locals);
            for (Command cc : i.elsePart()) checkCommand(cc, locals);
        }
        else if (c instanceof WhileCommand w) {
            checkExpression(w.cond(), locals);
            for (Command cc : w.body()) checkCommand(cc, locals);
        }
    }

    private void checkExpression(Expression e, Set<String> locals) {
        if (e == null) return; // segurança extra

        if (e instanceof Const) return;

        if (e instanceof VariableReference v) {
            String name = v.getName();
            boolean isLocal = locals != null && locals.contains(name);
            if (!isLocal && !globals.isDeclared(name)) {
                throw new SemanticException("Variável não declarada: " + name);
            }
        }
        else if (e instanceof BinaryOperation b) {
            checkExpression(b.getLeft(), locals);
            checkExpression(b.getRight(), locals);
        }
        else if (e instanceof FunCall call) {
            Integer ar = functionArity.get(call.getName());
            if (ar == null) {
                throw new SemanticException("Função não declarada: " + call.getName());
            }
            if (ar != call.getArgs().size()) {
                throw new SemanticException(
                        "Aridade incorreta em " + call.getName() +
                                ": esperado " + ar + ", recebido " + call.getArgs().size()
                );
            }
            for (Expression arg : call.getArgs()) {
                checkExpression(arg, locals);
            }
        }
    }
}
