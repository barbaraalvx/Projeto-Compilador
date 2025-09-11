.section .bss
.lcomm a, 8
.section .text
.globl _start
_start:
    mov $427, %rax
    push %rax
    mov $7, %rax
    mov %rax, %rcx
    pop %rax
    cqo
    idiv %rcx
    push %rax
    mov $11, %rax
    push %rax
    mov $231, %rax
    mov %rax, %rcx
    pop %rax
    imul %rcx, %rax
    mov %rax, %rcx
    pop %rax
    add %rcx, %rax
    push %rax
    mov $5, %rax
    mov %rax, %rcx
    pop %rax
    add %rcx, %rax
    mov %rax, a
    mov a, %rax
    call imprime_num
    call sair
.include "runtime.s"
