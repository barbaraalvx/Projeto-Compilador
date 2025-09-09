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
    mov x, %rax
    push %rax
    mov y, %rax
    mov %rax, %rcx
    pop %rax
    add %rcx, %rax
    call imprime_num
    call sair
.include "runtime.s"
