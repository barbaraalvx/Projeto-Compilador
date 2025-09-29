package codegen;

import syntacticanalysis.*;
import syntacticanalysis.commands.*;
import syntacticanalysis.expression.*;
import syntacticanalysis.VariableReference;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CodeGenerator {

    private int labelCounter = 0;
    private String newLabel(String base) { return base + (labelCounter++); }

    private FunDeclaration currentFunction = null;
    private String mainEndLabel = null;

    private static final String[] ARG_REGS = {"%rdi","%rsi","%rdx","%rcx","%r8","%r9"};

    // ===================== Programa =====================
    public String generate(Program program) {
        StringBuilder sb = new StringBuilder();

        // globais + vars do main
        Set<String> vars = new LinkedHashSet<>();
        for (Declaration d : program.getDeclarations()) {
            if (d instanceof VarDeclaration v) vars.add(v.getName());
        }
        for (Command c : program.getCommands()) {
            if (c instanceof VarDeclCommand v) vars.add(v.name());
        }

        // seção bss
        sb.append(".section .bss\n");
        for (String v : vars) sb.append(".lcomm ").append(v).append(", 8\n");

        // entry
        sb.append(".section .text\n");
        sb.append(".globl _start\n");
        sb.append("_start:\n");

        // inicialização de globais
        for (Declaration d : program.getDeclarations()) {
            if (d instanceof VarDeclaration v) {
                genExpr(v.getExpression(), sb);
                sb.append("    mov %rax, ").append(v.getName()).append("\n");
            }
        }

        // corpo do main
        mainEndLabel = newLabel("Lend_main");
        for (Command c : program.getCommands()) {
            genCmd(c, sb);
        }

        // finalização do main
        sb.append(mainEndLabel).append(":\n");
        sb.append("    mov %rax, %rdi\n");
        sb.append("    call imprime_num\n");
        sb.append("    call sair\n");

        // funções
        for (Declaration d : program.getDeclarations()) {
            if (d instanceof FunDeclaration f) genFun(f, sb);
        }

        sb.append(".include \"runtime.s\"\n");

        mainEndLabel = null;
        return sb.toString();
    }

    // ===================== Funções =====================
    private void genFun(FunDeclaration f, StringBuilder sb) {
        FunDeclaration prev = currentFunction;
        currentFunction = f;

        sb.append(f.getName()).append(":\n");
        sb.append("    push %rbp\n");
        sb.append("    mov %rsp, %rbp\n");

        for (VarDeclaration v : f.getLocals()) {
            genExpr(v.getExpression(), sb);
            sb.append("    mov %rax, ").append(v.getName()).append("\n");
        }

        for (Command c : f.getCommands()) {
            genCmd(c, sb);
        }

        currentFunction = prev;
    }

    // ===================== Comandos =====================
    private void genCmd(Command command, StringBuilder sb) {
        if (command instanceof AssignCommand a) {
            genExpr(a.expr(), sb);
            sb.append("    mov %rax, ").append(a.var()).append("\n");
            return;
        }

        if (command instanceof VarDeclCommand v) {
            genExpr(v.expr(), sb);
            sb.append("    mov %rax, ").append(v.name()).append("\n");
            return;
        }

        if (command instanceof IfCommand ic) {
            String Lfalse = newLabel("Lfalso");
            String Lend   = newLabel("Lfim");

            genExpr(ic.cond(), sb);
            sb.append("    cmp $0, %rax\n");
            sb.append("    jz ").append(Lfalse).append("\n");

            genBlock(ic.thenPart(), sb);
            sb.append("    jmp ").append(Lend).append("\n");

            sb.append(Lfalse).append(":\n");
            genBlock(ic.elsePart(), sb);

            sb.append(Lend).append(":\n");
            return;
        }

        if (command instanceof WhileCommand wc) {
            String Lstart = newLabel("Linicio");
            String Lend   = newLabel("Lfim");

            sb.append(Lstart).append(":\n");
            genExpr(wc.cond(), sb);
            sb.append("    cmp $0, %rax\n");
            sb.append("    jz ").append(Lend).append("\n");

            genBlock(wc.body(), sb);
            sb.append("    jmp ").append(Lstart).append("\n");
            sb.append(Lend).append(":\n");
            return;
        }

        if (command instanceof ReturnCommand r) {
            genExpr(r.expr(), sb);
            if (currentFunction != null) {
                sb.append("    pop %rbp\n");
                sb.append("    ret\n");
            } else {
                sb.append("    jmp ").append(mainEndLabel).append("\n");
            }
            return;
        }

        throw new IllegalStateException("Cmd não suportado: " + command.getClass().getSimpleName());
    }

    private void genBlock(List<Command> commands, StringBuilder sb) {
        if (commands == null) return;
        for (Command c : commands) genCmd(c, sb);
    }

    // ===================== Expressões =====================
    private void genExpr(Expression e, StringBuilder sb) {
        if (e instanceof Const c) {
            sb.append("    mov $").append(c.getValor()).append(", %rax\n");
            return;
        }

        if (e instanceof VariableReference v) {
            if (currentFunction != null) {
                List<String> params = currentFunction.getParameters();
                int idx = params.indexOf(v.getName());
                if (idx >= 0) {
                    if (idx >= ARG_REGS.length) {
                        throw new IllegalStateException("Mais de 6 parâmetros não suportado.");
                    }
                    sb.append("    mov ").append(ARG_REGS[idx]).append(", %rax\n");
                    return;
                }
            }
            sb.append("    mov ").append(v.getName()).append(", %rax\n");
            return;
        }

        if (e instanceof BinaryOperation b) {
            String op = b.getOperator().getValor();

            switch (op) {
                case "*" -> {
                    // otimização: multiplicação por constante
                    if (b.getLeft() instanceof Const cLeft) {
                        genExpr(b.getRight(), sb);
                        sb.append("    imul $").append(cLeft.getValor()).append(", %rax\n");
                    } else if (b.getRight() instanceof Const cRight) {
                        genExpr(b.getLeft(), sb);
                        sb.append("    imul $").append(cRight.getValor()).append(", %rax\n");
                    } else {
                        genExpr(b.getRight(), sb);
                        sb.append("    push %rax\n");
                        genExpr(b.getLeft(), sb);
                        sb.append("    pop %rbx\n");
                        sb.append("    imul %rbx, %rax\n");
                    }
                }
                case "+" -> {
                    if (b.getRight() instanceof Const cRight) {
                        genExpr(b.getLeft(), sb);
                        sb.append("    add $").append(cRight.getValor()).append(", %rax\n");
                    } else if (b.getLeft() instanceof Const cLeft) {
                        genExpr(b.getRight(), sb);
                        sb.append("    add $").append(cLeft.getValor()).append(", %rax\n");
                    } else {
                        genExpr(b.getRight(), sb);
                        sb.append("    push %rax\n");
                        genExpr(b.getLeft(), sb);
                        sb.append("    pop %rbx\n");
                        sb.append("    add %rbx, %rax\n");
                    }
                }
                case "-" -> {
                    if (b.getRight() instanceof Const cRight) {
                        genExpr(b.getLeft(), sb);
                        sb.append("    sub $").append(cRight.getValor()).append(", %rax\n");
                    } else {
                        genExpr(b.getRight(), sb);
                        sb.append("    push %rax\n");
                        genExpr(b.getLeft(), sb);
                        sb.append("    pop %rbx\n");
                        sb.append("    sub %rbx, %rax\n");
                    }
                }
                case "/" -> {
                    genExpr(b.getRight(), sb);
                    sb.append("    mov %rax, %rcx\n");
                    genExpr(b.getLeft(), sb);
                    sb.append("    cqo\n");
                    sb.append("    idiv %rcx\n");
                }
                case "==" -> {
                    genExpr(b.getRight(), sb);
                    sb.append("    push %rax\n");
                    genExpr(b.getLeft(), sb);
                    sb.append("    pop %rbx\n");
                    sb.append("    xor %rcx, %rcx\n");
                    sb.append("    cmp %rbx, %rax\n");
                    sb.append("    setz %cl\n");
                    sb.append("    mov %rcx, %rax\n");
                }
                case "<" -> {
                    genExpr(b.getRight(), sb);
                    sb.append("    push %rax\n");
                    genExpr(b.getLeft(), sb);
                    sb.append("    pop %rbx\n");
                    sb.append("    xor %rcx, %rcx\n");
                    sb.append("    cmp %rbx, %rax\n");
                    sb.append("    setl %cl\n");
                    sb.append("    mov %rcx, %rax\n");
                }
                case ">" -> {
                    genExpr(b.getRight(), sb);
                    sb.append("    push %rax\n");
                    genExpr(b.getLeft(), sb);
                    sb.append("    pop %rbx\n");
                    sb.append("    xor %rcx, %rcx\n");
                    sb.append("    cmp %rbx, %rax\n");
                    sb.append("    setg %cl\n");
                    sb.append("    mov %rcx, %rax\n");
                }
                default -> throw new IllegalStateException("Operador inválido: " + op);
            }
            return;
        }

        if (e instanceof FunCall call) {
            List<Expression> args = call.getArgs();
            int n = args.size();
            if (n > ARG_REGS.length) {
                throw new IllegalStateException("Mais de 6 argumentos não suportado.");
            }

            for (int i = 0; i < n; i++) {
                Expression arg = args.get(i);
                if (arg instanceof VariableReference v) {
                    sb.append("    mov ").append(v.getName()).append(", ").append(ARG_REGS[i]).append("\n");
                } else {
                    genExpr(arg, sb);
                    sb.append("    mov %rax, ").append(ARG_REGS[i]).append("\n");
                }
            }

            sb.append("    call ").append(call.getName()).append("\n");
            return;
        }

        throw new IllegalStateException("Expressão não suportada: " + e.getClass().getSimpleName());
    }
}
