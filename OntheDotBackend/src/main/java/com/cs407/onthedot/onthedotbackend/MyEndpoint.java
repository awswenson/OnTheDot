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
            tripEntity.setProperty("data", tripBean.getData());
            datastoreService.put(tripEntity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @ApiMethod(name = "getTrips")
    public List<TripBean> getTrips() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key tripBeanParentKey = KeyFactory.createKey("TripBeanParent", "todo.txt");
        Query query = new Query(tripBeanParentKey);
        //TODO use these two functions to provide queryability
        //query.setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, "2"));
        //query.setKeysOnly();
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
        ArrayList<TripBean> tripBeans = new ArrayList<TripBean>();
        for (Entity result : results) {
            TripBean tripBean = new TripBean();
            tripBean.setId(result.getKey().getId());
            tripBean.setData((String) result.getProperty("data"));
            tripBeans.add(tripBean);
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

    //used for testing purposes
    @ApiMethod(name = "sayHi")
    public TripBean sayHi(@Named("name") String name) {
        TripBean response = new TripBean();
        response.setData("Hi, " + name);
        return response;
    }

}
