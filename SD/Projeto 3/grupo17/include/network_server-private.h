//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#ifndef _NETWORK_SERVER_PRIVATE_H
#define _NETWORK_SERVER_PRIVATE_H

//handles interruption signals
//ensures all memory is freed, sockets are closed and threads are terminated
//before the server is shut down
void sigint_handler();

#endif
