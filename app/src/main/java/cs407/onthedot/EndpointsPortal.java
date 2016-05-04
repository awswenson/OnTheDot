package cs407.onthedot;

import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;
import com.cs407.onthedot.onthedotbackend.tripApi.model.TripBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.List;

/**
 * Created by connerhuff on 5/3/16.
 */
public class EndpointsPortal  {

    final TripApi tripApiService;


    public EndpointsPortal() {
        // Production testing
        //TripApi.Builder builder = new TripApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);

        // Local testing
        TripApi.Builder builder = new TripApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });

        tripApiService = builder.build();

    }

    //changed this to bypass the whole localfiletaskrepo thing
    public synchronized void pushToRemote() {
        /*
        ArrayList<String> taskStrList = TaskIo.loadTasksStrFromFile(LocalFileTaskRepository.TODO_TXT_FILE);

        taskApiService.clearTasks().execute();

        long id = 1;
        for (String taskStr : taskStrList) {
            TaskBean taskBean = new TaskBean();
            taskBean.setData(taskStr);
            taskBean.setId(id++);
            taskApiService.storeTask(taskBean).execute();
        }

        //lastSync = new Date();

        */
        //TODO get all of the trips from the local,

        //and then put them into the remote database
        //for () {
            //reform the trips into TripBeans
            //tripApiService.storeTrip().execute();
        //}
        TripBean trip = new TripBean();
        trip.setId(new Long(1));
        trip.setData("Test data");
        //tripApiService.storeTrip(trip).execute();
        new PushRemoteTask().execute();
        Log.e("Endpoints put sent out", "Hooray");

    }


    public synchronized void pullFromRemote() {

        try {
            /*
            // Remote Call
            List<TaskBean> remoteTasks = taskApiService.getTasks().execute().getItems();

            if (remoteTasks != null) {
                ArrayList<Task> taskList = new ArrayList<Task>();
                for (TaskBean taskBean : remoteTasks) {
                    taskList.add(new Task(taskBean.getId(), taskBean.getData()));
                }
                store(taskList);
                reload();
                lastSync = new Date();
            }
            */
            List<TripBean> remoteTrips = tripApiService.getTrips().execute().getItems();
            for (TripBean trip : remoteTrips){
                Log.e("Endpoints pull success", trip.getData());
            }
        } catch (IOException e) {
            Log.e("Endpoint Exception pull", "Error when loading trips", e);
        }
    }


    //used for testing purposes to ensure backend is able to be communicated with
    public synchronized void sayHi() {

       // try {
            TripBean trip = new TripBean();
            trip.setId(new Long(1));
            trip.setData("Test data");
            new PullRemoteTask().execute();
            //taskApiService.sayHi("Jason").execute();
            /*
        } catch (IOException e) {
            Log.e("Endpoint sayHi", "Error when loading tasks", e);
        }
        */
    }
}
