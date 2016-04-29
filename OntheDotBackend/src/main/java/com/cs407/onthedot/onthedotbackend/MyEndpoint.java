/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.cs407.onthedot.onthedotbackend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.Transaction;
import com.googlecode.objectify.ObjectifyService;

import java.util.ArrayList;
import java.util.List;

/** An endpoint class we are exposing */
@Api(
  name = "myApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "onthedotbackend.onthedot.cs407.com",
    ownerName = "onthedotbackend.onthedot.cs407.com",
    packagePath=""
  )
)
public class MyEndpoint {

    /** A simple endpoint method that takes a name and says Hi back */

    @ApiMethod(name = "storeTask")
    public void storeTask(TaskBean taskBean) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            //parent, kind, name
            Key taskBeanParentKey = KeyFactory.createKey("TaskBeanParent", "OnTheDot");
            Entity taskEntity = new Entity("TaskBean", taskBean.getId(), taskBeanParentKey);
            taskEntity.setProperty("data", taskBean.getData());
            datastoreService.put(taskEntity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @ApiMethod(name = "getTasks")
    public List<TaskBean> getTasks() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key taskBeanParentKey = KeyFactory.createKey("TaskBeanParent", "OnTheDot");
        Query query = new Query(taskBeanParentKey);
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

        ArrayList<TaskBean> taskBeans = new ArrayList<TaskBean>();
        for (Entity result : results) {
            TaskBean taskBean = new TaskBean();
            taskBean.setId(result.getKey().getId());
            taskBean.setData((String) result.getProperty("data"));
            taskBeans.add(taskBean);
        }

        return taskBeans;
    }

    @ApiMethod(name = "clearTasks")
    public void clearTasks() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key taskBeanParentKey = KeyFactory.createKey("TaskBeanParent", "OnTheDot");
            Query query = new Query(taskBeanParentKey);
            List<Entity> results = datastoreService.prepare(query)
                    .asList(FetchOptions.Builder.withDefaults());
            for (Entity result : results) {
                datastoreService.delete(result.getKey());
            }
            txn.commit();
        } finally {
            if (txn.isActive()) { txn.rollback(); }
        }
    }


    /**
     * This inserts a new <code>Quote</code> object.
     * @param quote The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertBean")
    public TaskBean insertBean(TaskBean bean) throws ConflictException {
        //If if is not null, then check if it exists. If yes, throw an Exception
        //that it is already present
        if (bean.getId() != null) {
            if (findRecord(bean.getId()) != null) {
                throw new ConflictException("Object already exists");
            }
        }
        //Since our @Id field is a Long, Objectify will generate a unique value for us
        //when we use put
        OfyService.ofy().save().entity(bean).now();
        return bean;
    }


    /*
    @ApiMethod(name = "listQuote")
    public CollectionResponse<TaskBean> listQuote(@Nullable @Named("cursor") String cursorString,
                                               @Nullable @Named("count") Integer count) {

        Query<TaskBean> query = OfyService.ofy().load().type(TaskBean.class);
        //if (count != null) query.limit(count);
        if (cursorString != null && cursorString != "") {
            query = query.startAt(Cursor.fromWebSafeString(cursorString));
        }

        List<TaskBean> records = new ArrayList<TaskBean>();
        QueryResultIterator<TaskBean> iterator = query.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            records.add(iterator.next());
            if (count != null) {
                num++;
                if (num == count) break;
            }
        }

        //Find the next cursor
        if (cursorString != null && cursorString != "") {
            Cursor cursor = iterator.getCursor();
            if (cursor != null) {
                cursorString = cursor.toWebSafeString();
            }
        }
        return CollectionResponse.<TaskBean>builder().setItems(records).setNextPageToken(cursorString).build();
    }
    */

    private TaskBean findRecord(Long id) {
        return OfyService.ofy().load().type(TaskBean.class).id(id).now();
        //or return ofy().load().type(Quote.class).filter("id",id).first.now();
    }




}
