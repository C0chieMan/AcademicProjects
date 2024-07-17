//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "../include/client_stub.h"
#include "../include/client_stub-private.h"

int main(int argc, char *argv[]) {
    if (argc == 2) {
        struct rtree_t *rtree = rtree_connect(argv[1]);
        if (rtree == NULL) {
            printf("Error connecting to server.\n");
            exit(-1);
        }
        printf("Menu:\n"
               "put <key> <value>\n"
               "get <key>\n"
               "del <key>\n"
               "size\n"
               "height\n"
               "getkeys\n"
               "getvalues\n"
               "quit\n");
        char *command = malloc(100 * sizeof(char));
        if (command == NULL) {
            printf("Error initializing client.\n");
            exit(-1);
        }
        char *token = NULL;
        int quit = 0;
        while (!quit) {
            memset(command, '\0', 100 * sizeof(char));
            printf("\nCommand:\n");
            read(0, command, 100);
            token = strtok(command, " \n");
            if (strcmp(token, "put") == 0) {
                char *key = strtok(NULL, " \n");
                if (key != NULL) {
                    char *value = strtok(NULL, "\n");
                    if (value != NULL) {
                        struct data_t *data = data_create2(sizeof(value), value);
                        struct entry_t *entry = entry_create(key, data);
                        if (rtree_put(rtree, entry) == 0) {
                            printf("Entry put successfully.\n");
                        }
                        else {
                            printf("Entry not put.\n");
                        }
                    }
                    else {
                        printf("No value given to put.\n");
                    }
                }
                else {
                    printf("No key given to put.\n");
                }
            }
            else if (strcmp(token, "get") == 0) {
                char *key = strtok(NULL, " \n");
                if (key != NULL) {
                    struct data_t *data = rtree_get(rtree, key);
                    if (data != NULL && data->data != NULL) {
                        printf("%s\n", (char*)data->data);
                        free(data);
                    }
                    else if (data != NULL) {
                        printf("No value found.\n");
                    }
                    else {
                        printf("Error getting value.\n");
                    }
                }
                else {
                    printf("No key given to get.\n");
                }
            }
            else if (strcmp(token, "del") == 0) {
                char *key = strtok(NULL, " \n");
                if (key != NULL) {
                    if (rtree_del(rtree, key) == 0) {
                        printf("Entry deleted successfully.\n");
                    }
                    else {
                        printf("Entry not deleted.\n");
                    }
                }
                else {
                    printf("No key given to delete.\n");
                }
            }
            else if (strcmp(token, "size") == 0) {
                int size = rtree_size(rtree);
                if (size == -1) {
                    printf("Error getting size.");
                }
                else {
                    printf("Tree has size %i.\n", size);
                }
            }
            else if (strcmp(token, "height") == 0) {
                int height = rtree_height(rtree);
                if (height == -1) {
                    printf("Error getting height.");
                }
                else {
                    printf("Tree has height %i.\n", height);
                }
            }
            else if (strcmp(token, "getkeys") == 0) {
                char** keys = rtree_get_keys(rtree);
                int i = 0;
                if (keys != NULL && keys[0] != NULL) {
                    printf("Keys:\n");
                    while (keys[i] != NULL) {
                        printf("%s\n", keys[i]);
                        i++;
                    }
                }
                else {
                    printf("Tree has no entries.\n");
                }
            }
            else if (strcmp(token, "getvalues") == 0) {
                void** values = rtree_get_values(rtree);
                if (values != NULL && values[0] != NULL) {
                    printf("Values:\n");
                    int i = 0;
                    while (values[i] != NULL) {
                        printf("%s\n", (char*)values[i]);
                        i++;
                    }
                }
                else {
                    printf("Tree has no entries.\n");
                }
            }
            else if (strcmp(token, "quit") == 0) {
                quit = 1;
            }
            else {
                printf("Unknown command, please try again.\n");
            }
        }
        printf("Quitting...\n");
        rtree_disconnect(rtree);
        free(command);
    }
    else {
        printf("Wrong arguments.\nUse \"tree-client <server_address>:<port>\"\n");
    }
}