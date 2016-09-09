/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.gameontext.regroom.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gameontext.regroom.Config;
import org.gameontext.regroom.Log;
import org.gameontext.regroom.models.Registration;

import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Class to talk to the registration service
 */
public class RegistrationServiceClient {
    @Inject
    private Config config;
    
    public String registerRoom(Registration reg) throws WebApplicationException {

        try {
            HttpClient client = HttpClientBuilder.create().build();
    
            HttpPost hg = new HttpPost(config.getRegistrationsUrl());
            
            ObjectMapper om = new ObjectMapper();
            String data = om.writeValueAsString(reg);
            StringEntity entity = new StringEntity(data);
            entity.setContentType("application/json");
            hg.setEntity(entity);
            Log.log(Level.FINEST, this, "Building web target: {0}", hg.getURI().toString());
    
            HttpResponse r = client.execute(hg);
            switch(r.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK:
                    return "Room regsitered successfully";
                case HttpStatus.SC_CONFLICT:
                    return "This room has already been regsitered for this event";
                default :
                    Log.log(Level.WARNING, this, "Failed to register room", r.getStatusLine());
                    return "Unable to register room";
            }

        } catch (HttpResponseException hre) {
            Log.log(Level.FINEST, this, "Error communicating with registration service: {0} {1}", hre.getStatusCode(), hre.getMessage());
            throw new WebApplicationException("Error communicating with registration service", Response.Status.INTERNAL_SERVER_ERROR);
        } catch (WebApplicationException wae) {
            Log.log(Level.FINEST, this, "Error processing response: {0}", wae.getResponse());
            throw wae;
        } catch ( IOException e ) {
            Log.log(Level.FINEST, this, "Unexpected exception from registration service: {0}", e);
            e.printStackTrace();
            throw new WebApplicationException("Error communicating with registration service", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    public List<Registration> getRoomsForEvent() throws WebApplicationException {

        try {
            HttpClient client = HttpClientBuilder.create().build();
    
            HttpGet hg = new HttpGet(config.getRegistrationsUrl());
            Log.log(Level.FINEST, this, "Building web target: {0}", hg.getURI().toString());
    
            HttpResponse r = client.execute(hg);
            switch(r.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK:
                    ObjectMapper om = new ObjectMapper();
                    Registration[] allrooms = om.readValue(r.getEntity().getContent(), Registration[].class);
                    List<Registration> registrations = new ArrayList<>();
                    for(Registration reg : allrooms) {
                        if(reg.getEventId().equals(config.getName())) {
                            registrations.add(reg);
                        }
                    }
                    return registrations;
                default :
                    Log.log(Level.WARNING, this, "Failed to get list of regsitered rooms from " + config.getRegistrationsUrl(), r.getStatusLine());
                    return Collections.emptyList();
            }
        } catch (HttpResponseException hre) {
            Log.log(Level.FINEST, this, "Error communicating with registration service: {0} {1}", hre.getStatusCode(), hre.getMessage());
            throw new WebApplicationException("Error communicating with IoT board service", Response.Status.INTERNAL_SERVER_ERROR);
        } catch (WebApplicationException wae) {
            Log.log(Level.FINEST, this, "Error processing response: {0}", wae.getResponse());
            throw wae;
        } catch ( IOException e ) {
            Log.log(Level.FINEST, this, "Unexpected exception from registration service: {0}", e);
            e.printStackTrace();
            throw new WebApplicationException("Error communicating with registration service", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
