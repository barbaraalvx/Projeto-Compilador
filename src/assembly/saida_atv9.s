.section .bss
.lcomm x, 8
.lcomm y, 8
.section .text
.globl _start
_start:
    mov $10, %rax
    mov %rax, x
    mov $20, %rax
    mov %rax, y
    mov y, %rax
    push %rax
    mov x, %rax
    pop %rbx
    xor %rcx, %rcx
    cmp %rbx, %rax
    setl %cl
    mov %rcx, %rax
    cmp $0, %rax
    jz Lfalso0
    mov y, %rax
    push %rax
    mov x, %rax
    pop %rbx
    add %rbx, %rax
    mov %rax, x
    jmp Lfim1
Lfalso0:
    mov y, %rax
    push %rax
    mov x, %rax
    pop %rbx
    sub %rbx, %rax
    mov %rax, x
Lfim1:
    mov x, %rax
    call imprime_num
    call sair
.include "runtime.s"
