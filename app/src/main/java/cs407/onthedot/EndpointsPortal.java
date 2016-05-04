package cs407.onthedot;

import android.util.Log;

import com.cs407.onthedot.onthedotbackend.taskApi.TaskApi;
import com.cs407.onthedot.onthedotbackend.taskApi.model.TaskBean;
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

    final TaskApi taskApiService;

    //public EndpointsTaskBagImpl(TodoPreferences preferences, LocalTaskRepository localRepository) {
    public EndpointsPortal() {
        //super(preferences, localRepository, null);

        // Production testing
        //TaskApi.Builder builder = new TaskApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);

        // Local testing
        TaskApi.Builder builder = new TaskApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });

        taskApiService = builder.build();

    }

    //changed this to bypass the whole localfiletaskrepo thing
    public synchronized void pushToRemote() {
        try {
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
            //TODO get all of the tasks from the local,

            //and then put them into the remote database
            //for () {
                //reform the trips into TripBeans
                //taskApiService.storeTask().execute();
            //}
            TaskBean task = new TaskBean();
            task.setId(new Long(1));
            task.setData("Test data");
            taskApiService.storeTask(task).execute();
            Log.e("Endpoints put sent out", "Hooray");


        } catch (IOException e) {
            //Log.e(EndpointsTaskBagImpl.class.getSimpleName(), "Error when storing tasks", e);
            Log.e("Endpoint Exception", "Error when storing tasks", e);
        }
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
            List<TaskBean> remoteTasks = taskApiService.getTasks().execute().getItems();
            for (TaskBean task : remoteTasks){
                Log.e("Endpoints pull success", task.getData());
            }
        } catch (IOException e) {
            Log.e("Endpoint Exception pull", "Error when loading tasks", e);
        }
    }


    public synchronized void sayHi() {

       // try {
            TaskBean task = new TaskBean();
            task.setId(new Long(1));
            task.setData("Test data");
            new PullRemoteTask().execute();
            //taskApiService.sayHi("Jason").execute();
            /*
        } catch (IOException e) {
            Log.e("Endpoint sayHi", "Error when loading tasks", e);
        }
        */
    }
}
