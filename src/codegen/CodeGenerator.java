package codegen;

import syntacticanalysis.Declaration;
import syntacticanalysis.Program;
import syntacticanalysis.commands.AssignCmd;
import syntacticanalysis.commands.Cmd;
import syntacticanalysis.commands.IfCmd;
import syntacticanalysis.commands.WhileCmd;
import syntacticanalysis.expression.BinaryOperation;
import syntacticanalysis.expression.Const;
import syntacticanalysis.expression.Expression;
import syntacticanalysis.VariableReference;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CodeGenerator {

    private int labelCounter = 0;
    private String newLabel(String base) { return base + (labelCounter++); }

    public String generate(Program program) {
        StringBuilder sb = new StringBuilder();

        // 1) Coletar variáveis declaradas
        Set<String> vars = new LinkedHashSet<>();
        for (Declaration d : program.getDeclarations()) {
            vars.add(d.getVariableName());
        }

        // .bss para variáveis
        sb.append(".section .bss\n");
        for (String v : vars) sb.append(".lcomm ").append(v).append(", 8\n");

        // 2) Cabeçalho de código
        sb.append(".section .text\n");
        sb.append(".globl _start\n");
        sb.append("_start:\n");

        // 3) Inicialização por declarações (var = <expr>;)
        for (Declaration d : program.getDeclarations()) {
            genExpr(d.getExpression(), sb);
            sb.append("    mov %rax, ").append(d.getVariableName()).append("\n");
        }

        // 4) Corpo de comandos vindo de program.getCommands()
        for (Cmd c : program.getCommands()) {
            genCmd(c, sb);
        }

        // 5) Expressão final (retorno), imprimir e sair
        genExpr(program.getFinalExpression(), sb);
        sb.append("    call imprime_num\n");
        sb.append("    call sair\n");

        // Rotinas de runtime (I/O, saída)
        sb.append(".include \"runtime.s\"\n");

        return sb.toString();
    }

    // Útil para testar gerando só uma Expression
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

    // ===================== Comandos =====================
    private void genCmd(Cmd cmd, StringBuilder sb) {
        // Troque os nomes das classes abaixo se no seu projeto forem diferentes:
        if (cmd instanceof AssignCmd a) {
            genExpr(a.expr, sb);
            sb.append("    mov %rax, ").append(a.var).append("\n");
            return;
        }

        if (cmd instanceof IfCmd ic) {
            String Lfalse = newLabel("Lfalso");
            String Lend   = newLabel("Lfim");

            // condição
            genExpr(ic.cond, sb);
            sb.append("    cmp $0, %rax\n");
            sb.append("    jz ").append(Lfalse).append("\n");

            // then
            genBlock(ic.thenPart, sb);
            sb.append("    jmp ").append(Lend).append("\n");

            // else
            sb.append(Lfalse).append(":\n");
            genBlock(ic.elsePart, sb);

            sb.append(Lend).append(":\n");
            return;
        }

        if (cmd instanceof WhileCmd wc) {
            String Lstart = newLabel("Linicio");
            String Lend   = newLabel("Lfim");

            sb.append(Lstart).append(":\n");
            genExpr(wc.cond, sb);
            sb.append("    cmp $0, %rax\n");
            sb.append("    jz ").append(Lend).append("\n");

            genBlock(wc.body, sb);
            sb.append("    jmp ").append(Lstart).append("\n");
            sb.append(Lend).append(":\n");
            return;
        }

        throw new IllegalStateException("Cmd não suportado: " + cmd.getClass().getSimpleName());
    }

    private void genBlock(List<Cmd> cmds, StringBuilder sb) {
        if (cmds == null) return;
        for (Cmd c : cmds) genCmd(c, sb);
    }

    // ===================== Expressões =====================
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
            String op = b.getOperator().getValor();

            // Avalia direita, empilha; avalia esquerda; combina
            genExpr(b.getRight(), sb);     // B
            sb.append("    push %rax\n");
            genExpr(b.getLeft(), sb);      // A
            sb.append("    pop %rbx\n");   // B -> %rbx

            switch (op) {
                case "+" -> sb.append("    add %rbx, %rax\n");
                case "-" -> sb.append("    sub %rbx, %rax\n");
                case "*" -> sb.append("    imul %rbx, %rax\n");
                case "/" -> {
                    sb.append("    mov %rbx, %rcx\n");
                    sb.append("    cqo\n");
                    sb.append("    idiv %rcx\n");
                }
                case "==" -> {
                    sb.append("    xor %rcx, %rcx\n");
                    sb.append("    cmp %rbx, %rax\n");
                    sb.append("    setz %cl\n");
                    sb.append("    mov %rcx, %rax\n");
                }
                case "<" -> {
                    sb.append("    xor %rcx, %rcx\n");
                    sb.append("    cmp %rbx, %rax\n");
                    sb.append("    setl %cl\n");
                    sb.append("    mov %rcx, %rax\n");
                }
                case ">" -> {
                    sb.append("    xor %rcx, %rcx\n");
                    sb.append("    cmp %rbx, %rax\n");
                    sb.append("    setg %cl\n");
                    sb.append("    mov %rcx, %rax\n");
                }
                default -> throw new IllegalStateException("Operador inválido: " + op);
            }
            return;
        }
        throw new IllegalStateException("Expressão não suportada: " + e.getClass().getSimpleName());
    }
}
