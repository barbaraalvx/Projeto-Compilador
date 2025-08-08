package codegen;

import syntacticanalysis.BinaryOperation;
import syntacticanalysis.Const;
import syntacticanalysis.Expression;

public class CodeGenerator {

    private final StringBuilder builder = new StringBuilder();

    public String generate(Expression expr) {
        builder.setLength(0);
        builder.append(".section .text\n");
        builder.append(".globl _start\n");
        builder.append("_start:\n");

        generateExpression(expr);

        builder.append("call imprime_num\n");
        builder.append("call sair\n");
        builder.append(".include \"runtime.s\"\n");

        return builder.toString();
    }

    private void generateExpression(Expression expr) {
        if (expr instanceof Const c) {
            builder.append("mov $")
                    .append( c.getValor())
                    .append(", %rax\n");
        } else if (expr instanceof BinaryOperation bin) {
            String op = bin.getOperator().getValor();

            // Gera lado direito primeiro e empilha
            generateExpression(bin.getRight());
            builder.append("    push %rax\n");

            // Gera lado esquerdo
            generateExpression(bin.getLeft());
            builder.append("    pop %rbx\n");

            switch (op) {
                case "+":
                    builder.append("    add %rbx, %rax\n");
                    break;
                case "-":
                    builder.append("    sub %rbx, %rax\n"); // rax - rbx
                    break;
                case "*":
                    builder.append("    imul %rbx, %rax\n");
                    break;
                case "/":
                    builder.append("    xor %rdx, %rdx\n"); // limpa rdx
                    builder.append("    mov %rax, %rcx\n"); // salva numerador
                    builder.append("    mov %rbx, %rax\n"); // denominador
                    builder.append("    mov %rcx, %rbx\n"); // numerador em rbx
                    builder.append("    div %rbx\n");       // rax := rcx / rbx
                    break;
                default:
                    throw new RuntimeException("Operador inválido: " + op);
            }
        }
    }
}
