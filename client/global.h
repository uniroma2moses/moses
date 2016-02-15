/* 
 * File:   global.h
 * Author: stefano
 *
 * Created on May 19, 2010, 12:20 PM
 */

#ifndef _GLOBAL_H
#define	_GLOBAL_H

#ifdef	__cplusplus
extern "C" {
#endif




#ifdef	__cplusplus
}
#endif

#include <sys/socket.h>       /*  socket definitions        */
#include <sys/types.h>        /*  socket types              */
#include <arpa/inet.h>        /*  inet (3) funtions         */
#include <unistd.h>           /*  misc. UNIX functions      */

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <time.h>

#define BUFLEN 4096

#define DEFAULT_PROCESS_THREAD 256

struct user {
    char username[32];
    char password[32];
    char name[32];
    char surname[32];
};

struct process {
    char processname[32];
    char processclass[32];
};

struct sla {
    char processname[32];
    char processclass[32];
    struct tm *exp_date;
    float request_rate;
};
#endif	/* _GLOBAL_H */

