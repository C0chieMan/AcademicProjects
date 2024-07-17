//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#include <arpa/inet.h>
#include <signal.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <unistd.h>

#include "../include/network_server.h"

struct sockaddr_in *address;
const int MAX_MSG_SIZE = 256 * 1024; //256KB
int stop;

void sigpipe_handler(int sig) {
    stop = 1;
}

int network_server_init(short port) {
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    address = malloc(sizeof(struct sockaddr_in));
    if (address == NULL) {
        return -1;
    }
    address->sin_family = AF_INET;
    address->sin_port = htons(port);
    address->sin_addr.s_addr = inet_addr("127.0.0.1");
    int optval = 1;
    if (sock == -1 || bind(sock, (struct sockaddr*)address, sizeof(struct sockaddr_in)) == -1 ||
            setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(int))) {
        return -1;
    }
    else {
        signal(SIGPIPE, sigpipe_handler);
        return sock;
    }
}

int network_main_loop(int listening_socket) {
    stop = 0;
    if (listen(listening_socket, 1) == -1) {
        return -1;
    }
    socklen_t client_size = sizeof(struct sockaddr);
    struct sockaddr *client_address = malloc(client_size);
    if (client_address == NULL) {
        return -1;
    }
    int client_sock = accept(listening_socket, client_address,
            &client_size);
    if (client_sock == -1) {
        return -1;
    }
    stop = 0;
    while (!stop) {
        MessageT *msg = network_receive(client_sock);
        if (msg == NULL) {
            free(client_address);
            return -1;
        }
        invoke(msg);
        network_send(client_sock, msg);
    }
    free(client_address);
    return 0;
}

MessageT *network_receive(int client_socket) {
    uint8_t buffer[MAX_MSG_SIZE];
    int size = recv(client_socket, buffer, sizeof(buffer), 0);
    if (size == -1) {
        return NULL;
    }
    MessageT *msg = message_t__unpack(NULL, size, buffer);
    return msg;
}

int network_send(int client_socket, MessageT *msg) {
    int packed_size = message_t__get_packed_size(msg);
    uint8_t buffer[packed_size];
    message_t__pack(msg, buffer);
    send(client_socket, buffer, packed_size, 0);
    return 0;
}

int network_server_close() {
    free(address);
    return 0;
}