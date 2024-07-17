//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#include <stdlib.h>
#include <string.h>

#include "tree.h"
#include "tree-private.h"

void tree_get_keys_help(struct tree_t *tree, char** keys, int* index) {
    if (tree != NULL) {
        if (tree->node_1 != NULL) {
            tree_get_keys_help(tree->node_1, keys, index);
        }
        if (tree->node_2 != NULL) {
            tree_get_keys_help(tree->node_2, keys, index);
        }
        if (tree->entry != NULL && tree->entry->key != NULL) {
            keys[*index] = malloc((strlen(tree->entry->key) + 1) * sizeof(char));
            strcpy(keys[*index], tree->entry->key);
            (*index)++;
        }
    }
}

void tree_get_values_help(struct tree_t *tree, void** values, int* index) {
    if (tree != NULL) {
        if (tree->node_1 != NULL) {
            tree_get_values_help(tree->node_1, values, index);
        }
        if (tree->node_2 != NULL) {
            tree_get_values_help(tree->node_2, values, index);
        }
        if (tree->entry != NULL && tree->entry->value != NULL) {
            values[*index] = data_dup(tree->entry->value);
            (*index)++;
        }
    }
}
