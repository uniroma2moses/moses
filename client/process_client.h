/* 
 * File:   process_client.h
 * Author: stefano
 *
 * Created on May 24, 2010, 12:13 PM
 */

#ifndef _PROCESS_CLIENT_H
#define	_PROCESS_CLIENT_H

#ifdef	__cplusplus
extern "C" {
#endif




#ifdef	__cplusplus
}
#endif

#define PROCESS_HTTP_REQUEST_PART1 "POST /ESECCompositeApplicationService1/casaPort1 HTTP/1.1\nAccept: text/xml, text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2\nContent-Type: text/xml; charset=utf-8\nContent-Length: "
#define PROCESS_HTTP_REQUEST_PART2 "\nSOAPAction:\nCache-Control: no-cache\nPragma: no-cache\nUser-Agent: Java/1.6.0_20\nHost: "
#define PROCESS_HTTP_REQUEST_PART3 "\nConnection: close\r\n\r\n"

#define PROCESS_SOAP_MSG_PART1 "<soapenv:Envelope xsi:schemaLocation=\"http://schemas.xmlsoap.org/soap/envelope/ http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:esec=\"ESECCompositeApplication\" xmlns:esec1=\"http://xml.netbeans.org/schema/ESECProcess\" xmlns:mos=\"http://xml.netbeans.org/schema/MOSESSchema\"><soapenv:Body><esec:ESECProcessOperation soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><ESECPart><esec1:ioElement xsi:type=\"xsd:string\">ESECProcess</esec1:ioElement></ESECPart><MOSESPart><mos:processMetaData><processClass xsi:type=\"xsd:string\">"
#define PROCESS_SOAP_MSG_PART2 "</processClass><username xsi:type=\"xsd:string\">"
#define PROCESS_SOAP_MSG_PART3 "</username><password xsi:type=\"xsd:string\">"
#define PROCESS_SOAP_MSG_PART4 "</password></mos:processMetaData></MOSESPart></esec:ESECProcessOperation></soapenv:Body></soapenv:Envelope>"

#define PROCESS_HTTP_RESPONSE_PART1 "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns0=\"http://xml.netbeans.org/schema/ESECProcess\"><SOAP-ENV:Body SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><m:ESECProcessOperationResponse xmlns:m=\"ESECCompositeApplication\"><part1 xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ioElement\"><ioElement xmlns=\"http://xml.netbeans.org/schema/ESECProcess\">"
#define PROCESS_HTTP_RESPONSE_PART2 "</ioElement></part1></m:ESECProcessOperationResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>"

float invoke_process(struct user *user, struct sla *sla, char* address, short port);
#endif	/* _PROCESS_CLIENT_H */

