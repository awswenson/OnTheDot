/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.cs407.onthedot.onthedotbackend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(
  name = "tripApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "onthedotbackend.onthedot.cs407.com",
    ownerName = "onthedotbackend.onthedot.cs407.com",
    packagePath=""
  )
)
public class MyEndpoint {

    @ApiMethod(name = "storeTrip")
    public void storeTrip(TripBean tripBean) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key tripBeanParentKey = KeyFactory.createKey("TripBeanParent", "todo.txt");
            Entity tripEntity = new Entity("TripBean", tripBean.getId(), tripBeanParentKey);
            //new Entity()
            tripEntity.setProperty("id", tripBean.getId());
            tripEntity.setProperty("date", tripBean.getDate());
            tripEntity.setProperty("destLat", tripBean.getDestLat());
            tripEntity.setProperty("destLong", tripBean.getDestLong());
            tripEntity.setProperty("startLat", tripBean.getStartLat());
            tripEntity.setProperty("startLong", tripBean.getStartLong());
            tripEntity.setProperty("tripComplete", tripBean.getTripComplete());
            tripEntity.setProperty("friendsList", tripBean.getFriendsList());
            datastoreService.put(tripEntity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @ApiMethod(name = "getTrips")
    public List<TripBean> getTrips(@Named("id") String facebookId) {
        //first get all the participants associated with the given facebook id
        List<ParticipantBean> participantList =  getParticipants(facebookId);
        //put the entries into a hash table for efficiency
        HashSet<Long> map = new HashSet<Long>();
        for (ParticipantBean bean : participantList){
            map.add(bean.getTripId());
        }

        //finish the remainder of the query but only get the trips that match the trip id's returned
        //by the first query.
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key tripBeanParentKey = KeyFactory.createKey("TripBeanParent", "todo.txt");
        Query query = new Query(tripBeanParentKey);
        //TODO use these two functions to provide queryability
        //query.setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, "2"));
        //query.setKeysOnly();
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
        ArrayList<TripBean> tripBeans = new ArrayList<TripBean>();
        for (Entity result : results) {
            if (map.contains(result.getKey().getId())){
                TripBean tripBean = new TripBean();
                tripBean.setId(result.getKey().getId());
                tripBean.setDate((String) result.getProperty("date"));
                tripBean.setDestLat((Double) result.getProperty("destLat"));
                tripBean.setDestLong((Double) result.getProperty("destLong"));
                tripBean.setStartLat((Double) result.getProperty("startLat"));
                tripBean.setStartLong((Double) result.getProperty("startLong"));
                tripBean.setTripComplete((Boolean) result.getProperty("tripComplete"));
                tripBean.setFriendsList((String) result.getProperty("friendsList"));
                tripBeans.add(tripBean);
            }
        }

        return tripBeans;
    }

    @ApiMethod(name = "clearTripsById")
    public void clearTripsById(@Named("id") Long id) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key tripBeanParentKey = KeyFactory.createKey("TripBeanParent", "todo.txt");
            Query query = new Query(tripBeanParentKey);
            List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
            for (Entity result : results) {
                Key key = result.getKey();
                Long resultId = key.getId();
                if (resultId == id){
                    datastoreService.delete(result.getKey());
                }
            }
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }





    //Start the participant portion of our API
    @ApiMethod(name = "storeParticipant")
    public void storeParticipant(ParticipantBean partBean) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key partBeanParentKey = KeyFactory.createKey("ParticipantBeanParent", "todo.txt");
            //Entity tripEntity = new Entity("ParticipantBean", partBean.getId(), partBeanParentKey);
            Entity partEntity = new Entity("ParticipantBean", partBeanParentKey);
            //new Entity()
            partEntity.setProperty("participantId", partBean.getParticipantId());
            partEntity.setProperty("tripId", partBean.getTripId());
            partEntity.setProperty("participantName", partBean.getParticipantName());
            datastoreService.put(partEntity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    //returns the participant entries that match the users facebookId
    @ApiMethod(name = "getParticipants")
    public List<ParticipantBean> getParticipants(@Named("facebookId") String facebookId) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key partBeanParentKey = KeyFactory.createKey("ParticipantBeanParent", "todo.txt");
        Query query = new Query(partBeanParentKey);
        //TODO use these two functions to provide queryability
        //query.setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, "2"));
        //query.setKeysOnly();
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
        ArrayList<ParticipantBean> partBeans = new ArrayList<ParticipantBean>();
        for (Entity result : results) {
            if (facebookId.equals(result.getProperty("participantId"))) {
                ParticipantBean partBean = new ParticipantBean();
                partBean.setParticipantId((String) result.getProperty("participantId"));
                partBean.setTripId((Long) result.getProperty("tripId"));
                partBean.setParticipantName((String) result.getProperty("participantName"));
                partBeans.add(partBean);
            }
        }

        return partBeans;
    }

    @ApiMethod(name = "clearParticipantByPartAndTripId")
    public void clearParticipantByPartAndTripId(@Named("partId") String partId, @Named("tripId") Long tripId) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key partBeanParentKey = KeyFactory.createKey("ParticipantBeanParent", "todo.txt");
            Query query = new Query(partBeanParentKey);
            List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
            for (Entity result : results) {
                Long partIdResult = (Long) result.getProperty("participantId");
                Long tripIdResult = (Long) result.getProperty("tripId");
                if (tripIdResult.compareTo(tripId) == 0 && partIdResult.equals(partId)) {
                    datastoreService.delete(result.getKey());
                }
            }
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

}
