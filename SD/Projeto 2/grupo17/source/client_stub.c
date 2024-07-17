//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#include <stdlib.h>
#include <string.h>

#include "../include/client_stub.h"
#include "../include/client_stub-private.h"
#include "../include/network_client.h"

struct rtree_t *rtree_connect(const char *address_port) {
    struct rtree_t *rtree = malloc(sizeof(struct rtree_t));
    char *add_port_cpy = malloc(sizeof(address_port));
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
        return rtree;
    }
    else {
        return NULL;
    }
}

int rtree_disconnect(struct rtree_t *rtree) {
    if (network_close(rtree) == -1) {
        return -1;
    }
    free(rtree->port);
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
    msg->data[0].len = sizeof(entry->key);
    msg->data[0].data = (uint8_t*)entry->key;
    msg->data[1].len = entry->value->datasize;
    msg->data[1].data = entry->value->data;
    msg = network_send_receive(rtree, msg);
    if (msg != NULL && msg->opcode == MESSAGE_T__OPCODE__OP_PUT + 1){
        return 0;
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
    msg->data[0].len = sizeof(key);
    msg->data[0].data = (uint8_t*)key;
    msg = network_send_receive(rtree, msg);
    if (msg != NULL && msg->opcode == MESSAGE_T__OPCODE__OP_GET + 1){
        return data_create2(msg->data[0].len, msg->data->data);
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
    msg->data[0].len = sizeof(key);
    msg->data[0].data = (uint8_t*)key;
    msg = network_send_receive(rtree, msg);
    if (msg != NULL && msg->opcode == MESSAGE_T__OPCODE__OP_DEL + 1){
        return 0;
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
        return   (uint32_t)msg->data[0].data[0] <<  0
               | (uint32_t)msg->data[0].data[1] <<  8
               | (uint32_t)msg->data[0].data[2] << 16
               | (uint32_t)msg->data[0].data[3] << 24;
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
        return   (uint32_t)msg->data[0].data[0] <<  0
                 | (uint32_t)msg->data[0].data[1] <<  8
                 | (uint32_t)msg->data[0].data[2] << 16
                 | (uint32_t)msg->data[0].data[3] << 24;
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
            keys[i] = (char*)msg->data[i].data;
        }
        keys[msg->n_data + 1] = NULL;
        return keys;
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
            values[i] = msg->data[i].data;
        }
        values[msg->n_data] = NULL;
        return values;
    }
    return NULL;
}