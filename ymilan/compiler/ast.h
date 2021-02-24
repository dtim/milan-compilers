#ifndef _MILAN_AST_H
#define _MILAN_AST_H

#include <stdio.h>

typedef enum {
        C_EQ = 0,
        C_NE,
        C_LT,
        C_GT,
        C_LE,
        C_GE
} comparison;

typedef enum {
        OP_ADD = 0,
        OP_SUB,
        OP_MUL,
        OP_DIV,
        OP_NEG
} operation;

typedef enum {
        Node_Block = 0,
        Node_Const,
        Node_Var,
        Node_Read,
        Node_Expr,
        Node_Assign,
        Node_Write,
        Node_Cond,
        Node_If,
        Node_While
} node_type;

typedef struct ast_node_struct {
        node_type type;

        union {
                int integer_value;
                char* string_value;
                struct ast_node_struct* ast_value;
        } data;

        struct ast_node_struct* next;
        struct ast_node_struct* sub[2];
} ast_node;

void dump_ast(FILE* stream, ast_node* ast);

ast_node* new_block(ast_node* arglist);
ast_node* new_const(int v);
ast_node* new_var(char* v);
ast_node* new_read();
ast_node* new_expr(operation op, ast_node* arg1, ast_node* arg2);
ast_node* new_assign(char* var, ast_node* arg);
ast_node* new_cond(comparison comp, ast_node* arg1, ast_node* arg2);
ast_node* new_write(ast_node* expr);
ast_node* new_if(ast_node* cond, ast_node* arg1, ast_node* arg2);
ast_node* new_while(ast_node* cond, ast_node* arg);

void free_ast(ast_node* ast);

#endif

