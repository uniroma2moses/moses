#include "global.h"
#include "http_client.h"
#include "process_client.h"


char* build_process_http_request(char* address, short port, char* soap_post) {
    //Building HTTP Message
    char str_port[6];
    memset(str_port, 0, 6);
    sprintf(str_port, "%d", port);

    unsigned int soap_length = strlen(soap_post);
    char str_soap_length[12];
    memset(str_soap_length, 0, 12);
    sprintf(str_soap_length, "%u", soap_length);

    unsigned int req_length = strlen(PROCESS_HTTP_REQUEST_PART1)
    + strlen(str_soap_length) + strlen(PROCESS_HTTP_REQUEST_PART2)
    + strlen(address) + 1 + strlen(str_port) + strlen(PROCESS_HTTP_REQUEST_PART3)
    +soap_length + 1;

    char* request = malloc(req_length);
    strcpy(request, PROCESS_HTTP_REQUEST_PART1);
    strcat(request, str_soap_length);
    strcat(request, PROCESS_HTTP_REQUEST_PART2);
    strcat(request, address);
    strcat(request, ":");
    strcat(request, str_port);
    strcat(request, PROCESS_HTTP_REQUEST_PART3);
    strcat(request, soap_post);

    //HTTP message built
    return request;
}

float invoke_process(struct user *user, struct sla *sla, char* address, short port) {

    unsigned int soap_post_length = strlen(PROCESS_SOAP_MSG_PART1)
    + strlen(sla->processclass) + strlen(PROCESS_SOAP_MSG_PART2)
    +strlen(user->username) + strlen(PROCESS_SOAP_MSG_PART3) + strlen(user->password)
    +strlen(PROCESS_SOAP_MSG_PART4) + 1;

    char* soap_post = malloc(soap_post_length);
    strcpy(soap_post, PROCESS_SOAP_MSG_PART1);
    strcat(soap_post, sla->processclass);
    strcat(soap_post, PROCESS_SOAP_MSG_PART2);
    strcat(soap_post, user->username);
    strcat(soap_post, PROCESS_SOAP_MSG_PART3);
    strcat(soap_post, user->password);
    strcat(soap_post, PROCESS_SOAP_MSG_PART4);

    char* request = build_process_http_request(address, port, soap_post);


    char* resp_message = http_client(request, address, port);

    free(request);
    free(soap_post);

    float cost = -1;
    if (strstr(resp_message, PROCESS_HTTP_RESPONSE_PART1)!=NULL && strstr(resp_message, PROCESS_HTTP_RESPONSE_PART2)!=NULL) {
        char* cost_begin = strstr(resp_message, PROCESS_HTTP_RESPONSE_PART1) + strlen(PROCESS_HTTP_RESPONSE_PART1);
        char* cost_end = strstr(resp_message,PROCESS_HTTP_RESPONSE_PART2);
        char* scost = calloc(cost_end-cost_begin+1,sizeof(char));
        strncpy(scost,cost_begin,cost_end-cost_begin);
        printf("Costo: %s\n",scost);
        cost = atof(scost);
        free(resp_message);
        free(scost);
        return cost;
    }
    else if (strstr(resp_message,"No usable services for this solution")!=NULL) {
        //printf("%s\n",resp_message);
        free(resp_message);
        return -1;
    }
    else {
        printf("%s\n",resp_message);
        free(resp_message);
        return -2;
    }
};