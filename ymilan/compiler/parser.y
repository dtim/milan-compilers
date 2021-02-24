%{
        #include "ast.h"
        #include "ident.h"
        #include <stdio.h>
        #include <stdlib.h>

        int yylex();
        void yyerror(char const *);

        ast_node* tmp_ast;
        ast_node* parse_result;
%}

%union {
        int integer;
        char* string;
        ast_node* ast;
};

%token T_BEGIN
%token T_END
%token T_IF
%token T_THEN
%token T_ELSE
%token T_FI
%token T_WHILE
%token T_DO
%token T_OD
%token T_READ
%token T_WRITE

%token T_ASSIGN
%token <integer> T_RELATION

%token <string>  T_IDENT
%token <integer> T_CONST

%left '+' '-'
%left '*' '/'
%left T_NEG

%type <ast> program
%type <ast> stmt_list
%type <ast> stmt
%type <ast> rest_if
%type <ast> cond
%type <ast> expr

%%

program         : T_BEGIN stmt_list T_END             { parse_result = new_block($2); $$ = parse_result; }
                ;

stmt_list       : stmt                                { $$ = $1;                                         }
                | stmt ';' stmt_list                  { tmp_ast = $1; tmp_ast->next = $3; $$ = tmp_ast;  }
                ;

stmt            : /* Empty */                         { $$ = NULL;                                       }
                | T_WRITE '(' expr ')'                { $$ = new_write($3);                              }
                | T_IDENT T_ASSIGN expr               { $$ = new_assign($1, $3);                         }
                | T_WHILE  cond T_DO stmt_list T_OD   { $$ = new_while($2, new_block($4));               }
                | T_IF cond T_THEN stmt_list rest_if  { $$ = new_if($2, new_block($4), $5);              }
                ;

rest_if         : T_ELSE stmt_list T_FI               { $$ = new_block($2);                              }
                | T_FI                                { $$ = NULL;                                       }
                ;

cond            : expr T_RELATION expr                { $$ = new_cond($2, $1, $3);                       }
                ;

expr            : T_IDENT                             { $$ = new_var($1);                                }
                | T_CONST                             { $$ = new_const($1);                              } 
                | T_READ                              { $$ = new_read();                                 }
                | expr '+' expr                       { $$ = new_expr(OP_ADD, $1, $3);                   }
                | expr '-' expr                       { $$ = new_expr(OP_SUB, $1, $3);                   }
                | expr '*' expr                       { $$ = new_expr(OP_MUL, $1, $3);                   }
                | expr '/' expr                       { $$ = new_expr(OP_DIV, $1, $3);                   }
                | '-' expr %prec T_NEG                { $$ = new_expr(OP_NEG, $2, NULL);                 }
                | '(' expr ')'                        { $$ = $2;                                         }
                ;

%%

void yyerror(char const *str)
{
	printf("Syntax error: %s\n", str);
}

