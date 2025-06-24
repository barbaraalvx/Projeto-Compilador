#
# modelo de saida para o compilador
#
.section .text
.globl _start
_start:
mov $1234532, %rax
call imprime_num
call sair
.include "runtime.s"
