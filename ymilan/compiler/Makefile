milan:	lex.yy.c parser.tab.h milan.c ident.c ident.h ast.c ast.h code.h code.c
	gcc -Wall -o milan milan.c lex.yy.c parser.tab.c ident.c ast.c code.c

lex.yy.c:	lexer.l
	flex lexer.l

parser.tab.h:	parser.y
	bison -d parser.y
	
clean:
	rm lex.yy.c parser.tab.h parser.tab.c

distclean:
	rm milan lex.yy.c parser.tab.h parser.tab.c
