package codegen;

import java.util.LinkedHashSet;
import java.util.Set;
import syntacticanalysis.*;
import syntacticanalysis.expression.BinaryOperation;
import syntacticanalysis.expression.Const;
import syntacticanalysis.expression.Expression;

public class CodeGenerator {

    public String generate(Program program) {
        StringBuilder sb = new StringBuilder();

        Set<String> vars = new LinkedHashSet<>();
        for (Declaration d : program.getDeclarations()) vars.add(d.getVariableName());

        sb.append(".section .bss\n");
        for (String v : vars) sb.append(".lcomm ").append(v).append(", 8\n");

        sb.append(".section .text\n");
        sb.append(".globl _start\n");
        sb.append("_start:\n");

        for (Declaration d : program.getDeclarations()) {
            genExpr(d.getExpression(), sb);
            sb.append("    mov %rax, ").append(d.getVariableName()).append("\n");
        }

        genExpr(program.getFinalExpression(), sb);
        sb.append("    call imprime_num\n");
        sb.append("    call sair\n");
        sb.append(".include \"runtime.s\"\n");

        return sb.toString();
    }

    public String generate(Expression expr) {
        StringBuilder sb = new StringBuilder();
        sb.append(".section .text\n");
        sb.append(".globl _start\n");
        sb.append("_start:\n");
        genExpr(expr, sb);
        sb.append("    call imprime_num\n");
        sb.append("    call sair\n");
        sb.append(".include \"runtime.s\"\n");
        return sb.toString();
    }

    private void genExpr(Expression e, StringBuilder sb) {
        if (e instanceof Const c) {
            sb.append("    mov $").append(c.getValor()).append(", %rax\n");
            return;
        }
        if (e instanceof VariableReference v) {
            sb.append("    mov ").append(v.getName()).append(", %rax\n");
            return;
        }
        if (e instanceof BinaryOperation b) {
            genExpr(b.getLeft(), sb);
            sb.append("    push %rax\n");
            genExpr(b.getRight(), sb);
            sb.append("    mov %rax, %rcx\n");
            sb.append("    pop %rax\n");

            String op = b.getOperator().getValor();
            switch (op) {
                case "+" -> sb.append("    add %rcx, %rax\n");
                case "-" -> sb.append("    sub %rcx, %rax\n");
                case "*" -> sb.append("    imul %rcx, %rax\n");
                case "/" -> {
                    sb.append("    cqo\n");
                    sb.append("    idiv %rcx\n");
                }
                default -> throw new IllegalStateException("Operador inválido: " + op);
            }
            return;
        }
        throw new IllegalStateException("Expressão não suportada: " + e.getClass());
    }
}
