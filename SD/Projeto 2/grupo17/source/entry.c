//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#include <stdlib.h>
#include <string.h>

#include "../include/data.h"
#include "../include/entry.h"

struct entry_t *entry_create(char *key, struct data_t *data) {
    struct entry_t *entry = malloc(sizeof(struct entry_t));
    if (entry == NULL) {
        return NULL;
    }
    entry->key = key;
    entry->value = data;
    return entry;
}

void entry_destroy(struct entry_t *entry) {
    if (entry != NULL) {
        if (entry->key != NULL) {
            free(entry->key);
        }
        if (entry->value != NULL) {
            data_destroy(entry->value);
        }
        free(entry);
    }
}

struct entry_t *entry_dup(struct entry_t *entry) {
    char *dup_key = malloc(sizeof(&(entry->key)));
    if (dup_key == NULL) {
        return NULL;
    }
    strcpy(dup_key, entry->key);
    void *dup_value = data_dup(entry->value);
    struct entry_t *dup = entry_create(dup_key, dup_value);
    return dup;
}

void entry_replace(struct entry_t *entry, char *new_key, struct data_t *new_value) {
    free(entry->key);
    data_destroy(entry->value);
    entry->key = new_key;
    entry->value = new_value;
}

int entry_compare(struct entry_t *entry1, struct entry_t *entry2) {
    if (strcmp(entry1->key, entry2->key) < 0) {
        return -1;
    }
    else if (strcmp(entry1->key, entry2->key) > 0) {
        return 1;
    }
    else {
        return 0;
    }
}