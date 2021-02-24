#include "code.h"
#include "milan.h"
#include <stdlib.h>

typedef struct opcode_info {
        char *name;
        int need_arg;
} opcode_info;

opcode_info opcodes_table[] = {
        {"STOP",     0},
        {"LOAD",     1},
        {"STORE",    1},
        {"BLOAD",    1},
        {"BSTORE",   1},
        {"PUSH",     1},
        {"POP",      0},
        {"DUP",      0},
        {"INVERT",   0},
        {"ADD",      0},
        {"SUB",      0},
        {"MULT",     0},
        {"DIV",      0},
        {"COMPARE",  1},
        {"JUMP",     1},
        {"JUMP_YES", 1},
        {"JUMP_NO",  1},
        {"INPUT",    0},
        {"PRINT",    0}
};

int opcode_count = sizeof(opcodes_table) / sizeof(opcode_info);

unsigned int generate_command(FILE* stream, unsigned int address, opcode op, int arg)
{
        if(op < opcode_count) {
                if(opcodes_table[op].need_arg) {
                        fprintf(stream, "%d:\t%s\t\t%d\n", address,
                                        opcodes_table[op].name, arg);
                }
                else {
                        fprintf(stream, "%d:\t%s\n", address, opcodes_table[op].name);
                }
        }
        else {
                milan_error("Illegal opcode");
        }

        return address + 1;
}

int get_var_address(ast_node* ast)
{
        return find_name(ast->data.string_value);
}

unsigned int generate_code(FILE* stream, unsigned int address, ast_node* ast)
{
        ast_node* ptr;
        opcode op;
        unsigned int tmpaddr1, tmpaddr2;
        unsigned int new_address;
        
        if(ast) {
                switch(ast->type) {
                        case Node_Block:
                                ptr = ast->sub[0];
                                new_address = address;
                                while(ptr) {
                                        new_address = generate_code(stream, new_address, ptr);
                                        ptr = ptr->next;
                                }
                                break;

                        case Node_Const:
                                new_address = generate_command(stream, address, PUSH, ast->data.integer_value);
                                break;

                        case Node_Var:
                                new_address = generate_command(stream, address, LOAD, get_var_address(ast));
                                break;

                        case Node_Read:
                                new_address = generate_command(stream, address, INPUT, 0);
                                break;
                                
                        case Node_Expr:
                                new_address = generate_code(stream, address, ast->sub[0]);
                                
                                if(ast->data.integer_value != OP_NEG) {
                                        new_address = generate_code(stream, new_address, ast->sub[1]);
                                }
                                        
                                switch(ast->data.integer_value) {
                                        case OP_ADD: op = ADD; break;
                                        case OP_SUB: op = SUB; break;
                                        case OP_MUL: op = MULT; break;
                                        case OP_DIV: op = DIV; break;
                                        case OP_NEG: op = INVERT; break;
                                        default:
                                                     milan_error("Unknown arithmetical operator");
                                                     break;
                                }

                                new_address = generate_command(stream, new_address, op, 0);
                                break;
                                
                        case Node_Assign:
                                new_address = generate_code(stream, address, ast->sub[0]);
                                new_address = generate_command(stream, new_address, STORE, get_var_address(ast));
                                break;
                                
                        case Node_Write:
                                new_address = generate_code(stream, address, ast->sub[0]);
                                new_address = generate_command(stream, new_address, PRINT, 0);
                                break;
                                
                        case Node_Cond:
                                new_address = generate_code(stream, address, ast->sub[0]);
                                new_address = generate_code(stream, new_address, ast->sub[1]);
                                new_address = generate_command(stream, new_address, COMPARE, ast->data.integer_value);
                                break;
                                
                        case Node_If:
                                new_address = generate_code(stream, address, ast->data.ast_value);
                                tmpaddr1 = new_address++;
                                new_address = generate_code(stream, new_address, ast->sub[0]);

                                if(ast->sub[1]) {
                                        generate_command(stream, tmpaddr1, JUMP_NO, new_address + 1);
                                        tmpaddr1 = new_address++;
                                        new_address = generate_code(stream, new_address, ast->sub[1]);
                                        generate_command(stream, tmpaddr1, JUMP, new_address);
                                }
                                else {
                                        generate_command(stream, tmpaddr1, JUMP_NO, new_address);
                                }
                                break;
                                
                        case Node_While:
                                tmpaddr1 = address;
                                new_address = generate_code(stream, address, ast->data.ast_value);
                                tmpaddr2 = new_address++;

                                new_address = generate_code(stream, new_address, ast->sub[0]);
                                new_address = generate_command(stream, new_address, JUMP, tmpaddr1);
                                generate_command(stream, tmpaddr2, JUMP_NO, new_address);
                                break;
                                
                        default:
                                milan_error("Unknown node type");
                                break;
                }
        
                return new_address;
        }
        else {
                return address;
        }
}

void generate_program(FILE* stream, ast_node* ast)
{
        unsigned int address;
        
        address = generate_code(stream, 0, ast);
        generate_command(stream, address, STOP, 0);
}

