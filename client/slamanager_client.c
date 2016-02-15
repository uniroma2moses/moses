#include "global.h"
#include "http_client.h"
#include "slamanager_client.h"

char* build_slamanager_http_request(char* address, short port, char* soap_post) {
    //Building HTTP Message
    char str_port[6];
    memset(str_port, 0, 6);
    sprintf(str_port, "%d", port);

    unsigned int soap_length = strlen(soap_post);
    char str_soap_length[12];
    memset(str_soap_length, 0, 12);
    sprintf(str_soap_length, "%u", soap_length);

    unsigned int req_length = strlen(SLAMANAGER_HTTP_REQUEST_PART1)
            + strlen(address) + 1 + strlen(str_port) + 1
            + strlen(SLAMANAGER_HTTP_REQUEST_PART2) + strlen(str_soap_length)
            + strlen(SLAMANAGER_HTTP_REQUEST_PART3) + strlen(soap_post) + 1;


    char* request = malloc(req_length);
    strcpy(request, SLAMANAGER_HTTP_REQUEST_PART1);
    strcat(request, address);
    strcat(request, ":");
    strcat(request, str_port);
    strcat(request, "\n");
    strcat(request, SLAMANAGER_HTTP_REQUEST_PART2);
    strcat(request, str_soap_length);
    strcat(request, SLAMANAGER_HTTP_REQUEST_PART3);
    strcat(request, soap_post);

    //HTTP message built

    return request;
}

/**
 * This function adds a user calling the SLAManager addUser web service
 * Returns 1 in case of success, 0 otherwise
 */
short add_user(struct user *user, char* address, short port) {

    //Building SOAP message

    char* soap_post = malloc(strlen(ADDUSER_SOAP_MSG_PART1) + strlen(user->surname)
            + strlen(ADDUSER_SOAP_MSG_PART2) + strlen(user->name) + strlen(ADDUSER_SOAP_MSG_PART3)
            + strlen(user->username) + strlen(ADDUSER_SOAP_MSG_PART4) + strlen(user->password)
            + strlen(ADDUSER_SOAP_MSG_PART5) + 1);

    strcpy(soap_post, ADDUSER_SOAP_MSG_PART1);
    strcat(soap_post, user->username);
    strcat(soap_post, ADDUSER_SOAP_MSG_PART2);
    strcat(soap_post, user->name);
    strcat(soap_post, ADDUSER_SOAP_MSG_PART3);
    strcat(soap_post, user->surname);
    strcat(soap_post, ADDUSER_SOAP_MSG_PART4);
    strcat(soap_post, user->password);
    strcat(soap_post, ADDUSER_SOAP_MSG_PART5);

    //SOAP Message built

    char* request = build_slamanager_http_request(address, port, soap_post);

    //Invoking web service
    char *resp_message;
    resp_message = http_client(request, address, port);
    //Comparing response with the expected one
    char* expected_response = malloc(
            strlen(ADDUSER_SOAP_RESP_PART1) + strlen(user->name) + strlen(ADDUSER_SOAP_RESP_PART2)
            + strlen(user->password) + strlen(ADDUSER_SOAP_RESP_PART3) + strlen(user->surname)
            + strlen(ADDUSER_SOAP_RESP_PART4) + strlen(user->username)
            + strlen(ADDUSER_SOAP_RESP_PART5) + 1);
    strcpy(expected_response, ADDUSER_SOAP_RESP_PART1);
    strcat(expected_response, user->name);
    strcat(expected_response, ADDUSER_SOAP_RESP_PART2);
    strcat(expected_response, user->password);
    strcat(expected_response, ADDUSER_SOAP_RESP_PART3);
    strcat(expected_response, user->surname);
    strcat(expected_response, ADDUSER_SOAP_RESP_PART4);
    strcat(expected_response, user->username);
    strcat(expected_response, ADDUSER_SOAP_RESP_PART5);

    free(request);
    free(soap_post);
    if (strstr(resp_message, expected_response) != NULL) {
        free(resp_message);
        return 1;
    }
    else {
        fprintf(stderr, "Errore nella creazione dell'utente. Messaggio di risposta:\n%s", resp_message);
        free(resp_message);
        return 0;
    }
}

/**
 * This function creates a new sla, given a user and a sla that is composed by a
 * process name, a process class and an expiration date.
 * It returns 1 in case of successful sla creation, 0 in case of unsuccessful creation;
 * -1 in case of errors.
 */
short create_sla(struct user *user, struct sla *sla, char* address, short port) {

    //Building SOAP message

    char exp_day[3];
    char exp_month[3];
    char exp_year[5];
    char exp_second[3];
    char exp_minute[3];
    char exp_hour[3];
    memset(exp_day, 0, 3);
    memset(exp_month, 0, 3);
    memset(exp_year, 0, 5);
    memset(exp_second, 0, 3);
    memset(exp_minute, 0, 3);
    memset(exp_hour, 0, 3);

    char rate[16];
    memset(rate,0,16);

    sprintf(exp_day, "%d", sla->exp_date->tm_mday);

    sprintf(exp_month, "%d", sla->exp_date->tm_mon);

    if (strlen(exp_day) == 1) {
        exp_day[1]=exp_day[0];
        exp_day[0]='0';
    }
    if (strlen(exp_month) == 1) {
        exp_month[1]=exp_month[0];
        exp_month[0]='0';
    }
    sprintf(exp_year, "%d",(sla->exp_date->tm_year + 1900) );
    sprintf(exp_second, "%d", sla->exp_date->tm_sec);
    sprintf(exp_minute, "%d", sla->exp_date->tm_min);
    sprintf(exp_hour, "%d", sla->exp_date->tm_hour);

    if (strlen(exp_second) == 1) {
        exp_second[1] = exp_second[0];
        exp_second[0] = '0';
    }
    if (strlen(exp_minute) == 1) {
        exp_minute[1] = exp_minute[0];
        exp_minute[0] = '0';
    }
    if (strlen(exp_hour) == 1) {
        exp_hour[1] = exp_hour[0];
        exp_hour[0] = '0';
    }

    sprintf(rate,"%f",sla->request_rate);


    unsigned int soap_post_length = strlen(ADDSLA_SOAP_MSG_PART1)
            + strlen(ADDSLA_SOAP_MSG_PART2) + strlen(ADDSLA_SOAP_MSG_PART3)
            + strlen(ADDSLA_SOAP_MSG_PART4) + strlen(ADDSLA_SOAP_MSG_PART5)
            + strlen(ADDSLA_SOAP_MSG_PART6) + strlen(ADDSLA_SOAP_MSG_PART7)
            + strlen(sla->processname) + strlen(sla->processclass)
            + strlen(user->username) + strlen(user->password) + strlen(rate) + DATE_LENGTH + 1;
    char* soap_post = malloc(soap_post_length);
    strcpy(soap_post, ADDSLA_SOAP_MSG_PART1);
    strcat(soap_post, sla->processname);
    strcat(soap_post, ADDSLA_SOAP_MSG_PART2);
    strcat(soap_post, sla->processclass);
    strcat(soap_post, ADDSLA_SOAP_MSG_PART3);
    strcat(soap_post, user->username);
    strcat(soap_post, ADDSLA_SOAP_MSG_PART4);
    strcat(soap_post, user->password);
    strcat(soap_post, ADDSLA_SOAP_MSG_PART5);
    strcat(soap_post, rate);
    strcat(soap_post, ADDSLA_SOAP_MSG_PART6);
    strcat(soap_post, exp_year);
    strcat(soap_post, "-");
    strcat(soap_post, exp_month);
    strcat(soap_post, "-");
    strcat(soap_post, exp_day);
    strcat(soap_post, "T");
    strcat(soap_post, exp_hour);
    strcat(soap_post, ":");
    strcat(soap_post, exp_minute);
    strcat(soap_post, ":");
    strcat(soap_post, exp_second);
    strcat(soap_post, ADDSLA_SOAP_MSG_PART7);

    //SOAP message built

    char* request = build_slamanager_http_request(address, port, soap_post);
    char* resp_message = http_client(request, address, port);

    free(request);
    free(soap_post);
    if (strstr(resp_message, ADDSLA_SOAP_RESP_TRUE) != NULL) {
	printf("---Richiesta di SLA accettata---\n");
        free(resp_message);
	fprintf(stderr,"!!!!!!!Dopo la free!!!!!!!!\n");
        return 1;
    } else if (strstr(resp_message, ADDSLA_SOAP_RESP_FALSE) != NULL) {
        printf("---Richiesta di SLA non accettata---\n");
	printf("%s\n",resp_message);
        free(resp_message);
        return 0;
    } else {
        fprintf(stderr, "Errore nella creazione del contratto:\n%s", resp_message);
        free(resp_message);
        return -1;
    }
}

short delete_sla(struct user *user, struct sla *sla, char* address, short port) {
    unsigned int soap_post_length = strlen(DELSLA_SOAP_MSG_PART1)
            + strlen(sla->processname) + strlen(DELSLA_SOAP_MSG_PART2)
            + strlen(sla->processclass) + strlen(DELSLA_SOAP_MSG_PART3)
            + strlen(user->username) + strlen(DELSLA_SOAP_MSG_PART4)
            + strlen(user->password) + strlen(DELSLA_SOAP_MSG_PART5) + 1;

    char* soap_post = malloc(soap_post_length);
    strcpy(soap_post, DELSLA_SOAP_MSG_PART1);
    strcat(soap_post, sla->processname);
    strcat(soap_post, DELSLA_SOAP_MSG_PART2);
    strcat(soap_post, sla->processclass);
    strcat(soap_post, DELSLA_SOAP_MSG_PART3);
    strcat(soap_post, user->username);
    strcat(soap_post, DELSLA_SOAP_MSG_PART4);
    strcat(soap_post, user->password);
    strcat(soap_post, DELSLA_SOAP_MSG_PART5);

    char* request = build_slamanager_http_request(address, port, soap_post);
    char* resp_message = http_client(request, address, port);


    short ret = -1;
    if (strstr(resp_message,DELSLA_SOAP_MSG_TRUE) != NULL)
        ret = 1;
    else if(strstr(resp_message,DELSLA_SOAP_MSG_FALSE) != NULL)
        ret = 0;
    else
        ret = -1;
    
    free(request);
    free(soap_post);
    free(resp_message);

    return ret;
};
