//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#include <stdio.h>

#include "../include/network_server.h"

int main(int argc, char *argv[]) {
    if (argc == 2) {
        if (tree_skel_init() == 0) {
            short port;
            sscanf(argv[1], "%hd", &port);
            int sock = network_server_init(port);
            if (sock == -1) {
                printf("Error initializing server.\n");
            } else {
                int stop = 0;
                while (!stop) {
                    if(network_main_loop(sock) == -1) {
                        printf("Error while connected to client.\n");
                        break;
                    }
                }
                network_server_close();
            }
        }
        else {
            printf("Error initializing data tree.\n");
        }
    }
    else {
        printf("Wrong arguments.\nUse \"tree-server <port>\"\n");
    }
}