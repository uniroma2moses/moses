#!/bin/bash
rm -rfv build
mkdir build
gcc -lpthread -lm   -c -o build/slamanager_client.o slamanager_client.c
gcc -lpthread -lm   -c -o build/my_rand.o my_rand.c
gcc -lpthread -lm   -c -o build/process_client.o process_client.c
gcc -lpthread -lm   -c -o build/http_client.o http_client.c
gcc -lpthread -lm   -c -o build/main.o main.c
gcc -Wall -o build/client build/*.o -lm -lpthread
