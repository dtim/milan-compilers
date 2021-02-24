#include "ast.h"
#include <string.h>
#include <stdlib.h>

#define FORMAT_INDENT 4

char* op_names[] = {
        "+",
        "-",
        "*",
        "/",
        "NEG"
};

char* cond_names[] = {
       "=",
       "!=",
       "<",
       ">",
       "<=",
       ">="
};

void dump_ast_list(FILE* stream, int nspaces, ast_node* list);

void dump_ast_proc(FILE* stream, int nspaces, ast_node* ast)
{
        char* spaces;
        int use_spaces = 1;
        
        if(!stream)
                return;
        
        if(!ast) 
                return;

        spaces = (char*)malloc(nspaces + 1);
        if(spaces) {
                memset(spaces, ' ', nspaces);
                spaces[nspaces] = 0;
        }
        else {
                spaces = "";
                use_spaces = 0;
        }
        
        switch(ast->type) {
                
                case Node_Block:
                        fprintf(stream, "%sBLOCK: \n", spaces);
                        dump_ast_list(stream, nspaces + FORMAT_INDENT, ast->sub[0]);
                        break;

               case Node_Const:
                        fprintf(stream, "%sCONST %d\n", spaces, ast->data.integer_value);
                        break;

               case Node_Var:
                        fprintf(stream, "%sVAR %s\n", spaces, ast->data.string_value);
                        break;

               case Node_Read:
                        fprintf(stream, "%sREAD\n", spaces);
                        break;

               case Node_Expr:
                        fprintf(stream, "%sOP %s: \n", spaces, op_names[ast->data.integer_value]);
                        dump_ast_proc(stream, nspaces + FORMAT_INDENT, ast->sub[0]);
                        dump_ast_proc(stream, nspaces + FORMAT_INDENT, ast->sub[1]);
                        break;

               case Node_Assign:
                        fprintf(stream, "%sASSIGN %s:\n", spaces, ast->data.string_value);
                        dump_ast_proc(stream, nspaces + FORMAT_INDENT, ast->sub[0]);
                        break;

               case Node_Write:
                        fprintf(stream, "%sWRITE:\n", spaces);
                        dump_ast_proc(stream, nspaces + FORMAT_INDENT, ast->sub[0]);
                        break;

               case Node_Cond:
                        fprintf(stream, "%sCOND %s: \n", spaces, cond_names[ast->data.integer_value]);
                        dump_ast_proc(stream, nspaces + FORMAT_INDENT, ast->sub[0]);
                        dump_ast_proc(stream, nspaces + FORMAT_INDENT, ast->sub[1]);
                        break;

               case Node_If:
                        fprintf(stream, "%sIF:\n", spaces);
                        dump_ast_proc(stream, nspaces + FORMAT_INDENT, ast->data.ast_value);
                        dump_ast_proc(stream, nspaces + FORMAT_INDENT, ast->sub[0]);
                        dump_ast_proc(stream, nspaces + FORMAT_INDENT, ast->sub[1]);
                        break;

               case Node_While:
                        fprintf(stream, "%sWHILE:\n", spaces);
                        dump_ast_proc(stream, nspaces + FORMAT_INDENT, ast->data.ast_value);
                        dump_ast_proc(stream, nspaces + FORMAT_INDENT, ast->sub[0]);
                        break;

               default:
                        fprintf(stream, "%s<-- UNKNOWN NODE TYPE -->\n", spaces);
                        break;
        }

        if(spaces && use_spaces) {
                free(spaces);
        }
}

void dump_ast_list(FILE* stream, int nspaces, ast_node* list)
{
        ast_node* ptr = list;

        while(ptr) {
                dump_ast_proc(stream, nspaces, ptr);
                ptr = ptr->next;
        }
}

void dump_ast(FILE* stream, ast_node* ast)
{
        dump_ast_proc(stream, 0, ast);
}

ast_node* new_block(ast_node* arglist)
{
        ast_node* node;

        node = (ast_node*)malloc(sizeof(ast_node));
        if(node) {
                node->type = Node_Block;
                node->sub[0] = arglist;
                node->sub[1] = NULL;
                node->next = NULL;
        }

        return node;
}

ast_node* new_const(int v)
{
        ast_node* node;

        node = (ast_node*)malloc(sizeof(ast_node));
        if(node) {
                node->type = Node_Const;
                node->data.integer_value = v;
                node->sub[0] = node->sub[1] = NULL;
                node->next = NULL;
        }

        return node;
}

ast_node* new_var(char* v)
{
        ast_node* node;

        node = (ast_node*)malloc(sizeof(ast_node));
        if(node) {
                node->type = Node_Var;
                node->data.string_value = v;
                node->sub[0] = node->sub[1] = NULL;
                node->next = NULL;
        }

        return node;
}

ast_node* new_read()
{
        ast_node* node;

        node = (ast_node*)malloc(sizeof(ast_node));
        if(node) {
                node->type = Node_Read;
                node->sub[0] = node->sub[1] = NULL;
                node->next = NULL;
        }

        return node;
}

ast_node* new_expr(operation op, ast_node* arg1, ast_node* arg2)
{
        ast_node* node;

        node = (ast_node*)malloc(sizeof(ast_node));
        if(node) {
                node->type = Node_Expr;
                node->data.integer_value = op;
                node->sub[0] = arg1;
                node->sub[1] = arg2;
                node->next = NULL;
        }

        return node;
}
        
ast_node* new_assign(char* var, ast_node* arg)
{
        ast_node* node;

        node = (ast_node*)malloc(sizeof(ast_node));
        if(node) {
                node->type = Node_Assign;
                node->data.string_value = var;
                node->sub[0] = arg;
                node->sub[1] = NULL;
                node->next = NULL;
        }

        return node;
}

ast_node* new_cond(comparison comp, ast_node* arg1, ast_node* arg2)
{
        ast_node* node;

        node = (ast_node*)malloc(sizeof(ast_node));
        if(node) {
                node->type = Node_Cond;
                node->data.integer_value = comp;
                node->sub[0] = arg1;
                node->sub[1] = arg2;
                node->next = NULL;
        }

        return node;
}

ast_node* new_write(ast_node* expr)
{
        ast_node* node;

        node = (ast_node*)malloc(sizeof(ast_node));
        if(node) {
                node->type = Node_Write;
                node->sub[0] = expr;
                node->sub[1] = NULL;
                node->next = NULL;
        }

        return node;
}

ast_node* new_if(ast_node* cond, ast_node* arg1, ast_node* arg2)
{
        ast_node* node;

        node = (ast_node*)malloc(sizeof(ast_node));
        if(node) {
                node->type = Node_If;
                node->data.ast_value = cond;
                node->sub[0] = arg1;
                node->sub[1] = arg2;
                node->next = NULL;
        }

        return node;
}

ast_node* new_while(ast_node* cond, ast_node* arg)
{
        ast_node* node;

        node = (ast_node*)malloc(sizeof(ast_node));
        if(node) {
                node->type = Node_While;
                node->data.ast_value = cond;
                node->sub[0] = arg;
                node->sub[1] = NULL;
                node->next = NULL;
        }

        return node;
}

void free_ast(ast_node* ast)
{
        if(ast) {
                if(ast->type == Node_If || ast->type == Node_While) {
                        free_ast(ast->data.ast_value);
                }

                free_ast(ast->sub[0]);
                free_ast(ast->sub[1]);

                if(ast->next) {
                        free_ast(ast->next);
                }

                free(ast);
        }
}

#ifdef AST_MAIN

char* vars[] = {
        "i",
        "j"
};

int main()
{
        ast_node *ast, *tmp;
 
        tmp = new_assign(vars[0], new_const(2));
        ast = tmp;
        
        tmp->next = new_assign(vars[1], new_const(3));
        tmp = tmp->next;

        tmp->next = new_if(
                        new_cond(C_LT,
                                new_var(vars[0]),
                                new_var(vars[1])),

                        new_assign(vars[0],
                                new_read()),

                        new_write(new_expr(OP_ADD,
                                        new_var(vars[0]),
                                        new_const(5))));
      
        ast = new_block(ast);
        
        dump_ast(stdout, ast);
        free_ast(ast);

        return 0;
}

#endif

