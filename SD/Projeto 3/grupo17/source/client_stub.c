//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#include <stdlib.h>
#include <string.h>

#include "client_stub.h"
#include "client_stub-private.h"
#include "network_client.h"

struct rtree_t *rtree_connect(const char *address_port) {
    struct rtree_t *rtree = malloc(sizeof(struct rtree_t));
    char *add_port_cpy = malloc((strlen(address_port) + 1) * sizeof(char));
    if (rtree == NULL || add_port_cpy == NULL) {
        return NULL;
    }
    strcpy(add_port_cpy, address_port);
    rtree->port = strrchr(add_port_cpy, ':');
    rtree->address = malloc((strlen(add_port_cpy) - strlen(rtree->port) + 1) * sizeof(char));
    if (rtree->address == NULL) {
        return NULL;
    }
    memset(rtree->address, '\0', strlen(add_port_cpy) - strlen(rtree->port) + 1);
    strncpy(rtree->address, add_port_cpy, strlen(add_port_cpy) - strlen(rtree->port));
    memmove(rtree->port, rtree->port+1, strlen(rtree->port)); //deletes ":" from port
    if (network_connect(rtree) == 0) {
        free(add_port_cpy);
        return rtree;
    }
    else {
        free(add_port_cpy);
        free(rtree->address);
        free(rtree->sock_address);
        free(rtree);
        return NULL;
    }
}

int rtree_disconnect(struct rtree_t *rtree) {
    if (network_close(rtree) == -1) {
        return -1;
    }
    free(rtree->address);
    free(rtree->sock_address);
    free(rtree);
    return 0;
}

int rtree_put(struct rtree_t *rtree, struct entry_t *entry) {
    MessageT *msg = malloc(sizeof(MessageT));
    message_t__init(msg);
    msg->opcode = MESSAGE_T__OPCODE__OP_PUT;
    msg->c_type = MESSAGE_T__C_TYPE__CT_ENTRY;
    msg->n_data = 2;
    void *data = malloc(msg->n_data * sizeof(ProtobufCBinaryData));
    msg->data = data;
    if (msg->data == NULL) {
        return -1;
    }
    msg->data[0].len = (strlen(entry->key) + 1) * sizeof(char);
    msg->data[0].data = (uint8_t*)entry->key;
    msg->data[1].len = entry->value->datasize;
    msg->data[1].data = entry->value->data;
    msg = network_send_receive(rtree, msg);
    if (msg != NULL && msg->opcode == MESSAGE_T__OPCODE__OP_PUT + 1 && msg->data != NULL) {
        int op_n =   (uint32_t)msg->data[0].data[0] <<  0
                   | (uint32_t)msg->data[0].data[1] <<  8
                   | (uint32_t)msg->data[0].data[2] << 16
                   | (uint32_t)msg->data[0].data[3] << 24;
        message_t__free_unpacked(msg, NULL);
        return op_n;
    }
    else if (msg != NULL) {
        message_t__free_unpacked(msg, NULL);
    }
    return -1;
}

struct data_t *rtree_get(struct rtree_t *rtree, char *key) {
    MessageT *msg = malloc(sizeof(MessageT));
    message_t__init(msg);
    msg->opcode = MESSAGE_T__OPCODE__OP_GET;
    msg->c_type = MESSAGE_T__C_TYPE__CT_KEY;
    msg->n_data = 1;
    void *data = malloc(msg->n_data * sizeof(ProtobufCBinaryData));
    msg->data = data;
    msg->data[0].len = (strlen(key) + 1) * sizeof(char);
    msg->data[0].data = (uint8_t*)key;
    msg = network_send_receive(rtree, msg);
    if (msg != NULL && msg->opcode == MESSAGE_T__OPCODE__OP_GET + 1) {
        struct data_t *value = NULL;
        if (msg->data[0].len == 0) {
            value = data_create2(0, NULL);
        }
        else {
            value = data_create(msg->data[0].len);
            memcpy(value->data, msg->data[0].data, msg->data[0].len);
        }
        message_t__free_unpacked(msg, NULL);
        return value;
    }
    else if (msg != NULL) {
        message_t__free_unpacked(msg, NULL);
    }
    return NULL;
}

int rtree_del(struct rtree_t *rtree, char *key) {
    MessageT *msg = malloc(sizeof(MessageT));
    message_t__init(msg);
    msg->opcode = MESSAGE_T__OPCODE__OP_DEL;
    msg->c_type = MESSAGE_T__C_TYPE__CT_KEY;
    msg->n_data = 1;
    void *data = malloc(msg->n_data * sizeof(ProtobufCBinaryData));
    msg->data = data;
    msg->data[0].len = (strlen(key) + 1) * sizeof(char);
    msg->data[0].data = (uint8_t*)key;
    msg = network_send_receive(rtree, msg);
    if (msg != NULL && msg->opcode == MESSAGE_T__OPCODE__OP_DEL + 1 && msg->data != NULL) {
        int op_n =   (uint32_t)msg->data[0].data[0] <<  0
                   | (uint32_t)msg->data[0].data[1] <<  8
                   | (uint32_t)msg->data[0].data[2] << 16
                   | (uint32_t)msg->data[0].data[3] << 24;
        message_t__free_unpacked(msg, NULL);
        return op_n;
    }
    else if (msg != NULL) {
        message_t__free_unpacked(msg, NULL);
    }
    return -1;
}

int rtree_size(struct rtree_t *rtree) {
    MessageT *msg = malloc(sizeof(MessageT));
    message_t__init(msg);
    msg->opcode = MESSAGE_T__OPCODE__OP_SIZE;
    msg->c_type = MESSAGE_T__C_TYPE__CT_NONE;
    msg->n_data = 0;
    msg->data = NULL;
    msg = network_send_receive(rtree, msg);
    if (msg != NULL && msg->opcode == MESSAGE_T__OPCODE__OP_SIZE + 1 && msg->data != NULL) {
        int size =   (uint32_t)msg->data[0].data[0] <<  0
                   | (uint32_t)msg->data[0].data[1] <<  8
                   | (uint32_t)msg->data[0].data[2] << 16
                   | (uint32_t)msg->data[0].data[3] << 24;
        message_t__free_unpacked(msg, NULL);
        return size;
    }
    else if (msg != NULL) {
        message_t__free_unpacked(msg, NULL);
    }
    return -1;
}

int rtree_height(struct rtree_t *rtree) {
    MessageT *msg = malloc(sizeof(MessageT));
    message_t__init(msg);
    msg->opcode = MESSAGE_T__OPCODE__OP_HEIGHT;
    msg->c_type = MESSAGE_T__C_TYPE__CT_NONE;
    msg->n_data = 0;
    msg->data = NULL;
    msg = network_send_receive(rtree, msg);
    if (msg != NULL && msg->opcode == MESSAGE_T__OPCODE__OP_HEIGHT + 1 && msg->data != NULL){
        int height =   (uint32_t)msg->data[0].data[0] <<  0
                     | (uint32_t)msg->data[0].data[1] <<  8
                     | (uint32_t)msg->data[0].data[2] << 16
                     | (uint32_t)msg->data[0].data[3] << 24;
        message_t__free_unpacked(msg, NULL);
        return height;
    }
    else if (msg != NULL) {
        message_t__free_unpacked(msg, NULL);
    }
    return -1;
}

char **rtree_get_keys(struct rtree_t *rtree) {
    MessageT *msg = malloc(sizeof(MessageT));
    message_t__init(msg);
    msg->opcode = MESSAGE_T__OPCODE__OP_GETKEYS;
    msg->c_type = MESSAGE_T__C_TYPE__CT_NONE;
    msg->n_data = 0;
    msg->data = NULL;
    msg = network_send_receive(rtree, msg);
    if (msg != NULL && msg->opcode == MESSAGE_T__OPCODE__OP_GETKEYS + 1) {
        char** keys = malloc((msg->n_data + 1) * sizeof(char*));
        if (keys == NULL) {
            return NULL;
        }
        for (int i = 0; i < msg->n_data; i++) {
            keys[i] = malloc(msg->data[i].len);
            if (keys[i] == NULL) {
                return NULL;
            }
            strcpy(keys[i], (char*)msg->data[i].data);
        }
        keys[msg->n_data] = NULL;
        message_t__free_unpacked(msg, NULL);
        return keys;
    }
    else if (msg != NULL) {
        message_t__free_unpacked(msg, NULL);
    }
    return NULL;
}

void **rtree_get_values(struct rtree_t *rtree) {
    MessageT *msg = malloc(sizeof(MessageT));
    message_t__init(msg);
    msg->opcode = MESSAGE_T__OPCODE__OP_GETVALUES;
    msg->c_type = MESSAGE_T__C_TYPE__CT_NONE;
    msg->n_data = 0;
    msg->data = NULL;
    msg = network_send_receive(rtree, msg);
    if (msg != NULL && msg->opcode == MESSAGE_T__OPCODE__OP_GETVALUES + 1){
        void** values = malloc((msg->n_data + 1) * sizeof(void*));
        if (values == NULL) {
            return NULL;
        }
        for (int i = 0; i < msg->n_data; i++) {
            values[i] = malloc(msg->data[i].len);
            if (values[i] == NULL) {
                return NULL;
            }
            memcpy(values[i], msg->data[i].data, msg->data[i].len);
        }
        values[msg->n_data] = NULL;
        message_t__free_unpacked(msg, NULL);
        return values;
    }
    else if (msg != NULL) {
        message_t__free_unpacked(msg, NULL);
    }
    return NULL;
}

int rtree_verify(struct rtree_t *rtree, int op_n) {
    MessageT *msg = malloc(sizeof(MessageT));
    message_t__init(msg);
    msg->opcode = MESSAGE_T__OPCODE__OP_VERIFY;
    msg->c_type = MESSAGE_T__C_TYPE__CT_RESULT;
    msg->n_data = 1;
    uint8_t *op_n_ptr = malloc(4 * sizeof(uint8_t));
    msg->data = malloc(sizeof(ProtobufCBinaryData));
    if (msg->data != NULL && op_n_ptr != NULL) {
        op_n_ptr[0] = (uint8_t)(op_n >>  0);
        op_n_ptr[1] = (uint8_t)(op_n >>  8);
        op_n_ptr[2] = (uint8_t)(op_n >> 16);
        op_n_ptr[3] = (uint8_t)(op_n >> 24);
        msg->data[0].len = 4 * sizeof(uint8_t);
        msg->data[0].data = op_n_ptr;
    }
    msg = network_send_receive(rtree, msg);
    if (msg != NULL && msg->opcode == MESSAGE_T__OPCODE__OP_VERIFY + 1 && msg->data != NULL){
        int done =   (uint32_t)msg->data[0].data[0] <<  0
                   | (uint32_t)msg->data[0].data[1] <<  8
                   | (uint32_t)msg->data[0].data[2] << 16
                   | (uint32_t)msg->data[0].data[3] << 24;
        message_t__free_unpacked(msg, NULL);
        return done;
    }
    else if (msg != NULL) {
        message_t__free_unpacked(msg, NULL);
    }
    return -1;
}