/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.wsmonitor.client;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


/**
 *
 * @author francesco
 */
public class RemoteServiceClient {

    public static boolean test(String endpointURL, String operationName) {
        boolean result = false;
        int status;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(endpointURL + "/" + operationName);
            HttpResponse res = httpclient.execute(httpget);
            status = res.getStatusLine().getStatusCode();
            if (status == 200)
               result = true;
        } catch (IOException ex) {
        }
        return result;
    }
}
