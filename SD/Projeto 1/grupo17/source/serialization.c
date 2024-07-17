//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#include <stdlib.h>
#include <string.h>

#include "serialization.h"

int keyArray_to_buffer(char **keys, char **keys_buf){
    // determine size to allocate buffer knowing that last elements of keys is NULL
    int index = 0;
    int size = 0;
    while (keys[index] != NULL) {
        free(keys[index]);
        index++;
        size = size + (strlen(keys[index]) + 1) * sizeof(char);
    }

    //allocating buffer
    keys_buf = malloc(size);
    if (keys_buf == NULL) return -1;

    //serialization
    for (int i = 0; i < index + 1; i++) {
        keys_buf[i] = malloc((strlen(keys[i]) + 1) * sizeof(char));
        if (keys_buf[i]) return -1;
        strcpy(keys_buf[i], keys[i]);
    }

    return size;
}

//Function that calls this function should free the memory
char** buffer_to_keyArray(char *keys_buf, int keys_buf_size){
    char **keys = malloc(sizeof(char)*keys_buf_size+sizeof(char));
    if (keys == NULL) return NULL;
    memset(keys, 0, keys_buf_size+sizeof(char));
    memcpy(keys, keys_buf, sizeof(char)*keys_buf_size);
    free(keys_buf);
    return keys;
}