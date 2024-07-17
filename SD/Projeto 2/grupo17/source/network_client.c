//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#include <arpa/inet.h>
#include <netdb.h>
#include <signal.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "../include/client_stub-private.h"
#include "../include/network_client.h"

const int MAX_MSG_SIZE = 256 * 1024; //256KB

void sigpipe_handler(int sig) {}

int network_connect(struct rtree_t *rtree) {
    rtree->sock = socket(AF_INET, SOCK_STREAM, 0);
    struct addrinfo **info = malloc(sizeof(struct addrinfo **));
    if (info == NULL) {
        return -1;
    }
    getaddrinfo(rtree->address, rtree->port, NULL, info);
    rtree->sock_address = malloc(sizeof(struct sockaddr_in));
    if (info[0] != NULL && info[0]->ai_addr != NULL && rtree->sock_address != NULL &&
            rtree->sock != -1) {
        memcpy(rtree->sock_address, info[0]->ai_addr, sizeof(struct sockaddr));
        int optval = 1;
        if (connect(rtree->sock, (struct sockaddr*)rtree->sock_address, sizeof(struct sockaddr))
                == -1 || setsockopt(rtree->sock,SOL_SOCKET,SO_REUSEADDR,&optval,
               sizeof(int)) == -1) {
            return -1;
        }
        signal(SIGPIPE, sigpipe_handler);
        return 0;
    }
    else {
        return -1;
    }
}

MessageT *network_send_receive(struct rtree_t * rtree, MessageT *msg) {
    int packed_size = message_t__get_packed_size(msg);
    uint8_t buffer[packed_size];
    message_t__pack(msg, buffer);
    if (send(rtree->sock, buffer, packed_size, 0) == -1) {
        return NULL;
    }
    uint8_t buffer2[MAX_MSG_SIZE];
    int size = recv(rtree->sock, buffer2, sizeof(buffer2), 0);
    if (size == -1) {
        return NULL;
    }
    msg = message_t__unpack(NULL, size, buffer2);
    return msg;
}

int network_close(struct rtree_t * rtree) {
    return close(rtree->sock);
}