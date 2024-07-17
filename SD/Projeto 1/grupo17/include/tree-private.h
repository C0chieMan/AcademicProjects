#ifndef _TREE_PRIVATE_H
#define _TREE_PRIVATE_H

#include "entry.h"

struct tree_t {
	struct entry_t *entry;
    struct tree_t *node_1;
    struct tree_t *node_2;
};

void tree_get_keys_help(struct tree_t *tree, char** keys, int* index);

void tree_get_values_help(struct tree_t *tree, void** values, int* index);

#endif
