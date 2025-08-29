.section .text
.globl _start
_start:
mov $5, %rax
    push %rax
mov $231, %rax
    push %rax
mov $11, %rax
    pop %rbx
    imul %rbx, %rax
    push %rax
mov $7, %rax
    push %rax
mov $427, %rax
    pop %rbx
    xor %rdx, %rdx
    div %rbx
    pop %rbx
    add %rbx, %rax
    pop %rbx
    add %rbx, %rax
call imprime_num
call sair
.include "runtime.s"
