/* 
 * File:   slamanager_client.h
 * Author: stefano
 *
 * Created on May 19, 2010, 6:17 PM
 */

#ifndef _SLAMANAGER_CLIENT_H
#define	_SLAMANAGER_CLIENT_H

#ifdef	__cplusplus
extern "C" {
#endif




#ifdef	__cplusplus
}
#endif

#define SLAMANAGER_HTTP_REQUEST_PART1 "POST /SLAManagerService/SLAManager HTTP/1.1\nContent-type: text/xml;charset=\"utf-8\"\nSoapaction: \"\"\nAccept: text/xml, multipart/related, text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2\nUser-Agent: JAX-WS RI 2.1.6 in JDK 6\nHost: "
#define SLAMANAGER_HTTP_REQUEST_PART2 "Connection: close\nContent-Length: "
#define SLAMANAGER_HTTP_REQUEST_PART3 "\r\n\r\n"

#define ADDUSER_SOAP_MSG_PART1 "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:createUser xmlns:ns2=\"http://sla_manager.moses.org/\"><username>"
#define ADDUSER_SOAP_MSG_PART2 "</username><name>"
#define ADDUSER_SOAP_MSG_PART3 "</name><surname>"
#define ADDUSER_SOAP_MSG_PART4 "</surname><password>"
#define ADDUSER_SOAP_MSG_PART5 "</password></ns2:createUser></S:Body></S:Envelope>"

#define ADDUSER_SOAP_RESP_PART1 "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:createUserResponse xmlns:ns2=\"http://sla_manager.moses.org/\"><return><name>"
#define ADDUSER_SOAP_RESP_PART2 "</name><password>"
#define ADDUSER_SOAP_RESP_PART3 "</password><surname>"
#define ADDUSER_SOAP_RESP_PART4 "</surname><username>"
#define ADDUSER_SOAP_RESP_PART5 "</username></return></ns2:createUserResponse></S:Body></S:Envelope>"

#define ADDSLA_SOAP_MSG_PART1 "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:createSlaAgreement xmlns:ns2=\"http://sla_manager.moses.org/\"><processName>"
#define ADDSLA_SOAP_MSG_PART2 "</processName><processClass>"
#define ADDSLA_SOAP_MSG_PART3 "</processClass><username>"
#define ADDSLA_SOAP_MSG_PART4 "</username><password>"
#define ADDSLA_SOAP_MSG_PART5 "</password><rate>"
#define ADDSLA_SOAP_MSG_PART6 "</rate><expireDate>"
#define ADDSLA_SOAP_MSG_PART7 "</expireDate></ns2:createSlaAgreement></S:Body></S:Envelope>"

#define ADDSLA_SOAP_RESP_TRUE "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:createSlaAgreementResponse xmlns:ns2=\"http://sla_manager.moses.org/\"><return>true</return></ns2:createSlaAgreementResponse></S:Body></S:Envelope>"
#define ADDSLA_SOAP_RESP_FALSE "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:createSlaAgreementResponse xmlns:ns2=\"http://sla_manager.moses.org/\"><return>false</return></ns2:createSlaAgreementResponse></S:Body></S:Envelope>"

#define DELSLA_SOAP_MSG_PART1 "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:deleteAgreement xmlns:ns2=\"http://sla_manager.moses.org/\"><processName>"
#define DELSLA_SOAP_MSG_PART2 "</processName><processClass>"
#define DELSLA_SOAP_MSG_PART3 "</processClass><username>"
#define DELSLA_SOAP_MSG_PART4 "</username><password>"
#define DELSLA_SOAP_MSG_PART5 "</password></ns2:deleteAgreement></S:Body></S:Envelope>"

#define DELSLA_SOAP_MSG_TRUE "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:deleteAgreementResponse xmlns:ns2=\"http://sla_manager.moses.org/\"><return>true</return></ns2:deleteAgreementResponse></S:Body></S:Envelope>"
#define DELSLA_SOAP_MSG_FALSE "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:deleteAgreementResponse xmlns:ns2=\"http://sla_manager.moses.org/\"><return>false</return></ns2:deleteAgreementResponse></S:Body></S:Envelope>"

#define DATE_LENGTH 19
//#define EXPIRE_TIME "T00:00:00"
/**
 * This function adds a user calling the SLAManager addUser web service
 * Returns 1 in case of success, 0 otherwise
 */
short add_user(struct user *user, char* address, short port);
short create_sla(struct user *user, struct sla *sla, char* address, short port);
short delete_sla(struct user *user, struct sla *sla, char* address, short port);
#endif	/* _SLAMANAGER_CLIENT_H */

