//Grupo: 17
//Alunos:
//  Tiago Fernandes - 55246
//  Henrique Catarino - 56278
//  Miguel Nunes - 56338

#ifndef _TREE_SKEL_PRIVATE_H
#define _TREE_SKEL_PRIVATE_H

struct op_proc {
    int max_proc;
    int *in_progress;
};

struct request_t {
    int op_n;            //o número da operação
    int op;              //a operação a executar. op=0 se for um delete, op=1 se for um put
    char *key;           //a chave a remover ou adicionar
    struct data_t *data; // os dados a adicionar em caso de put, ou NULL em caso de delete
    struct request_t *next_in_line;
};

#endif
