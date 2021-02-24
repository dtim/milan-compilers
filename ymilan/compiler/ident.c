#include "ident.h"
#include "milan.h"
#include <string.h>

char id_table[MAX_ID_LENGTH][MAX_ID_NUMBER];

int max_id_number = -1;

char* register_identifier(char *name)
{
        int index = 0;

        while(index <= max_id_number) {
                if(0 == strcmp(name, id_table[index]))
                        break;
                ++index;
        }

        if(index <= max_id_number) {
                return id_table[index];
        }
        else {
                if(max_id_number < MAX_ID_NUMBER) {
                        ++max_id_number;
                        memset(id_table[max_id_number], 0, MAX_ID_LENGTH);
                        strncpy(id_table[max_id_number], name, MAX_ID_LENGTH - 1);
                        return id_table[max_id_number];
                }
        }

        milan_error("Too many identifiers");
        return NULL;
}

int find_name(char *name)
{
        int index = 0;

        while(index <= max_id_number) {
                if(0 == strcmp(name, id_table[index]))
                        break;
                ++index;
        }

        if(index <= max_id_number) {
                return index;
        }
        else {
                return -1;
        }
}

void print_id_table(FILE *stream)
{
        int i;

        for(i = 0; i <= max_id_number; ++i) {
                fprintf(stream, "SET\t%d\t%d\t; %s\n", i, 0, id_table[i]);
        }

        fprintf(stream, "\n");
}

