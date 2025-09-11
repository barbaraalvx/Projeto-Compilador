.section .bss
.lcomm a, 8
.lcomm b, 8
.section .text
.globl _start
_start:
    mov $23, %rax
    push %rax
    mov $34, %rax
    push %rax
    mov $7, %rax
    mov %rax, %rcx
    pop %rax
    cqo
    idiv %rcx
    mov %rax, %rcx
    pop %rax
    add %rcx, %rax
    mov %rax, a
    mov $2, %rax
    push %rax
    mov $8, %rax
    mov %rax, %rcx
    pop %rax
    imul %rcx, %rax
    mov %rax, b
    mov a, %rax
    push %rax
    mov b, %rax
    mov %rax, %rcx
    pop %rax
    sub %rcx, %rax
    call imprime_num
    call sair
.include "runtime.s"
