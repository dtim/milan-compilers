#include "milan.h"
#include "ast.h"
#include "code.h"
#include "parser.tab.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

extern FILE *yyin;
extern ast_node* parse_result;

int yyparse();

struct config {
        int dump_parse_tree;
        char* parse_tree_filename;
        FILE* parse_tree_stream;

        char* input_filename;
        FILE* input_stream;

        char* output_filename;
        FILE* output_stream;

        int just_help;
} config;

void print_help()
{
        printf("Usage: milan.exe [-D [ast-file]] [-o <out-file>] [in-file]\n");
        printf("       milan.exe -h\n\n");
        printf("Options:\n");
        printf("  -h              Show this help screen and exit\n");
        printf("  -D [<ast-file>] Print AST (into <ast-file> if it is specified\n");
        printf("                  or to stdout otherwise)\n");
        printf("  -o <out-file>   Write output into file <out-file> (stdout by default)\n");
        printf("\nInput is read from stdin by default\n\n");
}

int parse_arg(int argc, char** argv)
{
        if(argc > 0) {
                if(0 == strcmp(argv[0], "-h")) {
                        config.just_help = 1;
                        return 1;
                }
                
                if(0 == strcmp(argv[0], "-D")) {
                        config.dump_parse_tree = 1;
                        
                        if(argc > 1 && argv[1][0] != '-') {
                                config.parse_tree_filename = argv[1];
                                return 2;
                        }

                        return 1;
                }

                if(0 == strcmp(argv[0], "-o")) {
                        if(argc > 1 && argv[1][0] != '-') {
                                config.output_filename = argv[1];
                                return 2;
                        }
                        else {
                                return 0;
                        }
                }

                if(argv[0][0] != '-') {
                        config.input_filename = argv[0];
                        return 1;
                }
        }

        return 0;
}

int parse_arguments(int argc, char** argv)
{
        int pos, parsed;

        config.dump_parse_tree = 0;
        config.parse_tree_filename = NULL;
        config.parse_tree_stream = stdout;
        config.input_filename = NULL;
        config.input_stream = stdin;
        config.output_filename = NULL;
        config.output_stream = stdout;
        config.just_help = 0;

        pos = 1;
        while(pos < argc) {
                parsed = parse_arg(argc - pos, argv + pos);
                if(parsed) {
                        pos += parsed;
                }
                else {
                        return pos;
                }
        }

        if(config.just_help) {
                print_help();
                return 0;
        }
        
        if(config.parse_tree_filename) {
                config.parse_tree_stream = fopen(config.parse_tree_filename, "wt");
                if(!config.parse_tree_stream) {
                        fprintf(stderr, "Warning : unable to open parse tree dump file for writing, dumping to stdout\n");
                        config.parse_tree_stream = stdout;
                        config.parse_tree_filename = NULL;
                }
        }

        if(config.output_filename) {
                config.output_stream = fopen(config.output_filename, "wt");
                if(!config.output_stream) {
                        fprintf(stderr, "Warning : unable to open program output file for writing, dumping to stdout\n");
                        config.output_stream = stdout;
                        config.output_filename = NULL;
                }
        }

        if(config.input_filename) {
                config.input_stream = fopen(config.input_filename, "rt");
                if(!config.input_stream) {
                        fprintf(stderr, "Warning : unable to read input file, reading from stdin\n");
                        config.input_stream = stdin;
                        config.input_filename = NULL;
                }
        }

        return 0;
}

int main(int argc, char **argv)
{
        int error_arg;
        int retcode = 0;

        fprintf(stderr, "Welcome to the wonderful world of the do-it-yourself Milan compiler!\n\n");
       
        error_arg = parse_arguments(argc, argv);
        if(error_arg) {
                fprintf(stderr, "Error   : Command line error at option '%s'\n", argv[error_arg]);
                return 1;
        }

        if(config.just_help) {
                return 0;
        }

        if(config.dump_parse_tree) {
                fprintf(stderr, "Option  : dumping parse tree to %s\n", 
                                config.parse_tree_filename ? config.parse_tree_filename : "standard output");
        }

        fprintf(stderr, "Option  : reading input from %s\n",
                        config.input_filename ? config.input_filename : "standard input");
        
        fprintf(stderr, "Option  : writing Milan VM program to %s\n",
                        config.output_filename ? config.output_filename : "standard output");

        yyin = config.input_stream;
        
        if(0 == yyparse()) {
                if(config.dump_parse_tree) {
                        dump_ast(config.parse_tree_stream, parse_result);
                }

                print_id_table(config.output_stream);
                generate_program(config.output_stream, parse_result);
        }
        else {
                fprintf(stderr, "Error   : syntax error\n");
                retcode = 1;
        }

        if(config.input_filename) {
                fclose(config.input_stream);
        }

        if(config.output_filename) {
                fclose(config.output_stream);
        }

        if(config.parse_tree_filename) {
                fclose(config.parse_tree_stream);
        }

        return retcode;
}

void milan_error(char const *msg)
{
        printf("Milan error: %s\n", msg);
        exit(1);
}

