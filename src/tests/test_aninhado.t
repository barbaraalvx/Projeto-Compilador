$ java -cp out Main src/inputs/aninhado.txt
----------------------------------------------------------------------
Análise Léxica
----------------------------------------------------------------------
Token{type=ABRE_PARENTESES, lexeme="(", position={index=0, row=0, column=0}}
Token{type=ABRE_PARENTESES, lexeme="(", position={index=1, row=0, column=1}}
Token{type=DIGITO, lexeme="1", position={index=2, row=0, column=2}}
Token{type=SOMA, lexeme="+", position={index=4, row=0, column=4}}
Token{type=DIGITO, lexeme="2", position={index=6, row=0, column=6}}
Token{type=FECHA_PARENTESES, lexeme=")", position={index=7, row=0, column=7}}
Token{type=MULTIPLICACAO, lexeme="*", position={index=9, row=0, column=9}}
Token{type=ABRE_PARENTESES, lexeme="(", position={index=11, row=0, column=11}}
Token{type=DIGITO, lexeme="3", position={index=12, row=0, column=12}}
Token{type=SUBTRACAO, lexeme="-", position={index=14, row=0, column=14}}
Token{type=DIGITO, lexeme="4", position={index=16, row=0, column=16}}
Token{type=FECHA_PARENTESES, lexeme=")", position={index=17, row=0, column=17}}
Token{type=FECHA_PARENTESES, lexeme=")", position={index=18, row=0, column=18}}
Token{type=EOF, lexeme="endOfFile", position={index=19, row=0, column=19}}
----------------------------------------------------------------------
Análise Sintática
----------------------------------------------------------------------
AST:
\- *
    |- +
    │   |- 1
    │   \- 2
    \- -
        |- 3
        \- 4
Expressão: ((1 + 2) * (3 - 4))
Resultado: -3