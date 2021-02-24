#ifndef _MILAN_CODE_H
#define _MILAN_CODE_H

#include "ast.h"
#include "ident.h"
#include <stdio.h>

typedef enum {
        STOP = 0,
        LOAD,
        STORE,
        BLOAD,
        BSTORE,
        PUSH,
        POP,
        DUP,
        INVERT,
        ADD,
        SUB,
        MULT,
        DIV,
        COMPARE,
        JUMP,
        JUMP_YES,
        JUMP_NO,
        INPUT,
        PRINT
} opcode;

void generate_program(FILE* stream, ast_node* ast);

#endif

