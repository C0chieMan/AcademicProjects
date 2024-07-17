//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#ifndef _CLIENT_STUB_PRIVATE_H
#define _CLIENT_STUB_PRIVATE_H

struct rtree_t {
    char *address;
    char *port;
    int sock;
    struct sockaddr_in *sock_address;
};

#endif
