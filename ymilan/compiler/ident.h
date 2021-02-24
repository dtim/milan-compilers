#ifndef _MILAN_IDENT_H
#define _MILAN_IDENT_H

#include <stdio.h>

#define MAX_ID_LENGTH    64
#define MAX_ID_NUMBER    1000

extern char id_table[MAX_ID_LENGTH][MAX_ID_NUMBER];

char* register_identifier(char *name);
void print_id_table(FILE *);
int find_name(char *name);

#endif

