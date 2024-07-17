//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#include <stdlib.h>
#include <string.h>

#include "data.h"
#include "entry.h"
#include "tree.h"
#include "tree-private.h"

struct tree_t *tree_create() {
    struct tree_t *tree = malloc(sizeof(struct tree_t));
    if (tree == NULL) {
        return NULL;
    }
    tree->entry = NULL;
    tree->node_1 = NULL;
    tree->node_2 = NULL;
    return tree;
}

void tree_destroy(struct tree_t *tree) {
    if (tree != NULL) {
        if (tree->entry != NULL) {
            entry_destroy(tree->entry);
        }
        if (tree->node_1 != NULL) {
            tree_destroy(tree->node_1);
        }
        if (tree->node_2 != NULL) {
            tree_destroy(tree->node_2);
        }
        free(tree);
    }
}

int tree_put(struct tree_t *tree, char *key, struct data_t *value) {
    if (tree == NULL || key == NULL || value == NULL) {
        return -1;
    }
    if (tree->entry == NULL || tree->entry->key == NULL) {
        char *dup_key = malloc((strlen(key) + 1) * sizeof(char));
        if (dup_key == NULL) {
            return -1;
        }
        strcpy(dup_key, key);
        void *dup_value = data_dup(value);
        tree->entry = entry_create(dup_key, dup_value);
        return 0;
    }
    else {
        int compare = strcmp(key, tree->entry->key);
        if (compare < 0) {
            if (tree->node_1 == NULL) {
                tree->node_1 = tree_create();
                if (tree->node_1 == NULL) {
                    return -1;
                }
            }
            return tree_put(tree->node_1, key, value);
        } else if (compare > 0) {
            if (tree->node_2 == NULL) {
                tree->node_2 = tree_create();
                if (tree->node_2 == NULL) {
                    return -1;
                }
            }
            return tree_put(tree->node_2, key, value);
        } else {
            char *dup_key = malloc((strlen(key) + 1) * sizeof(char));
            if (dup_key == NULL) {
                return -1;
            }
            strcpy(dup_key, key);
            void *dup_value = data_dup(value);
            entry_replace(tree->entry, dup_key, dup_value);
            return 0;
        }
    }
}

struct data_t *tree_get(struct tree_t *tree, char *key) {
    if (tree == NULL || tree->entry == NULL || tree->entry->key == NULL) {
        return NULL;
    }
    else {
        int compare = strcmp(key, tree->entry->key);
        if (compare < 0) {
            if (tree->node_1 == NULL) {
                return NULL;
            }
            return tree_get(tree->node_1, key);
        } else if (compare > 0) {
            if (tree->node_2 == NULL) {
                return NULL;
            }
            return tree_get(tree->node_2, key);
        } else {
            return data_dup(tree->entry->value);
        }
    }
}

int tree_del(struct tree_t *tree, char *key) {
    if (tree->entry == NULL) {
        return -1;
    }
    else {
        int compare = strcmp(key, tree->entry->key);
        if (compare < 0) {
            if (tree->node_1 == NULL) {
                return -1;
            }
            else if (tree->node_1->node_1 == NULL && tree->node_1->node_2 == NULL) {
                tree_destroy(tree->node_1);
                tree->node_1 = NULL;
                return 0;
            }
            else {
                return tree_del(tree->node_1, key);
            }
        } else if (compare > 0) {
            if (tree->node_2 == NULL) {
                return -1;
            }
            else if (strcmp(key, tree->node_2->entry->key) == 0 && tree->node_2->node_1 == NULL &&
                    tree->node_2->node_2 == NULL) {
                tree_destroy(tree->node_2);
                tree->node_2 = NULL;
                return 0;
            }
            else {
                return tree_del(tree->node_2, key);
            }
        } else {
            if (tree->node_1 == NULL && tree->node_2 == NULL) {
                tree_destroy(tree);
            }
            else if (tree->node_1 != NULL && tree->node_2 == NULL) {
                struct tree_t *to_delete = tree;
                struct tree_t *replacement = tree->node_1;
                memcpy(tree, replacement, sizeof(struct tree_t));
                to_delete->node_1 = NULL;
                tree_destroy(to_delete);
            }
            else if (tree->node_1 == NULL && tree->node_2 != NULL) {
                struct tree_t *to_delete = tree;
                struct tree_t *replacement = tree->node_2;
                memcpy(tree, replacement, sizeof(struct tree_t));
                to_delete->node_2 = NULL;
                tree_destroy(to_delete);
            }
            else {
                struct tree_t *parent = tree;
                struct tree_t *replacement = tree->node_2;
                while (replacement->node_1 != NULL || replacement->node_2 != NULL) {
                    while (replacement->node_1 != NULL) {
                        parent = replacement;
                        replacement = replacement->node_1;
                    }
                    if (replacement->node_2 != NULL) {
                        parent = replacement;
                        replacement = replacement->node_2;
                    }
                }
                if (tree->node_1 != NULL) {
                    replacement->node_1 = tree->node_1;
                }
                if (tree->node_2 != NULL) {
                    replacement->node_2 = tree->node_2;
                }
                memcpy(tree, replacement, sizeof(struct tree_t));
                if (parent->node_1 != NULL) {
                    parent->node_1 = NULL;
                }
                else if (parent->node_2 != NULL) {
                    parent->node_2 = NULL;
                }
                if (replacement->entry != NULL) {
                    replacement->entry = NULL;
                }
                if (replacement->node_1 != NULL) {
                    replacement->node_1 = NULL;
                }
                if (replacement->node_2 != NULL) {
                    replacement->node_2 = NULL;
                }
                tree_destroy(replacement);
            }
            return 0;
        }
    }
}

int tree_size(struct tree_t *tree) {
    if (tree->entry == NULL) {
        return 0;
    }
    else if (tree->node_1 == NULL && tree->node_2 == NULL){
        return 1;
    }
    else if (tree->node_1 != NULL && tree->node_2 == NULL){
        return 1 + tree_size(tree->node_1);
    }
    else if (tree->node_1 == NULL && tree->node_2 != NULL){
        return 1 + tree_size(tree->node_2);
    }
    else {
        return 1 + tree_size(tree->node_1) + tree_size(tree->node_2);
    }
}

int tree_height(struct tree_t *tree) {
    if (tree->entry == NULL){
        return 0;
    }
    else {
        int left = 0;
        int right = 0;
        if (tree->node_1 != NULL) {
            left = tree_height(tree->node_1);
        }
        if (tree->node_2 != NULL) {
            right = tree_height(tree->node_2);
        }
        if (left > right){
            return 1 + left;
        }
        else {
            return 1 + right;
        }
    }
}

char **tree_get_keys(struct tree_t *tree) {
    char **keys = malloc((tree_size(tree)+1) * sizeof(char*));
    int index = 0;
    tree_get_keys_help(tree, keys, &index);
    keys[index] = NULL;
    return keys;
}

void **tree_get_values(struct tree_t *tree) {
    void **values = malloc((tree_size(tree)+1) * sizeof(void*));
    int index = 0;
    tree_get_values_help(tree, values, &index);
    values[index] = NULL;
    return values;
}

void tree_free_keys(char **keys) {
    int index = 0;
    while (keys[index] != NULL) {
        free(keys[index]);
        index++;
    }
    free(keys);
}

void tree_free_values(void **values) {
    int index = 0;
    while (values[index] != NULL) {
        data_destroy(values[index]);
        index++;
    }
    free(values);
}