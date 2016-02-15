/* 
 * File:   main.c
 * Author: stefano
 *
 * Created on May 19, 2010, 11:45 AM
 */

#ifndef _GLOBAL_H
#include "global.h"
#endif
#include "slamanager_client.h"
#include "process_client.h"
#include "my_rand.h"
#include <pthread.h>
#include <unistd.h>
#include <time.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#define CLASS_MU_K 1

static float lambdak[] = {0, 0, 0, 0};




pthread_mutex_t invoc_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t client_id_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t refused_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t first_request_mutex = PTHREAD_MUTEX_INITIALIZER;

int invocations = -1;
unsigned int client_id = 0;


char* process_address;
short process_port;
char* slamanager_address;
short slamanager_port;

struct req_gen_param {
    struct user *user;
    struct sla *sla;
    int class;
    time_t exp_ts;
    unsigned int client_id;
};

struct invoke_process_param {
    struct user *user;
    struct sla *sla;
    int fd;
    pthread_mutex_t *write_mutex;
};

void *process_invoker(void *param) {
    struct invoke_process_param *params = (struct invoke_process_param*) param;

    struct timeval *tv1, *tv2;
    tv1 = malloc(sizeof (struct timeval));
    tv2 = malloc(sizeof (struct timeval));
    gettimeofday(tv1, NULL);
    time_t t1 = time(NULL);
    printf("%u\t Invoking\n", t1);
    float invoke_cost = invoke_process(params->user, params->sla, process_address, process_port);
    gettimeofday(tv2, NULL);
    long millis1 = tv1->tv_sec * 1000 + tv1->tv_usec / 1000.;
    long millis2 = tv2->tv_sec * 1000 + tv2->tv_usec / 1000.;


    pthread_mutex_lock(&invoc_mutex);
    printf("---Process invoked, invocations total: %d---\n", ++invocations);
    pthread_mutex_unlock(&invoc_mutex);


    //Writing response time to logfile
    char line[1024];
    memset(line, 1024, 0);
    sprintf(line, "%u\t%s\t%u\t%f\n", t1, params->sla->processclass, (millis2 - millis1), invoke_cost);

    pthread_mutex_lock(params->write_mutex);
    if (write(params->fd, line, strlen(line)) == -1) {
        perror("Error writing logfile: ");
    }
    pthread_mutex_unlock(params->write_mutex);
    pthread_exit(NULL);
}

void *request_generator(void *param) {
    struct req_gen_param *params = (struct req_gen_param*) param;
    struct user *user = params->user;
    struct sla *sla = params->sla;
    int class = params->class;

    //Opening logfile
    char logfile[16];
    memset(logfile, 0, 16);
    sprintf(logfile, "%u.log", params->client_id);
    int fd = open(logfile, O_CREAT | O_WRONLY, 0644);
    if (fd == -1) {
        perror("Error creating logfile: ");
        exit(EXIT_FAILURE);
    }
    pthread_mutex_t *write_mutex = malloc(sizeof (pthread_mutex_t));
    pthread_mutex_init(write_mutex, NULL);
    struct invoke_process_param *invoke_process_param = calloc(1, sizeof (struct invoke_process_param));
    invoke_process_param->sla = sla;
    invoke_process_param->user = user;
    invoke_process_param->fd = fd;
    invoke_process_param->write_mutex = write_mutex;

    pthread_attr_t pthread_custom_attr;
    pthread_attr_init(&pthread_custom_attr);
    pthread_attr_setstacksize(&pthread_custom_attr, (size_t) 32768);
    pthread_t *req_threads = calloc(DEFAULT_PROCESS_THREAD, sizeof (pthread_t));
    int i = 0;
    int max_thread = DEFAULT_PROCESS_THREAD;
    time_t end_time = params->exp_ts;
    while (time(NULL) < end_time) {
        //float iatm = my_expntl(class, 1. / sla->request_rate);
	float iatm = 1. / sla->request_rate;
        usleep(iatm * 1000 * 1000);
        printf("Sending a request of class: %s\n", sla->processclass);
        if (pthread_create(&(req_threads[i++]), &pthread_custom_attr, process_invoker, (void*) invoke_process_param) != 0) {
            perror("Error creating new thread: ");
            exit(EXIT_FAILURE);
        }
        if (i == max_thread - 1) {
            req_threads = realloc(req_threads, (max_thread + DEFAULT_PROCESS_THREAD) * sizeof (pthread_t));
            if (req_threads == NULL) {
                fprintf(stderr, "Error allocating more space for threads");
                exit(EXIT_FAILURE);
            } else {
                max_thread += DEFAULT_PROCESS_THREAD;
            }
        }
    }
    
    delete_sla(user, sla, slamanager_address, slamanager_port);
    printf("---SLA deleted---\n");

    int j;
    for (j = 0; j < i; j++) {
        pthread_join(req_threads[j], NULL);
    }
    close(fd);
    free(params->sla);
    free(params->user);
    free(params);
    free(req_threads);
    pthread_exit(NULL);

}

void *contract_generator(void *arg) {
    int *class = (int*) arg;
    int class_index = (*class) - 1;
    time_t now = time(NULL);
    time_t end = now + 18000;
	
    struct timeval *ov1, *ov2;
    ov1 = malloc(sizeof (struct timeval));
    ov2 = malloc(sizeof (struct timeval));

    if (lambdak[class_index]!=0) {

        //User creation
	time_t curtime = time(NULL);
	time_t expire_date = curtime + 18000;  
      	struct user *user;
        user = malloc(sizeof (struct user));

        do {
            sprintf(user->name, "%f", my_uniform(10));
           sprintf(user->password, "%f", my_uniform(10));
            sprintf(user->surname, "%f", my_uniform(10));
            sprintf(user->username, "%f", my_uniform(10));
        } while (!add_user(user, slamanager_address, slamanager_port));
        //Contract creation
        struct sla *sla;
        sla = malloc(sizeof (struct sla));
        sprintf(sla->processclass, "%d", *class);
        strcpy(sla->processname, "ESECProcess");
        sla->exp_date = gmtime(&expire_date);
        sla->request_rate = lambdak[class_index] * 100;
	fprintf(stderr,"sla->request_rate=%f\n",sla->request_rate);
        //Registering contract
        short sla_ok = -1;
	
/*Modifica per escludere lo slamanager */
	fprintf(stderr,"log: creo lo SLA");
        sla_ok = create_sla(user, sla, slamanager_address, slamanager_port);
	fprintf(stderr,"log: ricevuta risposta di SLA");
        if (sla_ok) {
            pthread_mutex_lock(&client_id_mutex);
            unsigned int my_client_id = ++client_id;
            pthread_mutex_unlock(&client_id_mutex);
            struct req_gen_param *param;
            param = malloc(sizeof (struct req_gen_param));
            param->exp_ts = expire_date;
            param->class = *class;
            param->sla = sla;
            param->user = user;
            param->client_id = my_client_id;
            request_generator( (void*) param);
        } else {
            pthread_mutex_lock(&refused_mutex);
            int fd = open("refused_agreements", O_CREAT | O_WRONLY | O_APPEND, 0644);
            char crefused[4];
            memset(crefused, 0, 4);
            sprintf(crefused, "%s\n", sla->processclass);
            if (write(fd, crefused, strlen(crefused)) == -1) {
                perror("Error writing refused sla count: ");
            }
            if (sla == -1) {
                memset(crefused, 0, 4);
                sprintf(crefused, " %d\n", sla_ok);
                if (write(fd, crefused, strlen(crefused)) == -1) {
                    perror("Error writing refused sla count: ");
                }
            }
            close(fd);
            pthread_mutex_unlock(&refused_mutex);
        }
	gettimeofday(ov2, NULL);
    }

    return NULL;

}

int main(int argc, char** argv) {



    /*
        struct user user;
        strcpy(user.username, "chmod");
        strcpy(user.name, "chmod");
        strcpy(user.surname, "chmod");
        strcpy(user.password, "chmod");
     */

    //First argument: process address

    if (argv[1]==NULL || argv[2]==NULL || argv[3]==NULL ||
	argv[4]==NULL || argv[5]==NULL || argv[6]==NULL ||
	argv[7]==NULL || argv[8]==NULL) {
	fprintf(stderr,"Usage: ./client <BPEL Process Address> <BPEL Process Port> <SLAManager Address> <SLAManager Port> <Class 1 rate> <Class 2 rate> <Class 3 rate> <Class 4 rate>\n");
	exit(1);
	}
    process_address = malloc(strlen(argv[1]) + 1);
    strcpy(process_address, argv[1]);

    //Second argument: process port
    process_port = (short) atoi(argv[2]);

    //Third argument: slamanager address
    slamanager_address = malloc(strlen(argv[3]) + 1);
    strcpy(slamanager_address, argv[3]);

    //Fourth argument: slamanager port
    slamanager_port = (short) atoi(argv[4]);

    /*
        printf("Creo utente\n");
        add_user(&user, slamanager_address, slamanager_port);
        printf("Utente creato\n");
     */

	lambdak[0]=atof(argv[5]);
        lambdak[1]=atof(argv[6]);
        lambdak[2]=atof(argv[7]);
        lambdak[3]=atof(argv[8]);

printf("lambdak[%d]=%f\n",0,lambdak[0]);
    pthread_t contract_generators[4];
    pthread_attr_t pthread_custom_attr;
    pthread_attr_init(&pthread_custom_attr);
    pthread_attr_setstacksize(&pthread_custom_attr, 4*1024*1024);

    int i;
    int class_number[4] = {1, 2, 3, 4};
//pthread_create(&contract_generators[0], &pthread_custom_attr, contract_generator, (void*) & class_number[3]);
    for (i = 0; i < 4; i++) {
        pthread_create(&contract_generators[i], &pthread_custom_attr, contract_generator, (void*) & class_number[i]);
        size_t *size = malloc(sizeof (size_t));
        pthread_attr_getstacksize(&pthread_custom_attr, size);
    }

    for (i = 0; i < 4; i++) {
        pthread_join(contract_generators[i], NULL);
    }


    return (EXIT_SUCCESS);
}


