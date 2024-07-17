CC = gcc
link = ld

all: tree-client tree-server

tree-client: client-lib.o tree_client.o
	$(CC) lib/client-lib.o object/tree_client.o -o binary/tree-client -lrt -lpthread -lprotobuf-c
tree-server: data.o entry.o network_server.o sdmessage.pb-c.o tree_server.o tree_skel.o
	$(CC) object/data.o object/entry.o object/network_server.o object/sdmessage.pb-c.o object/tree.o object/tree_server.o object/tree_skel.o -o binary/tree-server -lrt -lpthread -lprotobuf-c

client-lib.o: client_stub.o data.o entry.o network_client.o sdmessage.pb-c.o
	$(link) -g -r object/client_stub.o object/data.o object/entry.o object/network_client.o object/sdmessage.pb-c.o -o lib/client-lib.o

client_stub.o: source/client_stub.c
	$(CC) -g -I include -o object/client_stub.o -c source/client_stub.c
data.o: source/data.c
	$(CC) -g -I include -o object/data.o -c source/data.c
entry.o: source/entry.c
	$(CC) -g -I include -o object/entry.o -c source/entry.c
network_client.o: source/network_client.c
	$(CC) -g -I include -o object/network_client.o -c source/network_client.c
network_server.o: source/network_server.c
	$(CC) -g -I include -o object/network_server.o -c source/network_server.c
sdmessage.pb-c.o: source/sdmessage.pb-c.c
	$(CC) -g -I include -o object/sdmessage.pb-c.o -c source/sdmessage.pb-c.c
tree_client.o: source/tree_client.c
	$(CC) -g -I include -o object/tree_client.o -c source/tree_client.c
tree_server.o: source/tree_server.c
	$(CC) -g -I include -o object/tree_server.o -c source/tree_server.c
tree_skel.o: source/tree_skel.c
	$(CC) -g -I include -o object/tree_skel.o -c source/tree_skel.c

clean:
	rm -f binary/*
	rm -f lib/*
	rm -f object/client_stub.o
	rm -f object/data.o
	rm -f object/entry.o
	rm -f object/network_client.o
	rm -f object/network_server.o
	rm -f object/sdmessage.pb-c.o
	rm -f object/tree_client.o
	rm -f object/tree_server.o
	rm -f object/tree_skel.o
