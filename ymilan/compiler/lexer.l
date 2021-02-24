/*
	Лексический анализатор языка Милан
*/

%{
#include "ast.h"
#include "parser.tab.h"
#include "ident.h"
#include <stdio.h>
#include <stdlib.h>
%}

%option noyywrap

%x comment

WHITESPACE	[ \t\n]+
INT		[0-9]+
IDENT		[a-zA-Z][_0-9a-zA-Z]*
ASSIGN		:=
LBRACKET	\(
RBRACKET	\)
EQ		=
NE		!=
LT		<
GT		>
LE		<=
GE		>=
%%

{WHITESPACE}

"/*"                    BEGIN(comment);
<comment>[^*]*
<comment>"*"+[^*/]*
<comment>"*"+"/"        BEGIN(INITIAL);

"+"                     { return '+'; }
"-"	                { return '-'; }
"*"	                { return '*'; }
"/"                     { return '/'; }
";"                     { return ';'; }

{ASSIGN}                { return T_ASSIGN; }
"("                     { return '('; }
")"                     { return ')'; }

{EQ}                    { yylval.integer = 0; return T_RELATION; }
{NE}                    { yylval.integer = 1; return T_RELATION; }
{LT}                    { yylval.integer = 2; return T_RELATION; }
{GT}                    { yylval.integer = 3; return T_RELATION; }
{LE}                    { yylval.integer = 4; return T_RELATION; }
{GE}                    { yylval.integer = 5; return T_RELATION; }

BEGIN                   { return T_BEGIN; }
END                     { return T_END;   }
IF                      { return T_IF;    }
THEN                    { return T_THEN;  }
ELSE                    { return T_ELSE;  }
FI                      { return T_FI;    }
WHILE                   { return T_WHILE; }
DO                      { return T_DO;    }
OD                      { return T_OD;    }
READ                    { return T_READ;  }
WRITE                   { return T_WRITE; }

{INT}                   { yylval.integer = atoi(yytext); return T_CONST;        }
{IDENT}                 { yylval.string = register_identifier(yytext); return T_IDENT; }

<<EOF>>                 { yyterminate(); }

%%

