.section .text
.globl _start
_start:
mov $8, %rax
    push %rax
mov $2, %rax
    pop %rbx
    imul %rbx, %rax
    push %rax
mov $7, %rax
    push %rax
mov $34, %rax
    pop %rbx
    xor %rdx, %rdx
    div %rbx
    push %rax
mov $23, %rax
    pop %rbx
    add %rbx, %rax
    pop %rbx
    sub %rbx, %rax
call imprime_num
call sair
.include "runtime.s"
