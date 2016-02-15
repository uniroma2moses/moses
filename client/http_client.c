#include "global.h"
#include "http_client.h"

char* http_client(char* req_message, char* address, short port) {
    int conn_s; /*  connection socket         */
    struct sockaddr_in servaddr; /*  socket address structure  */


    if ((conn_s = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("Error creating socket: ");
        exit(EXIT_FAILURE);
    }

    /*  Set all bytes in socket address structure to
            zero, and fill in the relevant data members   */
    memset(&servaddr, 0, sizeof (servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(port);

    if (inet_aton(address, &servaddr.sin_addr) <= 0) {
        fprintf(stderr, "Invalid remote IP address.\n");
        exit(EXIT_FAILURE);
    }

    /*  connect() to the remote server  */
    if (connect(conn_s, (struct sockaddr *) & servaddr, sizeof (servaddr)) < 0) {
        fprintf(stderr, "Error calling connect()\n");
        exit(EXIT_FAILURE);
    }

    if (write(conn_s, req_message, strlen(req_message)) != strlen(req_message)) {
        fprintf(stderr, "Error sending request message.\n");
        exit(EXIT_FAILURE);
    }

    char buf[BUFLEN];
    memset(buf, 0, BUFLEN);
    char* resp_message = NULL;
    while (read(conn_s, buf, BUFLEN) > 0) {
        if (resp_message == NULL) {
            resp_message = calloc(BUFLEN, sizeof (char));
            strcpy(resp_message,buf);
        }
        else {
            resp_message = realloc(resp_message, strlen(resp_message) + BUFLEN);
            strcat(resp_message, buf);
        }
        memset(buf,0,BUFLEN);
    }
    close(conn_s);
    return resp_message;
}
