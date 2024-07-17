CC = gcc

all: test_tree test_entry test_data

test_tree: data.o entry.o tree.o tree-private.o test_tree.o
	$(CC) object/data.o object/entry.o object/tree.o object/tree-private.o object/test_tree.o -o binary/test_tree -lrt -lpthread
test_entry: data.o entry.o test_entry.o
	$(CC) object/data.o object/entry.o object/test_entry.o -o binary/test_entry -lrt -lpthread
test_data: data.o test_data.o
	$(CC) object/data.o object/test_data.o -o binary/test_data -lrt -lpthread

test_tree.o : source/test_tree.c
	$(CC) -I include -o object/test_tree.o -c source/test_tree.c
test_entry.o : source/test_entry.c
	$(CC) -I include -o object/test_entry.o -c source/test_entry.c
test_data.o : source/test_data.c
	$(CC) -I include -o object/test_data.o -c source/test_data.c
serialization.o : source/serialization.c
	$(CC) -I include -o object/serialization.o -c source/serialization.c
tree-private.o : source/tree-private.c
	$(CC) -I include -o object/tree-private.o -c source/tree-private.c
tree.o : source/tree.c
	$(CC) -I include -o object/tree.o -c source/tree.c
entry.o : source/entry.c
	$(CC) -I include -o object/entry.o -c source/entry.c
data.o : source/data.c
	$(CC) -I include -o object/data.o -c source/data.c

clean:
	rm -f binary/*
	rm -f object/*.o
