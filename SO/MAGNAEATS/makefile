OBJECTS = main.o synchronization.o process.o memory.o restaurant.o driver.o client.o configuration.o log.o metime.o mesignal.o stats.o
CC = gcc

magnaeats: $(OBJECTS)
	$(CC) $(addprefix obj/,$(OBJECTS)) -o bin/magnaeats -lrt -lpthread
main.o: src/main.c
	$(CC) -I include -o obj/main.o -c src/main.c
synchronization.o: src/synchronization.c
	$(CC) -I include -o obj/synchronization.o -c src/synchronization.c
process.o: src/process.c
	$(CC) -I include -o obj/process.o -c src/process.c
memory.o: src/memory.c
	$(CC) -I include -o obj/memory.o -c src/memory.c
restaurant.o: src/restaurant.c
	$(CC) -I include -o obj/restaurant.o -c src/restaurant.c
driver.o: src/driver.c
	$(CC) -I include -o obj/driver.o -c src/driver.c
client.o: src/client.c
	$(CC) -I include -o obj/client.o -c src/client.c
configuration.o: src/configuration.c
	$(CC) -I include -o obj/configuration.o -c src/configuration.c
log.o: src/log.c
	$(CC) -I include -o obj/log.o -c src/log.c
metime.o: src/metime.c
	$(CC) -I include -o obj/metime.o -c src/metime.c
mesignal.o: src/mesignal.c
	$(CC) -I include -o obj/mesignal.o -c src/mesignal.c
stats.o: src/stats.c
	$(CC) -I include -o obj/stats.o -c src/stats.c
clean:
	rm -f bin/magnaeats
	rm -f obj/*.o
