//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#include <stdlib.h>

#include "../include/tree_skel.h"

struct tree_t *root;

int tree_skel_init() {
    root = tree_create();
    if (root == NULL) {
        return -1;
    }
    else {
        return 0;
    }
}

void tree_skel_destroy() {
    tree_destroy(root);
}

int invoke(MessageT *msg) {
    if (root != NULL) {
        if (msg->opcode == MESSAGE_T__OPCODE__OP_SIZE && msg->c_type == MESSAGE_T__C_TYPE__CT_NONE) {
            msg->opcode = MESSAGE_T__OPCODE__OP_SIZE + 1;
            msg->c_type = MESSAGE_T__C_TYPE__CT_RESULT;
            int size = tree_size(root);
            msg->n_data = 1;
            uint8_t *size_ptr = malloc(4 * sizeof(uint8_t));
            msg->data = malloc(sizeof(ProtobufCBinaryData));
            if (msg->data != NULL && size_ptr != NULL) {
                size_ptr[0] = (uint8_t)(size >>  0);
                size_ptr[1] = (uint8_t)(size >>  8);
                size_ptr[2] = (uint8_t)(size >> 16);
                size_ptr[3] = (uint8_t)(size >> 24);
                msg->data[0].len = 4 * sizeof(uint8_t);
                msg->data[0].data = size_ptr;
                return 0;
            }
        }
        else if (msg->opcode == MESSAGE_T__OPCODE__OP_HEIGHT && msg->c_type == MESSAGE_T__C_TYPE__CT_NONE) {
            msg->opcode = MESSAGE_T__OPCODE__OP_HEIGHT + 1;
            msg->c_type = MESSAGE_T__C_TYPE__CT_RESULT;
            int height = tree_height(root);
            msg->n_data = 1;
            uint8_t *height_ptr = malloc(4 * sizeof(uint8_t));
            msg->data = malloc(sizeof(ProtobufCBinaryData));
            if (msg->data != NULL && height_ptr != NULL) {
                height_ptr[0] = (uint8_t)(height >>  0);
                height_ptr[1] = (uint8_t)(height >>  8);
                height_ptr[2] = (uint8_t)(height >> 16);
                height_ptr[3] = (uint8_t)(height >> 24);
                msg->data[0].len = 4 * sizeof(uint8_t);
                msg->data[0].data = height_ptr;
                return 0;
            }
        }
        else if (msg->opcode == MESSAGE_T__OPCODE__OP_DEL && msg->c_type == MESSAGE_T__C_TYPE__CT_KEY &&
                msg->n_data == 1) {
            char *key = (char *)msg->data[0].data;
            if (tree_del(root, key) == 0) {
                msg->opcode = MESSAGE_T__OPCODE__OP_DEL + 1;
                msg->c_type = MESSAGE_T__C_TYPE__CT_NONE;
                msg->n_data = 0;
                msg->data = NULL;
                return 0;
            }
        }
        else if (msg->opcode == MESSAGE_T__OPCODE__OP_GET && msg->c_type == MESSAGE_T__C_TYPE__CT_KEY &&
                msg->n_data == 1) {
            char *key = (char *)msg->data[0].data;
            msg->opcode = MESSAGE_T__OPCODE__OP_GET + 1;
            msg->c_type = MESSAGE_T__C_TYPE__CT_VALUE;
            msg->n_data = 1;
            msg->data = malloc(sizeof(ProtobufCBinaryData));
            struct data_t *data = tree_get(root, key);
            if (data != NULL) {
                if (msg->data != NULL) {
                    msg->data[0].len = data->datasize;
                    msg->data[0].data = data->data;
                    free(data);
                    return 0;
                }
            }
            else {
                if (msg->data != NULL) {
                    msg->data[0].len = 0;
                    msg->data[0].data = NULL;
                    return 0;
                }
            }
        }
        else if (msg->opcode == MESSAGE_T__OPCODE__OP_PUT && msg->c_type == MESSAGE_T__C_TYPE__CT_ENTRY &&
                msg->n_data == 2) {
            struct data_t *value = malloc(sizeof(struct data_t));
            if (value != NULL) {
                value->datasize = msg->data[1].len;
                value->data = msg->data[1].data;
                if (tree_put(root, (char*)msg->data[0].data, value) == 0) {
                    msg->opcode = MESSAGE_T__OPCODE__OP_PUT + 1;
                    msg->c_type = MESSAGE_T__C_TYPE__CT_NONE;
                    msg->n_data = 0;
                    msg->data = NULL;
                    return 0;
                }
            }
        }
        else if (msg->opcode == MESSAGE_T__OPCODE__OP_GETKEYS && msg->c_type == MESSAGE_T__C_TYPE__CT_NONE) {
            msg->n_data = tree_size(root);
            char **keys = tree_get_keys(root);
            msg->data = malloc(msg->n_data * sizeof(ProtobufCBinaryData));
            if (msg->data != NULL) {
                msg->opcode = MESSAGE_T__OPCODE__OP_GETKEYS + 1;
                msg->c_type = MESSAGE_T__C_TYPE__CT_KEYS;
                for (int i = 0; i < msg->n_data; i++) {
                    msg->data[i].len = sizeof(keys[i]);
                    msg->data[i].data = (uint8_t*)keys[i];
                }
                return 0;
            }
        }
        else if (msg->opcode == MESSAGE_T__OPCODE__OP_GETVALUES && msg->c_type == MESSAGE_T__C_TYPE__CT_NONE) {
            msg->n_data = tree_size(root);
            void **values = tree_get_values(root);
            msg->data = malloc(msg->n_data * sizeof(ProtobufCBinaryData));
            if (msg->data != NULL) {
                msg->opcode = MESSAGE_T__OPCODE__OP_GETVALUES + 1;
                msg->c_type = MESSAGE_T__C_TYPE__CT_VALUES;
                for (int i = 0; i < msg->n_data; i++) {
                    struct data_t *value = (struct data_t *)values[i];
                    msg->data[i].len = value->datasize;
                    msg->data[i].data = value->data;
                }
                free(values);
                return 0;
            }
        }
    }
    msg->opcode = MESSAGE_T__OPCODE__OP_ERROR;
    msg->c_type = MESSAGE_T__C_TYPE__CT_NONE;
    msg->n_data = 0;
    msg->data = NULL;
    return -1;
}