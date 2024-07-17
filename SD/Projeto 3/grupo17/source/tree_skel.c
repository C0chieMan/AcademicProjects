//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#include <pthread.h>
#include <stdlib.h>
#include <string.h>

#include "tree_skel.h"
#include "tree_skel-private.h"

struct tree_t *root;
int number_threads;
pthread_t *threads;
int last_assigned;
struct op_proc *proc;
struct request_t *queue_head;
pthread_mutex_t queue_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cond = PTHREAD_COND_INITIALIZER;
pthread_mutex_t tree_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t proc_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t assign_mutex = PTHREAD_MUTEX_INITIALIZER;
int stop_skel;

int tree_skel_init(int N) {
    stop_skel = 0;
    root = tree_create();
    last_assigned = 1;
    proc = malloc(sizeof(struct op_proc));
    queue_head = NULL;
    number_threads = N;
    threads = malloc(number_threads * sizeof(pthread_t));
    if (root != NULL && proc != NULL && threads != NULL) {
        proc->max_proc = 0;
        proc->in_progress = malloc(N * sizeof(int));
        if (proc->in_progress != NULL) {
            for (int i = 0; i < number_threads; i++) {
                int *i_ptr = &i;
                proc->in_progress[i] = 0;
                pthread_create(&threads[i], NULL, process_request, i_ptr);
            }
            return 0;
        }
    }
    return -1;
}

void *process_request (void *params) {
    while (!stop_skel) { //loops until tree_skel_destroy is called
        pthread_mutex_lock(&queue_mutex);
        if (queue_head == NULL) {
            pthread_cond_wait(&cond, &queue_mutex);
        }
        if (stop_skel) {
            pthread_mutex_unlock(&queue_mutex);
            break;
        }
        struct request_t *request = queue_head;
        queue_head = queue_head->next_in_line;
        pthread_mutex_unlock(&queue_mutex);
        proc->in_progress[*(int *)params] = request->op_n;
        pthread_mutex_lock(&tree_mutex);
        if (request->op == 1) {
            tree_put(root, request->key, request->data);
            data_destroy(request->data);
        }
        else {
            tree_del(root, request->key);
        }
        free(request->key);
        pthread_mutex_unlock(&tree_mutex);
        pthread_mutex_lock(&proc_mutex);
        if (request->op_n > proc->max_proc) {
            proc->max_proc = request->op_n;
        }
        pthread_mutex_unlock(&proc_mutex);
        proc->in_progress[*(int *)params] = 0;
        free(request);
    }
    return 0;
}

void recursive_free_request(struct request_t *request) {
    if (request != NULL) {
        if (request->next_in_line != NULL) {
            recursive_free_request(request->next_in_line);
        }
        if (request->data != NULL) {
            data_destroy(request->data);
        }
        free(request->key);
        free(request);
    }
}

void tree_skel_destroy() {
    stop_skel = 1;
    pthread_mutex_lock(&queue_mutex);
    pthread_cond_broadcast(&cond);
    pthread_mutex_unlock(&queue_mutex);
    for (int i = 0; i < number_threads; i++) {
        pthread_join(threads[i], NULL);
    }
    tree_destroy(root);
    free(threads);
    free(proc->in_progress);
    free(proc);
    recursive_free_request(queue_head);
}

int invoke(MessageT *msg) {
    if (root != NULL) {
        if (msg->opcode == MESSAGE_T__OPCODE__OP_SIZE && msg->c_type == MESSAGE_T__C_TYPE__CT_NONE) {
            msg->opcode = MESSAGE_T__OPCODE__OP_SIZE + 1;
            msg->c_type = MESSAGE_T__C_TYPE__CT_RESULT;
            pthread_mutex_lock(&tree_mutex);
            int size = tree_size(root);
            pthread_mutex_unlock(&tree_mutex);
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
            pthread_mutex_lock(&tree_mutex);
            int height = tree_height(root);
            pthread_mutex_unlock(&tree_mutex);
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
            struct request_t *request = malloc(sizeof(struct request_t));
            if (request != NULL) {
                pthread_mutex_lock(&assign_mutex);
                request->op_n = last_assigned;
                last_assigned++;
                pthread_mutex_unlock(&assign_mutex);
                int op_n = request->op_n;
                request->op = 0;
                request->key = (char*)msg->data[0].data;
                request->data = NULL;
                request->next_in_line = NULL;
                pthread_mutex_lock(&queue_mutex);
                if (queue_head == NULL) {
                    queue_head = request;
                }
                else {
                    struct request_t *queue_request = queue_head;
                    while (queue_request->next_in_line != NULL) {
                        queue_request = queue_request->next_in_line;
                    }
                    queue_request->next_in_line = malloc(sizeof(struct request_t));
                    *queue_request->next_in_line = *request;
                    free(request);
                }
                pthread_cond_signal(&cond);
                pthread_mutex_unlock(&queue_mutex);
                free(msg->data);
                msg->opcode = MESSAGE_T__OPCODE__OP_DEL + 1;
                msg->c_type = MESSAGE_T__C_TYPE__CT_RESULT;
                msg->n_data = 1;
                uint8_t *op_n_ptr = malloc(4 * sizeof(uint8_t));
                msg->data = malloc(sizeof(ProtobufCBinaryData));
                if (msg->data != NULL && op_n_ptr != NULL) {
                    op_n_ptr[0] = (uint8_t)(op_n >> 0);
                    op_n_ptr[1] = (uint8_t)(op_n >> 8);
                    op_n_ptr[2] = (uint8_t)(op_n >> 16);
                    op_n_ptr[3] = (uint8_t)(op_n >> 24);
                    msg->data[0].len = 4 * sizeof(uint8_t);
                    msg->data[0].data = op_n_ptr;
                    return 0;
                }
            }
        }
        else if (msg->opcode == MESSAGE_T__OPCODE__OP_GET && msg->c_type == MESSAGE_T__C_TYPE__CT_KEY &&
                msg->n_data == 1) {
            char *key = (char *)msg->data[0].data;
            msg->opcode = MESSAGE_T__OPCODE__OP_GET + 1;
            msg->c_type = MESSAGE_T__C_TYPE__CT_VALUE;
            msg->n_data = 1;
            pthread_mutex_lock(&tree_mutex);
            struct data_t *data = tree_get(root, key);
            pthread_mutex_unlock(&tree_mutex);
            free(msg->data[0].data);
            free(msg->data);
            msg->data = malloc(sizeof(ProtobufCBinaryData));
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
            struct request_t *request = malloc(sizeof(struct request_t));
            if (value != NULL && request != NULL) {
                value->datasize = msg->data[1].len;
                value->data = msg->data[1].data;
                pthread_mutex_lock(&assign_mutex);
                request->op_n = last_assigned;
                last_assigned++;
                pthread_mutex_unlock(&assign_mutex);
                int op_n = request->op_n;
                request->op = 1;
                request->key = (char*)msg->data[0].data;
                request->data = value;
                request->next_in_line = NULL;
                pthread_mutex_lock(&queue_mutex);
                if (queue_head == NULL) {
                    queue_head = request;
                }
                else {
                    struct request_t *queue_request = queue_head;
                    while (queue_request->next_in_line != NULL) {
                        queue_request = queue_request->next_in_line;
                    }
                    queue_request->next_in_line = malloc(sizeof(struct request_t));
                    *queue_request->next_in_line = *request;
                    free(request);
                }
                pthread_cond_signal(&cond);
                pthread_mutex_unlock(&queue_mutex);
                free(msg->data);
                msg->opcode = MESSAGE_T__OPCODE__OP_PUT + 1;
                msg->c_type = MESSAGE_T__C_TYPE__CT_RESULT;
                msg->n_data = 1;
                uint8_t *op_n_ptr = malloc(4 * sizeof(uint8_t));
                msg->data = malloc(sizeof(ProtobufCBinaryData));
                if (msg->data != NULL && op_n_ptr != NULL) {
                    op_n_ptr[0] = (uint8_t)(op_n >> 0);
                    op_n_ptr[1] = (uint8_t)(op_n >> 8);
                    op_n_ptr[2] = (uint8_t)(op_n >> 16);
                    op_n_ptr[3] = (uint8_t)(op_n >> 24);
                    msg->data[0].len = 4 * sizeof(uint8_t);
                    msg->data[0].data = op_n_ptr;
                    return 0;
                }
            }
        }
        else if (msg->opcode == MESSAGE_T__OPCODE__OP_GETKEYS &&
                msg->c_type == MESSAGE_T__C_TYPE__CT_NONE) {
            pthread_mutex_lock(&tree_mutex);
            msg->n_data = tree_size(root);
            char **keys = tree_get_keys(root);
            pthread_mutex_unlock(&tree_mutex);
            msg->data = malloc(msg->n_data * sizeof(ProtobufCBinaryData));
            if (msg->data != NULL) {
                msg->opcode = MESSAGE_T__OPCODE__OP_GETKEYS + 1;
                msg->c_type = MESSAGE_T__C_TYPE__CT_KEYS;
                for (int i = 0; i < msg->n_data; i++) {
                    msg->data[i].len = (strlen(keys[i]) + 1) * sizeof(uint8_t);
                    msg->data[i].data = (uint8_t*)keys[i];
                }
                free(keys);
                return 0;
            }
        }
        else if (msg->opcode == MESSAGE_T__OPCODE__OP_GETVALUES &&
                msg->c_type == MESSAGE_T__C_TYPE__CT_NONE) {
            pthread_mutex_lock(&tree_mutex);
            msg->n_data = tree_size(root);
            void **values = tree_get_values(root);
            pthread_mutex_unlock(&tree_mutex);
            msg->data = malloc(msg->n_data * sizeof(ProtobufCBinaryData));
            if (msg->data != NULL) {
                msg->opcode = MESSAGE_T__OPCODE__OP_GETVALUES + 1;
                msg->c_type = MESSAGE_T__C_TYPE__CT_VALUES;
                for (int i = 0; i < msg->n_data; i++) {
                    struct data_t *value = (struct data_t *)values[i];
                    msg->data[i].len = value->datasize;
                    msg->data[i].data = value->data;
                    free(value);
                }
                free(values);
                return 0;
            }
        }
        else if (msg->opcode == MESSAGE_T__OPCODE__OP_VERIFY &&
                msg->c_type == MESSAGE_T__C_TYPE__CT_RESULT) {
            int result = verify(  (uint32_t)msg->data[0].data[0] <<  0
                                | (uint32_t)msg->data[0].data[1] <<  8
                                | (uint32_t)msg->data[0].data[2] << 16
                                | (uint32_t)msg->data[0].data[3] << 24);
            free(msg->data[0].data);
            free(msg->data);
            msg->opcode = MESSAGE_T__OPCODE__OP_VERIFY + 1;
            msg->c_type = MESSAGE_T__C_TYPE__CT_RESULT;
            msg->n_data = 1;
            uint8_t *result_ptr = malloc(4 * sizeof(uint8_t));
            msg->data = malloc(sizeof(ProtobufCBinaryData));
            if (msg->data != NULL && result_ptr != NULL) {
                result_ptr[0] = (uint8_t)(result >>  0);
                result_ptr[1] = (uint8_t)(result >>  8);
                result_ptr[2] = (uint8_t)(result >> 16);
                result_ptr[3] = (uint8_t)(result >> 24);
                msg->data[0].len = 4 * sizeof(uint8_t);
                msg->data[0].data = result_ptr;
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

int verify(int op_n) {
    if (op_n > 0 && op_n < last_assigned) {
        int found = 0;
        pthread_mutex_lock(&queue_mutex);
        struct request_t *request = queue_head;
        while (request != NULL && !found) {
            if (request->op_n == op_n) {
                found = 1;
            }
            request = request->next_in_line;
        }
        pthread_mutex_unlock(&queue_mutex);
        for (int i = 0; i < number_threads && !found; i++) {
            if (proc->in_progress[i] == op_n) {
                found = 1;
            }
        }
        if (found) {
            return -1;
        }
        else {
            return 0;
        }
    }
    return -1;
}