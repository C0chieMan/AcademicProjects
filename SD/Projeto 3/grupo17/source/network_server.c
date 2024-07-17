//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#include <arpa/inet.h>
#include <poll.h>
#include <signal.h>
#include <sys/socket.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "network_server.h"

struct sockaddr_in *address;
const int MAX_CONNECTIONS = 1024; //includes listening socket
const int MAX_MSG_SIZE = 256 * 1024; //256KB
int stop_server = -1;

void sigpipe_handler(int sig) {}

void sigint_handler() {
    stop_server = 1;
    printf("\nStopping server...\n");
}

int network_server_init(short port) {
    if (stop_server == -1) {
        stop_server = 0;
    }
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
            setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(int))
            || listen(sock, SOMAXCONN) == -1) {
        return -1;
    }
    else {
        signal(SIGPIPE, sigpipe_handler);
        return sock;
    }
}

int network_main_loop(int listening_socket) {
    struct pollfd *desc_set = malloc((MAX_CONNECTIONS) * sizeof(struct pollfd));
    if (desc_set == NULL) {
        return -1;
    }
    memset(desc_set, 0, MAX_CONNECTIONS * sizeof(struct pollfd));
    desc_set[0].fd = listening_socket;
    desc_set[0].events = POLLIN;
    int current_connections = 1;
    while (poll(desc_set, current_connections, 0) >= 0 && stop_server == 0) {
        if (desc_set[0].revents & POLLIN && current_connections < MAX_CONNECTIONS) {
            socklen_t client_size = sizeof(struct sockaddr);
            struct sockaddr *client_address = malloc(client_size);
            if (client_address == NULL) {
                return -1;
            }
            int client_sock = accept(listening_socket, client_address, &client_size);
            if (client_sock == -1) {
                return -1;
            }
            desc_set[current_connections].fd = client_sock;
            desc_set[current_connections].events = POLLIN;
            current_connections++;
            free(client_address);
        }
        for (int i = 1; i < current_connections; i++) {
            if (desc_set[i].revents & POLLIN) {
                MessageT *msg = network_receive(desc_set[i].fd);
                if (msg != NULL) {
                    invoke(msg);
                    network_send(desc_set[i].fd, msg);
                }
                else {
                    close(desc_set[i].fd);
                    for (int f = i; f < current_connections - 1; f++) {
                        desc_set[f].fd = desc_set[f + 1].fd;
                        desc_set[f].events = desc_set[f + 1].events;
                        desc_set[f].revents = desc_set[f + 1].revents;
                    }
                    desc_set[current_connections - 1].fd = 0;
                    desc_set[current_connections - 1].events = 0;
                    desc_set[current_connections - 1].revents = 0;
                    current_connections--;
                    i--; //without this one socket would be skipped
                }
            }
            if (desc_set[i].revents & (POLLHUP | POLLERR | POLLNVAL)) {
                close(desc_set[i].fd);
                for (int f = i; f < current_connections - 1; f++) {
                    desc_set[f].fd = desc_set[f + 1].fd;
                    desc_set[f].events = desc_set[f + 1].events;
                    desc_set[f].revents = desc_set[f + 1].revents;
                }
                desc_set[current_connections - 1].fd = 0;
                desc_set[current_connections - 1].events = 0;
                desc_set[current_connections - 1].revents = 0;
                current_connections--;
                i--; //without this one socket would be skipped
            }
        }
    }
    for (int i = 0; i < current_connections; i++) {
        close(desc_set->fd);
    }
    free(desc_set);
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
    message_t__free_unpacked(msg, NULL);
    return 0;
}

int network_server_close() {
    free(address);
    return 0;
}