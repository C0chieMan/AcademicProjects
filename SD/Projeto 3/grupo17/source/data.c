//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#include <stdlib.h>
#include <string.h>

#include "data.h"

struct data_t *data_create(int size) {
    if (size <= 0) {
        return NULL;
    }
    struct data_t *data_struct = malloc(sizeof(struct data_t));
    if (data_struct == NULL) {
        return NULL;
    }
    data_struct->datasize = size;
    data_struct->data = malloc(size);
    if (data_struct->data == NULL) {
        return NULL;
    }
    memset(data_struct->data, 0, size);
    return data_struct;
}

struct data_t *data_create2(int size, void *data) {
    if (size < 0) {
        return NULL;
    }
    struct data_t *data_struct = malloc(sizeof(struct data_t));
    if (data_struct == NULL) {
        return NULL;
    }
    data_struct->datasize = size;
    data_struct->data = data;
    return data_struct;
}

void data_destroy(struct data_t *data) {
    if (data != NULL) {
        if (data->data != NULL) {
            free(data->data);
        }
        free(data);
    }
}

struct data_t *data_dup(struct data_t *data) {
    if (data == NULL || data->datasize <= 0 || data->data == NULL) {
        return NULL;
    }
    struct data_t *dup = data_create(data->datasize);
    memcpy(dup->data, data->data, data->datasize);
    return dup;
}

void data_replace(struct data_t *data, int new_size, void *new_data) {
    free(data->data);
    data->datasize = new_size;
    data->data = new_data;
}